package com.example.study.model.abs

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.study.BLOCK_ITEM_STATUS
import com.example.study.MENU_STATUS
import com.example.study.StudyUtils
import com.example.study.StudyUtils.COMPLETE_STATUS
import com.example.study.persistence.BlockStudyDatabase
import com.example.study.persistence.abs.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import java.io.Serializable

class BlockSheetManager() : Serializable {
    val TABLE_NAME = "AbstractionBlock"

    var abstractionBlockList = mutableListOf<AbstractionBlock>()

    val blockSheetTable = mutableMapOf<String, BlockSheet>()
    val blockItemTable = mutableMapOf<String, MutableMap<String, BlockItem>>()

    var status: Int = MENU_STATUS
    var previouStatus: Int = MENU_STATUS

    var selectedAbstractionBlockIndex: Int? = null
    var selectedAbstractionBlock: AbstractionBlock? = null
    var selectedBlockSheet: BlockSheet? = null
    var selectedBlockSheetIndex: Int? = null
//    var selectedBlockItem: BlockItem? = null

    var blockItemList: List<BlockItem>? = null
    var selectedBlockItemListIndex: Int? = null

    fun setup() {
//        abstractionBlockList = mutableListOf<AbstractionBlock>()
        val database = FirebaseDatabase.getInstance().reference
        database.child(TABLE_NAME).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach {
                    val abstractionBlock = it.getValue<AbstractionBlock>(AbstractionBlock::class.java)!!

                    abstractionBlock.blocksheetList!!.forEach { blockSheet ->
                        blockSheetTable.put(blockSheet.blockNo, blockSheet)
                        blockItemTable.put(blockSheet.blockNo, mutableMapOf())
                        blockSheet.itemList!!.forEach { blockItem ->
                            // 双方向関連
                            blockItem.blockSheet = blockSheet
                            blockItemTable[blockSheet.blockNo]!!.put(blockItem.itemNo.toString(), blockItem)
                            blockItem.contents!!.forEachIndexed { index, blockCard ->
                                blockCard.seq = index
                            }
                        }
                    }

                    abstractionBlockList.add(abstractionBlock)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    fun setup(context: Context) {
//        abstractionBlockList = mutableListOf()
        val db = BlockStudyDatabase(context).readableDatabase
        val abstractionBlockDao = AbstractionBlockDao(db)
        val blockSheetDao = BlockSheetDao(db)
        val blockItemDao = BlockItemDao(db)
        val blockCardDao = BlockCardDao(db)
//        abstractionBlockList = abstractionBlockDao.selectAll()
        abstractionBlockList!!.forEach {
            val abstractionBlock = it
            val blockSheetList = blockSheetDao.find("absNo = ?", arrayOf(it.absNo), "blockNo")
            abstractionBlock.blocksheetList = blockSheetList
            blockSheetList.forEach {
                val blockSheet = it
                val itemList = blockItemDao.find("blockNo = ?", arrayOf(blockSheet.blockNo), "itemNo")
                blockSheet.itemList = itemList
                itemList.forEach {
                    val blockItem = it
                    val contents = blockCardDao.find(
                        "blockNo = ? and itemNo = ?",
                        arrayOf(blockSheet.blockNo, blockItem.itemNo.toString()),
                        "seq"
                    )
                    blockItem.contents = contents
                    blockItem.blockSheet = blockSheet
                    blockSheet.blockItemTable.put(blockItem.itemNo.toString(), blockItem)
                }
                blockSheetTable.put(blockSheet.blockNo, blockSheet)
            }
        }
        db.close()
    }

    fun exportJson(db: SQLiteDatabase): String {
        var list = mutableListOf<Map<String, Any>>()
        val abstractionBlockDao = AbstractionBlockDao(db)
        val blockSheetDao = BlockSheetDao(db)
        val blockItemDao = BlockItemDao(db)
        val blockCardDao = BlockCardDao(db)
//        abstractionBlockList = abstractionBlockDao.selectAll()
        abstractionBlockList!!.forEach {
            //            val abstractionBlock = it
            val blockSheetList = blockSheetDao.find("absNo = ?", arrayOf(it.absNo), "blockNo")
//            abstractionBlock.blocksheetList = blockSheetList
            var abstractionBlock = mutableMapOf<String, Any>()
            abstractionBlock.put("absNo", it.absNo)
            abstractionBlock.put("title", it.title)
            abstractionBlock.put("blocksheetList", mutableListOf<Map<String, Any>>())
            list.add(abstractionBlock)

            blockSheetList.forEach {
                //                val blockSheet = it
                val itemList = blockItemDao.find("blockNo = ?", arrayOf(it.blockNo), "itemNo")
//                blockSheet.itemList = itemList
                var blockSheet = mutableMapOf<String, Any>()
                blockSheet.put("blockNo", it.blockNo)
                blockSheet.put("title", it.title)
                blockSheet.put("itemList", mutableListOf<Map<String, Any>>())
                (abstractionBlock["blocksheetList"] as MutableList<Map<String, Any>>).add(blockSheet)

                itemList.forEach {
                    //                    val blockItem = it
                    val contents = blockCardDao.find(
                        "blockNo = ? and itemNo = ?",
                        arrayOf(blockSheet["blockNo"].toString(), it.itemNo.toString()),
                        "seq"
                    )
//                    blockItem.contents = contents
//                    blockItem.blockSheet = blockSheet
                    var blockItem = mutableMapOf<String, Any>()
                    blockItem.put("itemNo", it.itemNo)
                    blockItem.put("subtitle", it.subtitle)
                    blockItem.put("contents", mutableListOf<Map<String, String?>>())
                    (blockSheet["itemList"] as MutableList<Map<String, Any>>).add(blockItem)

                    contents.forEach {
                        var blockCard = mapOf("Q" to it.Q, "hint" to it.hint)
                        (blockItem["contents"] as MutableList<Map<String, String?>>).add(blockCard)
                    }
                }
            }
        }
        val json = Gson().newBuilder().disableHtmlEscaping().create().toJson(list)
        return json
    }

    fun setupBlockSheetTable(): MutableMap<String, BlockSheet> {

        abstractionBlockList!!.forEach {

            it.blocksheetList!!.forEach {
                blockSheetTable.put(it.blockNo, it)
                val blockSheet = it
                blockSheet.blockItemTable = mutableMapOf<String, BlockItem>()
                blockSheet.itemList!!.forEach {
                    var blockItem = it
                    blockItem.blockSheet = blockSheet
                    blockSheet.blockItemTable.put(blockItem.itemNo.toString(), blockItem)
                }
            }
        }
        return blockSheetTable
    }

    fun getSelectedBlockItem(): BlockItem {
        return blockItemList!![selectedBlockItemListIndex!!]
    }

    fun getBlockItem(blockNo: String, itemNo: Int): BlockItem {
//        val blockSheet = blockSheetTable[blockNo]!!
//        val blockItem = blockSheet.blockItemTable[itemNo.toString()]!!
        return blockItemTable[blockNo]!!.get(itemNo.toString())!!
    }

    fun changeStatus(status: Int) {
        if (this.status == BLOCK_ITEM_STATUS && status == BLOCK_ITEM_STATUS) {
            this.status = status
        } else {
            previouStatus = this.status
            this.status = status
        }
    }

    fun init() {
        status = 0
        previouStatus = 0
        selectedAbstractionBlock = null
        selectedBlockSheet = null
//        selectedBlockItem = null
    }

    fun getSelectedBlockItemList(): List<BlockItem> {
        blockItemList = selectedBlockSheet!!.itemList
        return blockItemList!!
    }

    fun getForgettingList(context: Context): List<BlockItem> {

        val forgettingList = mutableListOf<BlockItem>()
        val nowTime = System.currentTimeMillis()
        val db = BlockStudyDatabase(context).writableDatabase
        val dao = AbsRecentResultsDao(db)
        dao.find("status > 0 and status < ?", arrayOf(COMPLETE_STATUS.toString())).forEach {

            val elapsedTimeSinceFirstCorrectAnswer = nowTime - it.beginTime
            val isInSpacedRepetition = !StudyUtils.outOfTerm(it.status, elapsedTimeSinceFirstCorrectAnswer)
            val isCorrectRecently = it.answerResult == 1

            if (!(isCorrectRecently && isInSpacedRepetition)) {
                forgettingList.add(getBlockItem(it.blockNo, it.itemNo))
            }
        }
        db.close()
        blockItemList = forgettingList
        return forgettingList
    }

    fun getWastingList(context: Context): List<BlockItem> {

        val wastingList = mutableListOf<BlockItem>()
        val db = BlockStudyDatabase(context).writableDatabase
        val dao = AbsRecentResultsDao(db)
        val elapsedTime = System.currentTimeMillis() - StudyUtils.BORDER_TIME_3
        // 最初の正解から１ヶ月超えたアイテムのステータスを０にする
        val update = ContentValues().apply {
            put("status", 0)
        }
        dao.update(update, "answerResult = 0 and beginTime < ?", arrayOf(elapsedTime.toString()))

        dao.findAll("status = 0").forEach {
            wastingList.add(getBlockItem(it.blockNo, it.itemNo))
        }
        db.close()
        blockItemList = wastingList
        return wastingList
    }
}