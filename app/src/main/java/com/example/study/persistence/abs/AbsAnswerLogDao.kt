package com.example.study.persistence.abs

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.study.DateUtils
import com.example.study.model.abs.AbsAnswerLog
import com.example.study.writeCsv
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AbsAnswerLogDao(val db: SQLiteDatabase) {

    val TABLE_NAME = "ABS_AnswerLog"

    fun insert(answerLog: AbsAnswerLog) {
        val record = ContentValues().apply {
            put("blockNo", answerLog.blockNo)
            put("itemNo", answerLog.itemNo)
            put("accuracyRate", answerLog.accuracyRate)
            put("startTime", answerLog.startTime)
            put("endTime", answerLog.endTime)
            put("elapsedTime", answerLog.elapsedTime)
        }
        db.insert(TABLE_NAME, null, record)
    }

    fun selectLast(blockNo: String, itemNo: Int): AbsAnswerLog? {
        db.query(
            TABLE_NAME,
            arrayOf("blockNo", "itemNo", "accuracyRate", "startTime", "endTime", "elapsedTime"),
            "blockNo = ? and itemNo = ?",
            arrayOf(blockNo, itemNo.toString()),
            null,
            null,
            "endTime desc",
            null
        ).use{ c ->
            if (c.moveToFirst()) {
                return AbsAnswerLog(
                    c.getString(c.getColumnIndex("blockNo")),
                    c.getInt(c.getColumnIndex("itemNo")),
                    c.getInt(c.getColumnIndex("accuracyRate")),
                    c.getString(c.getColumnIndex("startTime")),
                    c.getString(c.getColumnIndex("endTime")),
                    c.getInt(c.getColumnIndex("elapsedTime"))
                )
            }
        }
        return null
    }

    fun selectAll(): List<AbsAnswerLog> {
        return findAll(null, null)
    }

    fun findAll(whereClause: String?, orderBy: String?): List<AbsAnswerLog> {
        return find(whereClause, null, orderBy)
    }

    fun find(whereClause: String?, whereValues: Array<String>?, orderBy: String?): List<AbsAnswerLog> {
        val list = mutableListOf<AbsAnswerLog>()
        db.query(
            TABLE_NAME,
            arrayOf("_id", "blockNo", "itemNo", "accuracyRate", "startTime", "endTime", "elapsedTime"),
            whereClause,
            whereValues,
            null,
            null,
            orderBy,
            null
        ).use { c ->
            while (c.moveToNext()) {
                val resultRecord = AbsAnswerLog(
                    c.getInt(c.getColumnIndex("_id")),
                    c.getString(c.getColumnIndex("blockNo")),
                    c.getInt(c.getColumnIndex("itemNo")),
                    c.getInt(c.getColumnIndex("accuracyRate")),
                    c.getString(c.getColumnIndex("startTime")),
                    c.getString(c.getColumnIndex("endTime")),
                    c.getInt(c.getColumnIndex("elapsedTime"))
                )
                list.add(resultRecord)
            }
        }
        return list
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
        val CSV_HEADER = "blockNo,itemNo,accuracyRate,startTime,endTime,elapsedTime"
        val data = mutableListOf<List<String>>()
        selectAll().forEach {
            data.add(
                listOf<String>(
                    it.blockNo,
                    it.itemNo.toString(),
                    it.accuracyRate.toString(),
                    it.startTime,
                    it.endTime,
                    it.elapsedTime.toString()
                )
            )
        }
        writeCsv(TABLE_NAME, CSV_HEADER, data)
    }

    fun import() {
        val database = FirebaseDatabase.getInstance().reference
        database.child(TABLE_NAME).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val answerLog = it.getValue<AbsAnswerLog>(AbsAnswerLog::class.java)!!
                    db.delete(TABLE_NAME, "_id = ?", arrayOf(answerLog._id.toString()))
                    val record = ContentValues().apply {
                        put("_id", answerLog._id)
                        put("blockNo", answerLog.blockNo)
                        put("itemNo", answerLog.itemNo)
                        put("accuracyRate", answerLog.accuracyRate)
                        put("startTime", DateUtils.formatDateAndTime(answerLog.startTime) )
                        put("endTime", DateUtils.formatDateAndTime(answerLog.endTime))
                        put("elapsedTime", answerLog.elapsedTime)
                        put("bakFlg", 1)
                    }
                    try {
                        db.insert(TABLE_NAME, null, record)
                    } catch (e: Exception) {}
                }
                Log.d("TAG", "AbsAnswerLogDao.import complete")
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}