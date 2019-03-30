package com.example.study.persistence.eba

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.study.model.eba.EbaWebLog
import com.example.study.writeCsv
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EbaWebLogsDao(val db: SQLiteDatabase) {

    val TABLE_NAME = "EBA_WebLogs"

    fun insert(startTime: Long, endTime: Long) {
        val record = ContentValues().apply {
            put("startTime", startTime)
            put("endTime", endTime)
            put("watchTime", endTime - startTime)
        }
        db.insert(TABLE_NAME, null, record)
    }

    fun selectAll(): List<EbaWebLog> {
        return findAll(null, null)
    }

    fun findAll(whereClause: String?, orderBy: String?): List<EbaWebLog> {
        return find(whereClause, null, orderBy)
    }

    fun find(whereClause: String?, whereValues: Array<String>?, orderBy: String?): List<EbaWebLog> {
        val list = mutableListOf<EbaWebLog>()
        db.query(
            TABLE_NAME,
            arrayOf("_id", "startTime", "endTime", "watchTime"),
            whereClause,
            whereValues,
            null,
            null,
            orderBy,
            null
        ).use { c ->
            while (c.moveToNext()) {
                val resultRecord = EbaWebLog(
                    c.getInt(c.getColumnIndex("_id")),
                    c.getInt(c.getColumnIndex("startTime")),
                    c.getInt(c.getColumnIndex("endTime")),
                    c.getInt(c.getColumnIndex("watchTime"))
                )
                list.add(resultRecord)
            }
        }
        return list
    }

    fun select(): EbaWebLog? {
        db.query(
            TABLE_NAME,
            arrayOf("startTime", "endTime", "watchTime"),
            null,
            null,
            null,
            null,
            null,
            null
        ).use{ c ->
            if (c.moveToFirst()) {
                return EbaWebLog(
                    c.getInt(c.getColumnIndex("startTime")),
                    c.getInt(c.getColumnIndex("endTime")),
                    c.getInt(c.getColumnIndex("watchTime"))
                )
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
        val CSV_HEADER = "startTime,endTime,watchTime"
        val data = mutableListOf<List<String>>()
        selectAll().forEach {
            data.add(listOf<String>(it.startTime.toString(), it.endTime.toString(), it.watchTime.toString()))
        }
        writeCsv(TABLE_NAME, CSV_HEADER, data)
    }

    fun import() {
        val database = FirebaseDatabase.getInstance().reference
        database.child(TABLE_NAME).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val ebaWebLog = it.getValue<EbaWebLog>(EbaWebLog::class.java)!!
                    db.delete(TABLE_NAME, "_id = ?", arrayOf(ebaWebLog._id.toString()))
                    val record = ContentValues().apply {
                        put("_id", ebaWebLog._id)
                        put("startTime", ebaWebLog.startTime)
                        put("endTime", ebaWebLog.endTime)
                        put("watchTime", ebaWebLog.watchTime)
                        put("bakFlg", 1)
                    }
                    try {
                        db.insert(TABLE_NAME, null, record)
                    } catch (e: Exception) {}
                }
                Log.d("TAG", "EbaWebLogsDao.import complete")
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

}