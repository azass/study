package com.example.study.model.abs

import java.io.Serializable

data class AbstractionBlock(
    val _id: Int,
    val absNo: String,
    val title: String,
    var blocksheetList: List<BlockSheet>?) : Serializable {
    constructor(
        absNo: String,
        title: String,
        blocksheetList: List<BlockSheet>?
    ) : this (
        0,
        absNo,
        title,
        blocksheetList
    )
}