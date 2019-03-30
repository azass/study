package com.example.study
import java.text.SimpleDateFormat
import java.util.*
object DateUtils {

    fun nowDate(): Date {
        return Date()
    }

    fun getNowDateLabel(): String {
        return getDateLabel(nowDate())
    }

    fun getYesterdayLabel(): String {
        return getDateLabel(-1)
    }

    fun getDateLabel(diff: Int): String {
        return getDateLabel(getDate(diff))
    }

    fun getDate(diff: Int): Date {
        val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"), Locale.JAPAN);
        calendar.add(Calendar.DATE, diff)
        return calendar.time
    }

    fun getDate(dateText: String): Date {
        return SimpleDateFormat("yyyy/MM/dd").parse(dateText)
    }

    fun getDateLabel(date: Date): String {
        return SimpleDateFormat("yyyy/MM/dd").format(date)
    }

    fun getDateLabelFromTimestamp(timestamp: Long): String {
        val calendar: Calendar = getCalendar()
        calendar.timeInMillis = timestamp
        return getDateLabel(calendar.time)
    }

    fun getTimestamp(): CharSequence {
        return android.text.format.DateFormat.format("yyyy-MM-dd-hh-mm-ss", Date())
    }

    fun getCalendar(): Calendar {
        return Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"), Locale.JAPAN)
    }

    fun formatTime(millisec: Int): String {
        val dataFormat = SimpleDateFormat("mm:ss", Locale.US)
        return dataFormat.format(millisec)
    }

    fun formatDateAndTime(millisec: Long): String {
        val dataFormat = SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.JAPAN)
        return dataFormat.format(millisec)
    }

    fun formatDateAndTime(str: String): String {
        try {
            return DateUtils.formatDateAndTime(str.toLong())
        } catch(e: NumberFormatException) {
            return str
        }
    }
}