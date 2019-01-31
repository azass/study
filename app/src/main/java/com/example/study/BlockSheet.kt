package com.example.study

import java.io.Serializable

data class BlockSheet(val blockNo: String, val title: String, val itemList: MutableList<BlockItem>) : Serializable