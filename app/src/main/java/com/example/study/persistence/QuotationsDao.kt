package com.example.study.persistence

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.study.model.Quotation
import com.example.study.writeCsv
import com.google.firebase.database.FirebaseDatabase

class QuotationsDao(val db: SQLiteDatabase) {

    val TABLE_NAME = "Quotations"

    fun insert(quotation: Quotation) {
        val record = ContentValues().apply {
            put("quote", quotation.quote)
            put("author", quotation.author)
            put("likeCount", quotation.likeCount)
            put("status_1", quotation.status1)
            put("status_2", quotation.status2)
            put("status_3", quotation.status3)
        }
        db.insert(TABLE_NAME, null, record)
    }

    fun update(quotation: Quotation) {
        val updateSet = ContentValues().apply {
            put("quote", quotation.quote)
            put("author", quotation.author)
            put("likeCount", quotation.likeCount)
            put("status_1", quotation.status1)
            put("status_2", quotation.status2)
            put("status_3", quotation.status3)
        }
        db.update(TABLE_NAME, updateSet, "_id = ?",
            arrayOf(quotation._id.toString())
        )
    }

    fun delete(quotation: Quotation) {
        db.delete(TABLE_NAME, "_id = ?", arrayOf(quotation._id.toString()))
    }
    fun selectAll(): List<Quotation> {

        return findAll(null, "_id")
    }

    fun findAll(whereClause: String?, orderBy: String?): List<Quotation> {
        return find(whereClause, null, orderBy)
    }

    fun find(whereClause: String?, whereValues: Array<String>?, orderBy: String?): List<Quotation> {
        val list = mutableListOf<Quotation>()
        db.query(
            TABLE_NAME,
            arrayOf("_id", "quote", "author", "likeCount", "status_1", "status_2", "status_3"),
            whereClause,
            whereValues,
            null,
            null,
            orderBy,
            null
        ).use { c ->
            while (c.moveToNext()) {
                val quotation = Quotation(
                    c.getInt(c.getColumnIndex("_id")),
                    c.getString(c.getColumnIndex("quote")),
                    c.getString(c.getColumnIndex("author")),
                    c.getInt(c.getColumnIndex("likeCount")),
                    c.getInt(c.getColumnIndex("status_1")),
                    c.getInt(c.getColumnIndex("status_2")),
                    c.getInt(c.getColumnIndex("status_3"))
                )
                list.add(quotation)
            }
        }
        return list
    }

    fun select(quote: String) : Quotation? {
        db.query(
            TABLE_NAME,
            arrayOf("_id", "quote", "author", "likeCount", "status_1", "status_2", "status_3"),
            "quote = ?",
            arrayOf(quote),
            null,
            null,
            null,
            null
        ).use { c ->
            if (c.moveToFirst()) {
                return Quotation(
                    c.getInt(c.getColumnIndex("_id")),
                    c.getString(c.getColumnIndex("quote")),
                    c.getString(c.getColumnIndex("author")),
                    c.getInt(c.getColumnIndex("likeCount")),
                    c.getInt(c.getColumnIndex("status_1")),
                    c.getInt(c.getColumnIndex("status_2")),
                    c.getInt(c.getColumnIndex("status_3"))
                )
            }
        }
        return null
    }

    fun export() {
        exportCsv()
        val database = FirebaseDatabase.getInstance().reference
        selectAll().forEach {
            database.child(TABLE_NAME).push().setValue(it)
        }
    }

    fun exportCsv() {
        val CSV_HEADER = "quote,author,likeCount,status_1,status_2,status_3"
        val data = mutableListOf<List<String>>()
        selectAll().forEach {
            data.add(listOf<String>(it.quote, it.author, it.likeCount.toString(), it.status1.toString(), it.status2.toString(), it.status3.toString()))
        }
        writeCsv(TABLE_NAME, CSV_HEADER, data)
    }
}