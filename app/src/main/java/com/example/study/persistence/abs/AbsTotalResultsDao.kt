package com.example.study.persistence.abs

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.study.model.abs.AbsTotalResult
import com.example.study.writeCsv
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AbsTotalResultsDao(val db: SQLiteDatabase) {

    val TABLE_NAME = "ABS_TotalResults"

    fun insert(absTotalResult: AbsTotalResult) {
        val record = ContentValues().apply {
            put("answerCount", absTotalResult.answerCount)
            put("correctCount", absTotalResult.correctCount)
            put("execTime", absTotalResult.execTime)
            put("lastExecDate", absTotalResult.lastExecDate)
            put("days", absTotalResult.days)
        }
        db.insert(TABLE_NAME, null, record)
    }

    fun update(absTotalResult: AbsTotalResult) {
        val updateSet = ContentValues().apply {
            put("answerCount", absTotalResult.answerCount)
            put("correctCount", absTotalResult.correctCount)
            put("execTime", absTotalResult.execTime)
            put("lastExecDate", absTotalResult.lastExecDate)
            put("days", absTotalResult.days)
        }
        db.update(TABLE_NAME, updateSet, null, null)
    }

    fun select(): AbsTotalResult? {
        db.query(
            TABLE_NAME,
            arrayOf("answerCount", "correctCount", "execTime", "lastExecDate", "days"),
            null,
            null,
            null,
            null,
            null,
            null
        ).use{ c ->
            if (c.moveToFirst()) {
                return AbsTotalResult(
                    c.getString(c.getColumnIndex("lastExecDate")),
                    c.getInt(c.getColumnIndex("answerCount")),
                    c.getInt(c.getColumnIndex("correctCount")),
                    c.getInt(c.getColumnIndex("execTime")),
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
        val CSV_HEADER = "lastExecDate,answerCount,correctCount,execTime,days"
        val data = mutableListOf<List<String>>()
        val absTotalResult = select()!!
        data.add(
            listOf<String>(
                absTotalResult.lastExecDate!!,
                absTotalResult.answerCount.toString(),
                absTotalResult.correctCount.toString(),
                absTotalResult.execTime.toString(),
                absTotalResult.days.toString()
            )
        )
        writeCsv(TABLE_NAME, CSV_HEADER, data)
    }

    fun import() {
        val database = FirebaseDatabase.getInstance().reference
        database.child(TABLE_NAME).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val totalResult = p0.getValue<AbsTotalResult>(AbsTotalResult::class.java)!!
                db.delete(TABLE_NAME, "_id = ?", arrayOf(totalResult._id.toString()))
                val record = ContentValues().apply {
                    put("_id", totalResult._id)
                    put("lastExecDate", totalResult.lastExecDate)
                    put("answerCount", totalResult.answerCount)
                    put("correctCount", totalResult.correctCount)
                    put("execTime", totalResult.execTime)
                    put("days", totalResult.days)
                    put("bakFlg", 1)
                }
                try {
                    db.insert(TABLE_NAME, null, record)
                } catch (e: Exception) {}
                Log.d("TAG", "AbsTotalResultsDao.import complete")
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}