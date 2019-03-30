package com.example.study.persistence.abs

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.study.model.abs.BlockSheet

class BlockSheetDao(val db: SQLiteDatabase) {
    fun insert(blockSheet: BlockSheet) {
        val record = ContentValues().apply {
            put("absNo", blockSheet.absNo)
            put("blockNo", blockSheet.blockNo)
            put("title", blockSheet.title)
        }
        db.insert("BlockSheet", null, record)
    }

    fun selectAll(): List<BlockSheet> {

        return findAll(null, null)
    }

    fun findAll(whereClause: String?, orderBy: String?): List<BlockSheet> {
        return find(whereClause, null, orderBy)
    }

    fun find(whereClause: String?, whereValues: Array<String>?, orderBy: String?): List<BlockSheet> {
        val list = mutableListOf<BlockSheet>()
        db.query(
            "BlockSheet",
            arrayOf("absNo", "blockNo", "title"),
            whereClause,
            whereValues,
            null,
            null,
            orderBy,
            null
        ).use { c ->
            while (c.moveToNext()) {
                val blockSheet = BlockSheet(
                    c.getInt(c.getColumnIndex("absNo")),
                    c.getString(c.getColumnIndex("blockNo")),
                    c.getString(c.getColumnIndex("title"))
                )
                list.add(blockSheet)
            }
        }
        return list
    }
}