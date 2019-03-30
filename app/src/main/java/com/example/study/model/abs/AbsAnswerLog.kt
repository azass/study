package com.example.study.model.abs

import java.io.Serializable

class AbsAnswerLog(
    val _id: Int =0,
    val blockNo: String ="",
    val itemNo: Int =0,
    val accuracyRate: Int =0,
    val startTime: String ="",
    val endTime: String ="",
    val elapsedTime: Int =0
): Serializable {
    constructor(
        blockNo: String,
        itemNo: Int,
        accuracyRate: Int,
        startTime: String,
        endTime: String,
        elapsedTime: Int
    ) : this(
        0,
        blockNo,
        itemNo,
        accuracyRate,
        startTime,
        endTime,
        elapsedTime
    )
}