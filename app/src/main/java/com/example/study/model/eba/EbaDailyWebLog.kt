package com.example.study.model.eba

class EbaDailyWebLog(
    val _id: Int =0,
    val execDate: String ="",
    var watchTime: Int =0) {
    constructor(
        execDate: String,
        watchTime: Int
    ) : this(
        0,
        execDate,
        watchTime
    )
}