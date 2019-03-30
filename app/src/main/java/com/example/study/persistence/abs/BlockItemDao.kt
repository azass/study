package com.example.study.persistence.abs

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.study.model.abs.BlockItem

class BlockItemDao (val db: SQLiteDatabase) {
    fun insert(blockItem: BlockItem) {
        val record = ContentValues().apply {
            put("blockNo", blockItem.blockNo)
            put("itemNo", blockItem.itemNo)
            put("subtitle", blockItem.subtitle)
        }
        db.insert("BlockItem", null, record)
    }

    fun selectAll(): List<BlockItem> {

        return findAll(null, null)
    }

    fun findAll(whereClause: String?, orderBy: String?): List<BlockItem> {
        return find(whereClause, null, orderBy)
    }

    fun find(whereClause: String?, whereValues: Array<String>?, orderBy: String?): List<BlockItem> {
        val list = mutableListOf<BlockItem>()
        db.query(
            "BlockItem",
            arrayOf("blockNo", "itemNo", "subtitle"),
            whereClause,
            whereValues,
            null,
            null,
            orderBy,
            null
        ).use { c ->
            while (c.moveToNext()) {
                val blockItem = BlockItem(
                    c.getString(c.getColumnIndex("blockNo")),
                    c.getInt(c.getColumnIndex("itemNo")),
                    c.getString(c.getColumnIndex("subtitle")),
                    null
                )
                list.add(blockItem)
            }
        }
        return list
    }
}