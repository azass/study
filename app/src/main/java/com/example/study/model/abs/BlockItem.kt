package com.example.study.model.abs

import java.io.Serializable

class BlockItem(
    val _id: Int =0,
    var blockNo: String ="",
    val itemNo: Int =0,
    val subtitle: String ="",
    var contents: List<BlockCard>? = mutableListOf()) : Serializable {

    var blockSheet: BlockSheet? = null
    var cardAnswerLogs: MutableList<AbsCardAnswerLog> = mutableListOf<AbsCardAnswerLog>()

    constructor(
        blockNo: String,
        itemNo: Int,
        subtitle: String,
        contents: List<BlockCard>?
    ) : this(
        0,
        blockNo,
        itemNo,
        subtitle,
        contents
    )
}