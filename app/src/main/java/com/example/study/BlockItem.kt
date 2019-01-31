package com.example.study

import java.io.Serializable

class BlockItem(val itemNo: String, val subtitle: String, val contents: List<BlockContent>) : Serializable {
    var sheetTitle : String = ""
        set(sheetTitle: String) {
            field = sheetTitle
        }
    var blockNo: String = ""
        set(blockNo: String) {
            field = blockNo
        }
}