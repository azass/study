package com.example.study.persistence.eba

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.study.model.eba.EbaDailyWebLog
import com.example.study.writeCsv
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EbaDailyWebLogsDao(val db: SQLiteDatabase) {

    val TABLE_NAME = "EBA_DailyWebLogs"

    fun insert(ebaDailyWebLog: EbaDailyWebLog) {
        val record = ContentValues().apply {
            put("execDate", ebaDailyWebLog.execDate)
            put("watchTime", ebaDailyWebLog.watchTime)
        }
        db.insert(TABLE_NAME, null, record)
    }

    fun update(ebaDailyWebLog: EbaDailyWebLog) {
        val updateSet = ContentValues().apply {
            put("watchTime", ebaDailyWebLog.watchTime)
        }
        db.update(TABLE_NAME, updateSet, "execDate = ?", arrayOf(ebaDailyWebLog.execDate))
    }

    fun selectAll(): List<EbaDailyWebLog> {
        return findAll(null, null)
    }

    fun findAll(whereClause: String?, orderBy: String?): List<EbaDailyWebLog> {
        return find(whereClause, null, orderBy)
    }

    fun find(whereClause: String?, whereValues: Array<String>?, orderBy: String?): List<EbaDailyWebLog> {
        val list = mutableListOf<EbaDailyWebLog>()
        db.query(
            TABLE_NAME,
            arrayOf("_id", "execDate", "watchTime"),
            whereClause,
            whereValues,
            null,
            null,
            orderBy,
            null
        ).use { c ->
            while (c.moveToNext()) {
                val resultRecord = EbaDailyWebLog(
                    c.getInt(c.getColumnIndex("_id")),
                    c.getString(c.getColumnIndex("execDate")),
                    c.getInt(c.getColumnIndex("watchTime"))
                )
                list.add(resultRecord)
            }
        }
        return list
    }

    fun select(execDate: String): EbaDailyWebLog? {
        db.query(
            TABLE_NAME,
            arrayOf("execDate", "watchTime"),
            "execDate = ?",
            arrayOf(execDate),
            null,
            null,
            null,
            null
        ).use{ c ->
            if (c.moveToFirst()) {
                val resultRecord = EbaDailyWebLog(
                    c.getString(c.getColumnIndex("execDate")),
                    c.getInt(c.getColumnIndex("watchTime"))
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
        val CSV_HEADER = "execDate,watchTime"
        val data = mutableListOf<List<String>>()
        selectAll().forEach {
            data.add(listOf<String>(it.execDate, it.watchTime.toString()))
        }
        writeCsv(TABLE_NAME, CSV_HEADER, data)
    }

    fun import() {
        val database = FirebaseDatabase.getInstance().reference
        database.child(TABLE_NAME).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val ebaDailyWebLog = it.getValue<EbaDailyWebLog>(EbaDailyWebLog::class.java)!!
                    db.delete(TABLE_NAME, "_id = ?", arrayOf(ebaDailyWebLog._id.toString()))
                    val record = ContentValues().apply {
                        put("_id", ebaDailyWebLog._id)
                        put("execDate", ebaDailyWebLog.execDate)
                        put("watchTime", ebaDailyWebLog.watchTime)
                        put("bakFlg", 1)
                    }
                    try {
                        db.insert(TABLE_NAME, null, record)
                    } catch (e: Exception) {}
                }
                Log.d("TAG", "EbaDailyWebLogsDao.import complete")
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}