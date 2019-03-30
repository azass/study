package com.example.study.persistence.abs

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.study.model.abs.BlockCard

class BlockCardDao (val db: SQLiteDatabase) {
    fun insert(blockCard: BlockCard) {
        val record = ContentValues().apply {
            put("blockNo", blockCard.blockNo)
            put("itemNo", blockCard.itemNo)
            put("seq", blockCard.seq)
            put("Q", blockCard.Q)
            put("hint", blockCard.hint)
        }
        db.insert("BlockCard", null, record)
    }

    fun updateQ(blockCard: BlockCard) {
        val updateSet = ContentValues().apply {
            put("Q", blockCard.Q)
        }
        db.update("BlockCard", updateSet, "blockNo = ? and itemNo = ? and seq = ?",
            arrayOf(blockCard.blockNo, blockCard.itemNo.toString(), blockCard.seq.toString())
        )
    }

    fun updateHint(blockCard: BlockCard) {
        val updateSet = ContentValues().apply {
            put("hint", blockCard.hint)
        }
        db.update("BlockCard", updateSet, "blockNo = ? and itemNo = ? and seq = ?",
            arrayOf(blockCard.blockNo, blockCard.itemNo.toString(), blockCard.seq.toString())
        )
    }

    fun selectAll(): List<BlockCard> {

        return findAll(null, null)
    }

    fun findAll(whereClause: String?, orderBy: String?): List<BlockCard> {
        return find(whereClause, null, orderBy)
    }

    fun find(whereClause: String?, whereValues: Array<String>?, orderBy: String?): List<BlockCard> {
        val list = mutableListOf<BlockCard>()
        db.query(
            "BlockCard",
            arrayOf("blockNo", "itemNo", "seq", "Q", "hint"),
            whereClause,
            whereValues,
            null,
            null,
            orderBy,
            null
        ).use { c ->
            while (c.moveToNext()) {
                val blockCard = BlockCard(
                    c.getString(c.getColumnIndex("blockNo")),
                    c.getInt(c.getColumnIndex("itemNo")),
                    c.getInt(c.getColumnIndex("seq")),
                    c.getString(c.getColumnIndex("Q")),
                    c.getString(c.getColumnIndex("hint"))
                )
                list.add(blockCard)
            }
        }
        return list
    }
}