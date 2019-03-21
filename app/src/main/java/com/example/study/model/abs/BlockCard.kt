package com.example.study.model.abs

import java.io.Serializable

class BlockCard(
    val _id: Int,
    var blockNo: String,
    var itemNo: Int,
    var seq: Int,
    var Q: String,
    var hint: String?) :
    Serializable {
    constructor(
        blockNo: String,
        itemNo: Int,
        seq: Int,
        Q: String,
        hint: String?
    ) : this(
        0,
        blockNo,
        itemNo,
        seq,
        Q,
        hint
    )
}