package com.example.study.persistence.eba

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.study.model.eba.EbaTotalWebLog
import com.example.study.writeCsv
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EbaTotalWebLogsDao(val db: SQLiteDatabase) {

    val TABLE_NAME = "EBA_TotalWebLogs"

    fun insert(totalResult: EbaTotalWebLog) {
        val record = ContentValues().apply {
            put("watchTime", totalResult.watchTime)
            put("lastExecDate", totalResult.lastExecDate)
            put("days", totalResult.days)
        }
        db.insert(TABLE_NAME, null, record)
    }

    fun update(totalResult: EbaTotalWebLog) {
        val updateSet = ContentValues().apply {
            put("watchTime", totalResult.watchTime)
            put("lastExecDate", totalResult.lastExecDate)
            put("days", totalResult.days)
        }
        db.update(TABLE_NAME, updateSet, null, null)
    }

    fun select(): EbaTotalWebLog? {
        db.query(
            TABLE_NAME,
            arrayOf("lastExecDate", "days", "watchTime"),
            null,
            null,
            null,
            null,
            null,
            null
        ).use{ c ->
            if (c.moveToFirst()) {
                return EbaTotalWebLog(
                    c.getString(c.getColumnIndex("lastExecDate")),
                    c.getInt(c.getColumnIndex("watchTime")),
                    c.getInt(c.getColumnIndex("days"))
                )
            }
        }
        return null
    }

    fun export() {
        exportCsv()
        val database = FirebaseDatabase.getInstance().reference
        database.child(TABLE_NAME).setValue(select())
    }

    fun exportCsv() {
        val CSV_HEADER = "lastExecDate,watchTime,days"
        val data = mutableListOf<List<String>>()
        val ebaTotalWebLog = select()!!
        data.add(
            listOf<String>(
                ebaTotalWebLog.lastExecDate!!,
                ebaTotalWebLog.watchTime.toString(),
                ebaTotalWebLog.days.toString()
            )
        )
        writeCsv(TABLE_NAME, CSV_HEADER, data)
    }

    fun import() {
        val database = FirebaseDatabase.getInstance().reference
        database.child(TABLE_NAME).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val ebaTotalWebLog = p0.getValue<EbaTotalWebLog>(EbaTotalWebLog::class.java)!!
                db.delete(TABLE_NAME, "_id = ?", arrayOf(ebaTotalWebLog._id.toString()))
                val record = ContentValues().apply {
                    put("_id", ebaTotalWebLog._id)
                    put("lastExecDate", ebaTotalWebLog.lastExecDate)
                    put("watchTime", ebaTotalWebLog.watchTime)
                    put("days", ebaTotalWebLog.days)
                    put("bakFlg", 1)
                }
                try {
                    db.insert(TABLE_NAME, null, record)
                } catch (e: Exception) {}
                Log.d("TAG", "EbaTotalWebLogsDao.import complete")
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}