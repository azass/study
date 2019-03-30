package com.example.study.model.eba

class EbaTotalWebLog(
    val _id: Int =0,
    var lastExecDate: String? =null,
    var watchTime: Int =0,
    var days:Int =0) {
    constructor(
        lastExecDate: String?,
        watchTime: Int,
        days:Int
    ) : this(
        0,
        lastExecDate,
        watchTime,
        days
    )
}