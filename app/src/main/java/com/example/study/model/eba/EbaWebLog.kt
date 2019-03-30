package com.example.study.model.eba

class EbaWebLog(
    val _id: Int =0,
    val startTime: Int =0,
    val endTime: Int =0,
    val watchTime: Int =0) {
    constructor(
        startTime: Int,
        endTime: Int,
        watchTime: Int
    ) : this(
        0,
        startTime,
        endTime,
        watchTime
    )
}