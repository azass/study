package com.example.study.persistence.abs

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.study.model.abs.AbsResultRecord
import com.example.study.writeCsv
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AbsDailyResultsDao(val db: SQLiteDatabase) {

    val TABLE_NAME = "ABS_DailyResults"

    fun insert(absResultRecord: AbsResultRecord) {
        val record = ContentValues().apply {
            put("execDate", absResultRecord.execDate)
            put("answerCount", absResultRecord.answerCount)
            put("correctCount", absResultRecord.correctCount)
            put("execTime", absResultRecord.execTime)
        }
        db.insert(TABLE_NAME, null, record)
    }

    fun update(absResultRecord: AbsResultRecord) {
        val updateSet = ContentValues().apply {
            put("answerCount", absResultRecord.answerCount)
            put("correctCount", absResultRecord.correctCount)
            put("execTime", absResultRecord.execTime)
            put("bakFlg", 1)
        }
        db.update(TABLE_NAME, updateSet, "execDate = ?", arrayOf(absResultRecord.execDate))
    }

    fun selectAll(): List<AbsResultRecord> {
        return findAll(null, null)
    }

    fun findAll(whereClause: String?, orderBy: String?): List<AbsResultRecord> {
        return find(whereClause, null, orderBy)
    }

    fun find(whereClause: String?, whereValues: Array<String>?, orderBy: String?): List<AbsResultRecord> {
        val list = mutableListOf<AbsResultRecord>()
        db.query(
            TABLE_NAME,
            arrayOf("_id", "execDate", "answerCount", "correctCount", "execTime"),
            whereClause,
            whereValues,
            null,
            null,
            orderBy,
            null
        ).use { c ->
            while (c.moveToNext()) {
                val resultRecord = AbsResultRecord(
                    c.getInt(c.getColumnIndex("_id")),
                    c.getString(c.getColumnIndex("execDate")),
                    c.getInt(c.getColumnIndex("answerCount")),
                    c.getInt(c.getColumnIndex("correctCount")),
                    c.getInt(c.getColumnIndex("execTime"))
                )
                list.add(resultRecord)
            }
        }
        return list
    }

    fun select(execDate: String): AbsResultRecord? {

        db.query(
            TABLE_NAME,
            arrayOf("execDate", "answerCount", "correctCount", "execTime"),
            "execDate = ?",
            arrayOf(execDate),
            null,
            null,
            null,
            null
        ).use{ c ->
            if (c.moveToFirst()) {
                val resultRecord = AbsResultRecord(
                    c.getString(c.getColumnIndex("execDate")),
                    c.getInt(c.getColumnIndex("answerCount")),
                    c.getInt(c.getColumnIndex("correctCount")),
                    c.getInt(c.getColumnIndex("execTime"))
                )
                return resultRecord
            }
        }
        return null
    }

    fun export() {
        exportCsv()
        val database = FirebaseDatabase.getInstance().reference
        findAll("bakFlg = '1'", null).forEach {
            database.child(TABLE_NAME).push().setValue(it)

            val updateSet = ContentValues().apply {
                put("bakFlg", 1)
            }
            db.update(TABLE_NAME, updateSet, "_id = ?", arrayOf(it._id.toString()))
        }
    }

    fun exportCsv() {
        val CSV_HEADER = "execDate,answerCount,correctCount,execTime"
        val data = mutableListOf<List<String>>()
        selectAll().forEach {
            data.add(listOf<String>(it.execDate!!, it.answerCount.toString(), it.correctCount.toString(), it.execTime.toString()))
        }
        writeCsv(TABLE_NAME, CSV_HEADER, data)
    }

    fun import() {
        val database = FirebaseDatabase.getInstance().reference
        database.child(TABLE_NAME).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val absResultRecord = it.getValue<AbsResultRecord>(AbsResultRecord::class.java)!!
                    db.delete(TABLE_NAME, "_id = ?", arrayOf(absResultRecord._id.toString()))
                    val record = ContentValues().apply {
                        put("_id", absResultRecord._id)
                        put("execDate", absResultRecord.execDate)
                        put("answerCount", absResultRecord.answerCount)
                        put("correctCount", absResultRecord.correctCount)
                        put("execTime", absResultRecord.execTime)
                     }
                    try {
                        db.insert(TABLE_NAME, null, record)
                    } catch (e: Exception) {}
                }
                Log.d("TAG", "AbsDailyResultsDao.import complete")
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}
