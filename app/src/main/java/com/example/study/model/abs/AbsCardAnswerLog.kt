package com.example.study.model.abs

import java.io.Serializable

class AbsCardAnswerLog(
    val _id: Int =0,
    val blockNo: String ="",
    val itemNo: Int =0,
    val seqNo: Int =0,
    val result: Int? =null,
    val startTime: String? =null,
    val endTime: String? =null,
    val elapsedTime: Int? =null
): Serializable {
    constructor(
        blockNo: String,
        itemNo: Int,
        seqNo: Int,
        result: Int?,
        startTime: String?,
        endTime: String?,
        elapsedTime: Int?
    ) : this(
        0,
        blockNo,
        itemNo,
        seqNo,
        result,
        startTime,
        endTime,
        elapsedTime
    )
}