package com.example.study.persistence.eba

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.study.model.eba.EbaWritingDrill
import com.example.study.writeCsv
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EbaWritingDrillDao(val db: SQLiteDatabase) {

    val TABLE_NAME = "EBA_WritingDrill"

    fun insert(askDate: String, question: String) {
        val record = ContentValues().apply {
            put("askDate", askDate)
            put("question", question)
        }
        db.insert(TABLE_NAME, null, record)
    }

    fun selectAll(): List<EbaWritingDrill> {
        return findAll(null, null)
    }

    fun findAll(whereClause: String?, orderBy: String?): List<EbaWritingDrill> {
        return find(whereClause, null, orderBy)
    }

    fun find(whereClause: String?, whereValues: Array<String>?, orderBy: String?): List<EbaWritingDrill> {
        val list = mutableListOf<EbaWritingDrill>()
        db.query(
            TABLE_NAME,
            arrayOf("_id", "askDate", "question"),
            whereClause,
            whereValues,
            null,
            null,
            orderBy,
            null
        ).use { c ->
            while (c.moveToNext()) {
                val resultRecord = EbaWritingDrill(
                    c.getInt(c.getColumnIndex("_id")),
                    c.getString(c.getColumnIndex("askDate")),
                    c.getString(c.getColumnIndex("question"))
                )
                list.add(resultRecord)
            }
        }
        return list
    }

    fun select(): EbaWritingDrill? {
        db.query(
            TABLE_NAME,
            arrayOf("askDate", "question"),
            null,
            null,
            null,
            null,
            null,
            null
        ).use{ c ->
            if (c.moveToFirst()) {
                return EbaWritingDrill(
                    0,
                    c.getString(c.getColumnIndex("askDate")),
                    c.getString(c.getColumnIndex("question"))
                )
            }
        }
        return null
    }

    fun export() {
        exportCsv()
        val database = FirebaseDatabase.getInstance().reference
        findAll("bakFlg = '0'", null).forEach {
            database.child(TABLE_NAME).push().setValue(it)

            val updateSet = ContentValues().apply {
                put("bakFlg", 1)
            }
            db.update(TABLE_NAME, updateSet, "_id = ?", arrayOf(it._id.toString()))
        }
    }

    fun exportCsv() {
        val CSV_HEADER = "startTime,endTime,watchTime"
        val data = mutableListOf<List<String>>()
        selectAll().forEach {
            data.add(listOf<String>(it.askDate, it.question))
        }
        writeCsv(TABLE_NAME, CSV_HEADER, data)
    }

    fun import() {
        val database = FirebaseDatabase.getInstance().reference
        database.child(TABLE_NAME).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val EbaWritingDrill = it.getValue<EbaWritingDrill>(EbaWritingDrill::class.java)!!
                    db.delete(TABLE_NAME, "_id = ?", arrayOf(EbaWritingDrill._id.toString()))
                    val record = ContentValues().apply {
                        put("_id", EbaWritingDrill._id)
                        put("askDate", EbaWritingDrill.askDate)
                        put("endTime", EbaWritingDrill.question)
                        put("bakFlg", 1)
                    }
                    try {
                        db.insert(TABLE_NAME, null, record)
                    } catch (e: Exception) {}
                }
                Log.d("TAG", "EbaWritingDrillsDao.import complete")
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}