package com.example.study

import java.text.SimpleDateFormat
import java.util.*

object StudyUtils {

    const val BORDER_TIME_1: Long = 24L * 3600000L
    const val BORDER_TIME_2: Long = 168L * 3600000L
    const val BORDER_TIME_3: Long = 336L * 3600000L
    const val BORDER_TIME_4: Long = 720L * 3600000L

//    const val BORDER_TIME_1: Long = 5 * 60000
//    const val BORDER_TIME_2: Long = 10 * 60000
//    const val BORDER_TIME_3: Long = 20 * 60000

    const val INITIAL_STATUS = 0
    const val ONE_DAY_STATUS = 1
    const val ONE_WEEK_STATUS = 2
    const val TWO_WEEK_STATUS = 3
    const val ONE_MONTH_STATUS = 4
    const val COMPLETE_STATUS = 5

    fun getStatusLabel(status: Int): String {
        when(status) {
            INITIAL_STATUS -> return "未"
            ONE_DAY_STATUS -> return "1日"
            ONE_WEEK_STATUS -> return "1週"
            TWO_WEEK_STATUS -> return "2週"
            ONE_MONTH_STATUS -> return "1月"
            COMPLETE_STATUS -> return "完"
            else  -> return ""
        }
    }

    fun outOfTerm(status: Int, elapsedTime: Long): Boolean {
        if (status == ONE_DAY_STATUS && elapsedTime >= BORDER_TIME_1) {
            return true
        } else if (status == ONE_WEEK_STATUS && elapsedTime >= BORDER_TIME_2) {
            return true
        } else if (status == TWO_WEEK_STATUS && elapsedTime >= BORDER_TIME_3) {
            return true
        } else if (status == ONE_MONTH_STATUS && elapsedTime >= BORDER_TIME_4) {
            return true
        } else {
            return false
        }
    }

    fun openAnswer(txt: String) : String {
        return txt.replace("<@>", "<span style='color: black; background-color: white;'>")
            .replace("</@>", "</span>")
    }

    fun showHilight(txt: String): String {
        return txt.replace("<@>", "<span style='color: red; background-color: red;'>")
            .replace("</@>", "</span>")
    }

    fun highlight(txt: String, selectionStart: Int, selectionEnd: Int): String {
        var tmptxt = txt.replace("</@>", "<@>")
//        if (tmptxt.startsWith("<@>")) {
//            tmptxt = tmptxt.substring(3)
//        }
//        if (tmptxt.endsWith("<@>")) {
//            tmptxt = tmptxt.substring(0, tmptxt.length - 3)
//        }

        val str = tmptxt.split("<@>")
        val len = mutableListOf<Int>()
        var cum = 0
        str.forEach {
            cum = cum + it.length
            len.add(cum)
        }
        val startIndex = getIndex(selectionStart, 0, len, false)
        val endIndex = getIndex(selectionEnd, 0, len, true)

        var start0 = 0

        if (startIndex == endIndex) {
            start0 = if (startIndex == 0) 0 else len[startIndex - 1]
        } else {
            start0 = if (startIndex == 0 || startIndex == 1) 0 else len[startIndex - 1 - startIndex % 2]
        }
        val startI = selectionStart - start0
        val endI = selectionEnd - start0

        var newStr = ""
        val temp = mutableListOf<String>()
        for (i in len.indices) {
            if (i == startIndex || (isOdd(startIndex) && i == startIndex - 1)
                || (isOdd(endIndex) && i == endIndex)
                || (startIndex != endIndex && i > startIndex && i <= endIndex)) {

                newStr = newStr + str[i]
                if (i == endIndex + endIndex % 2) {
                    temp.add(addTag(newStr, startI, endI))
                    newStr = ""
                }
            } else {
                temp.add(str[i])
                newStr = ""
            }
        }

        var newTxt = ""
        for ((i, value) in temp.withIndex()) {
            if (isOdd(i)) {
                newTxt = newTxt + "<@>" + value + "</@>"
            } else {
                newTxt = newTxt + value
            }
        }
        return newTxt
    }

    fun addTag(str: String, startI: Int, endI: Int):String {
        val buffer = StringBuffer()
        buffer.append(str.substring(0, startI)).append("<@>").append(str.substring(startI, endI)).append("</@>").append(str.substring(endI))
        return buffer.toString()
    }

    fun getIndex(selection: Int, i: Int, len: List<Int>, isEnd: Boolean): Int {
        if (selection < len[i] || (isEnd && selection == len[i])) {
            return i
        } else {
            return getIndex(selection, i+1, len, isEnd)
        }
    }

    fun isOdd(i: Int): Boolean {
        return i%2 == 1
    }

    fun replaceHighlight(txt: String): String {
        return txt.replace("<span style='color: red; background-color: red;'>", "<@>")
            .replace("</span>", "</@>")
    }

    fun getDaysLeft(): String {
        // 現在の日付の取得
        val nowDate = Date()
        // 試験日
        val examDate = SimpleDateFormat("yyyy/MM/dd").parse("2019/10/20")

        val daysLeft = ((examDate.time - nowDate.time)/(24*60*60*1000)).toInt()

        return daysLeft.toString()
    }
}

