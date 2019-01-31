package com.example.study

import java.io.Serializable

class BlockSheetManager(val blockMenuList: List<BlockMenu>) : Serializable {

    val blockSheetTable = mutableMapOf<String, BlockSheet>()
    fun setupBlockSheetTable() : MutableMap<String, BlockSheet> {

        blockMenuList.forEach{
            it.blocksheetList.forEach {
                blockSheetTable.put(it.blockNo, it)
            }
        }
        return blockSheetTable
    }

    fun getBlockItem(blockNo: String, itemNo: Int): BlockItem {
        val blockSheet = blockSheetTable[blockNo]
        val blockItem = blockSheet?.itemList!![itemNo - 1]
        blockItem.blockNo = blockNo
        blockItem.sheetTitle = blockSheet.title
        return blockItem
    }
}