package com.example.study

import java.io.Serializable

data class BlockMenu(val blockMenuNo: String, val title: String, val blocksheetList: List<BlockSheet>) : Serializable