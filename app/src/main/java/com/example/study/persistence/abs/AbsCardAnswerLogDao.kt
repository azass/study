package com.example.study.persistence.abs

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.study.model.abs.AbsCardAnswerLog
import com.example.study.writeCsv
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AbsCardAnswerLogDao(val db: SQLiteDatabase) {

    val TABLE_NAME = "ABS_CardAnswerLog"

    fun insert(cardAnswer: AbsCardAnswerLog) {
        val record = ContentValues().apply {
            put("blockNo", cardAnswer.blockNo)
            put("itemNo", cardAnswer.itemNo)
            put("seqNo", cardAnswer.seqNo)
            put("result", cardAnswer.result)
            put("startTime", cardAnswer.startTime)
            put("endTime", cardAnswer.endTime)
            put("elapsedTime", cardAnswer.elapsedTime)
        }
        db.insert(TABLE_NAME, null, record)
    }

    fun selectLast(blockNo: String, itemNo: Int, seqNo: Int): AbsCardAnswerLog? {
        db.query(
            TABLE_NAME,
            arrayOf("blockNo", "itemNo", "seqNo", "result", "startTime", "endTime", "elapsedTime"),
            "blockNo = ? and itemNo = ? and seqNo = ?",
            arrayOf(blockNo, itemNo.toString(), seqNo.toString()),
            null,
            null,
            "endTime desc",
            null
        ).use{ c ->
            if (c.moveToFirst()) {
                return AbsCardAnswerLog(
                    c.getString(c.getColumnIndex("blockNo")),
                    c.getInt(c.getColumnIndex("itemNo")),
                    c.getInt(c.getColumnIndex("seqNo")),
                    c.getInt(c.getColumnIndex("result")),
                    c.getString(c.getColumnIndex("startTime")),
                    c.getString(c.getColumnIndex("endTime")),
                    c.getInt(c.getColumnIndex("elapsedTime"))
                )
            }
        }
        return AbsCardAnswerLog(blockNo, itemNo, seqNo, null, null, null, null)
    }

    fun selectAll(): List<AbsCardAnswerLog> {
        return findAll(null, null)
    }

    fun findAll(whereClause: String?, orderBy: String?): List<AbsCardAnswerLog> {
        return find(whereClause, null, orderBy)
    }

    fun find(whereClause: String?, whereValues: Array<String>?, orderBy: String?): List<AbsCardAnswerLog> {
        val list = mutableListOf<AbsCardAnswerLog>()
        db.query(
            TABLE_NAME,
            arrayOf("_id", "blockNo", "itemNo", "seqNo", "result", "startTime", "endTime", "elapsedTime"),
            whereClause,
            whereValues,
            null,
            null,
            orderBy,
            null
        ).use { c ->
            while (c.moveToNext()) {
                val resultRecord = AbsCardAnswerLog(
                    c.getInt(c.getColumnIndex("_id")),
                    c.getString(c.getColumnIndex("blockNo")),
                    c.getInt(c.getColumnIndex("itemNo")),
                    c.getInt(c.getColumnIndex("seqNo")),
                    c.getInt(c.getColumnIndex("result")),
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
        val CSV_HEADER = "blockNo,itemNo,seqNo,result,startTime,endTime,elapsedTime"
        val data = mutableListOf<List<String>>()
        selectAll().forEach {
            data.add(
                listOf<String>(
                    it.blockNo,
                    it.itemNo.toString(),
                    it.seqNo.toString(),
                    it.result.toString(),
                    it.startTime!!,
                    it.endTime!!,
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
                    val cardAnswerLog = it.getValue<AbsCardAnswerLog>(AbsCardAnswerLog::class.java)!!
                    db.delete(TABLE_NAME, "_id = ?", arrayOf(cardAnswerLog._id.toString()))
                    val record = ContentValues().apply {
                        put("_id", cardAnswerLog._id)
                        put("blockNo", cardAnswerLog.blockNo)
                        put("itemNo", cardAnswerLog.itemNo)
                        put("seqNo", cardAnswerLog.seqNo)
                        put("result", cardAnswerLog.result)
                        put("startTime", cardAnswerLog.startTime)
                        put("endTime", cardAnswerLog.endTime)
                        put("elapsedTime", cardAnswerLog.elapsedTime)
                        put("bakFlg", 1)
                    }
                    try {
                        db.insert(TABLE_NAME, null, record)
                    } catch (e: Exception) {}
                }
                Log.d("TAG", "AbsCardAnswerLogDao.import complete")
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}