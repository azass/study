package com.example.study.model.abs

import java.util.*

class AbsRecentResult(
    val _id: Int =0,
    val blockNo: String ="",
    val itemNo: Int =0,
    var correctTimes: Int =0,
    var status: Int =0,
    var answerResult: Int =0,
    var beginTime: Long =0L,
    var updateTime: Long =0L
) {
    constructor(
        blockNo: String,
        itemNo: Int,
        correctTimes: Int,
        status: Int,
        answerResult: Int,
        beginTime: Long,
        updateTime: Long
    ) : this(
        0,
        blockNo,
         itemNo,
         correctTimes,
         status,
         answerResult,
         beginTime,
         updateTime
    )
    override fun toString(): String {
        val str = ""
        val beginDate = Date(beginTime)
        val updateDate = Date(updateTime)
        println(blockNo + "," + itemNo + "," + correctTimes + "," + status + "," + answerResult + "," + beginDate + "," + updateDate)
        return str
    }
}