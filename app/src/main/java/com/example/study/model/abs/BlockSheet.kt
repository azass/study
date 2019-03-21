package com.example.study.model.abs

import java.io.Serializable

data class BlockSheet(
    val _id: Int,
    val blockNo: String,
    val title: String,
    var itemList: List<BlockItem>?) : Serializable {

    var absNo = ""
    var blockItemTable: MutableMap<String, BlockItem>
    init {
        blockItemTable = mutableMapOf<String, BlockItem>()

    }
    constructor(absNo: Int, blockNo: String, title: String): this(0, blockNo, title, null)
}
