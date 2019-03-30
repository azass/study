package com.example.study.persistence.abs

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.study.model.abs.AbstractionBlock

class AbstractionBlockDao(val db: SQLiteDatabase) {
    fun insert(abstractionBlock: AbstractionBlock) {
        val record = ContentValues().apply {
            put("absNo", abstractionBlock.absNo)
            put("title", abstractionBlock.title)
        }
        db.insert("AbstractionBlock", null, record)
    }

    fun selectAll(): List<AbstractionBlock> {

        return findAll(null)
    }

    fun findAll(whereClause: String?): List<AbstractionBlock> {
        return find(whereClause, null)
    }

    fun find(whereClause: String?, whereValues: Array<String>?): List<AbstractionBlock> {
        val list = mutableListOf<AbstractionBlock>()
        db.query(
            "AbstractionBlock",
            arrayOf("absNo", "title"),
            whereClause,
            whereValues,
            null,
            null,
            null,
            null
        ).use { c ->
            while (c.moveToNext()) {
                val abstractionBlock = AbstractionBlock(
                    c.getString(c.getColumnIndex("absNo")),
                    c.getString(c.getColumnIndex("title")),
                    null
                )
                list.add(abstractionBlock)
            }
        }
        return list
    }
}