package com.example.study.model.abs

import java.io.Serializable

class BlockItem(
    val _id: Int,
    var blockNo: String,
    val itemNo: Int,
    val subtitle: String,
    var contents: List<BlockCard>?) : Serializable {

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