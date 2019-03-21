package com.example.study.persistence.abs

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.study.model.abs.AbsRecentResult
import com.example.study.writeCsv
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AbsRecentResultsDao(val db: SQLiteDatabase) {

    val TABLE_NAME = "ABS_RecentResults"

    fun selectAll(): List<AbsRecentResult> {

        return findAll(null)
    }

    fun findAll(whereClause: String?): List<AbsRecentResult> {
        return find(whereClause, null)
    }

    fun find(whereClause: String?, whereValues: Array<String>?): List<AbsRecentResult> {
        val list = mutableListOf<AbsRecentResult>()
        db.query(
            TABLE_NAME,
            arrayOf("_id", "blockNo", "itemNo", "correctTimes", "status", "answerResult", "beginTime", "updateTime"),
            whereClause,
            whereValues,
            null,
            null,
            null,
            null
        ).use { c ->
            while (c.moveToNext()) {
                val recentResult = AbsRecentResult(
                    c.getInt(c.getColumnIndex("_id")),
                    c.getString(c.getColumnIndex("blockNo")),
                    c.getInt(c.getColumnIndex("itemNo")),
                    c.getInt(c.getColumnIndex("correctTimes")),
                    c.getInt(c.getColumnIndex("status")),
                    c.getInt(c.getColumnIndex("answerResult")),
                    c.getLong(c.getColumnIndex("beginTime")),
                    c.getLong(c.getColumnIndex("updateTime"))
                )
                println(recentResult)
                list.add(recentResult)
            }
        }
        return list
    }

    fun select(blockNo: String, itemNo: Int): AbsRecentResult? {

        db.query(
            TABLE_NAME,
            arrayOf("correctTimes", "status", "answerResult", "beginTime", "updateTime"),
            "blockNo = ? and itemNo = ?",
            arrayOf(blockNo, itemNo.toString()),
            null,
            null,
            null,
            null
        ).use { c ->
            if (c.moveToFirst()) {
                return AbsRecentResult(
                    blockNo,
                    itemNo,
                    c.getInt(c.getColumnIndex("correctTimes")),
                    c.getInt(c.getColumnIndex("status")),
                    c.getInt(c.getColumnIndex("answerResult")),
                    c.getLong(c.getColumnIndex("beginTime")),
                    c.getLong(c.getColumnIndex("updateTime"))
                )
            }
        }
        return null
    }

    fun insert(absRecentResult: AbsRecentResult) {
        val record = ContentValues().apply {
            put("blockNo", absRecentResult.blockNo)
            put("itemNo", absRecentResult.itemNo)
            put("correctTimes", absRecentResult.correctTimes)
            put("status", absRecentResult.status)
            put("answerResult", absRecentResult.answerResult)
            put("beginTime", absRecentResult.beginTime)
            put("updateTime", absRecentResult.updateTime)
        }
        db.insert(TABLE_NAME, null, record)
    }

    fun update(absRecentResult: AbsRecentResult) {
        val updateSet = ContentValues().apply {
            put("correctTimes", absRecentResult.correctTimes)
            put("status", absRecentResult.status)
            put("answerResult", absRecentResult.answerResult)
            put("beginTime", absRecentResult.beginTime)
            put("updateTime", absRecentResult.updateTime)
        }
        update(updateSet, "blockNo = ? and itemNo = ?",
            arrayOf(absRecentResult.blockNo, absRecentResult.itemNo.toString())
        )
    }

    fun update(updateSet: ContentValues, whereClause: String, whereValues: Array<String>) {
        db.update(TABLE_NAME, updateSet, whereClause, whereValues)
    }

    fun export() {
        exportCsv()
        val database = FirebaseDatabase.getInstance().reference
        findAll("bakFlg = '1'").forEach {
            database.child(TABLE_NAME).push().setValue(it)

            val updateSet = ContentValues().apply {
                put("bakFlg", 1)
            }
            db.update(TABLE_NAME, updateSet, "_id = ?", arrayOf(it._id.toString()))
        }
    }

    fun exportCsv() {
        val CSV_HEADER = "blockNo,itemNo,correctTimes,status,answerResult,beginTime,updateTime"
        val data = mutableListOf<List<String>>()
        selectAll().forEach {
            data.add(
                listOf<String>(
                    it.blockNo,
                    it.itemNo.toString(),
                    it.correctTimes.toString(),
                    it.status.toString(),
                    it.answerResult.toString(),
                    it.beginTime.toString(),
                    it.updateTime.toString()
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
                    val recentResult = it.getValue<AbsRecentResult>(AbsRecentResult::class.java)!!
                    db.delete(TABLE_NAME, "_id = ?", arrayOf(recentResult._id.toString()))
                    val record = ContentValues().apply {
                        put("_id", recentResult._id)
                        put("blockNo", recentResult.blockNo)
                        put("itemNo", recentResult.itemNo)
                        put("correctTimes", recentResult.correctTimes)
                        put("status", recentResult.status)
                        put("answerResult", recentResult.answerResult)
                        put("beginTime", recentResult.beginTime)
                        put("updateTime", recentResult.updateTime)
                        put("bakFlg", 1)
                    }
                    try {
                        db.insert(TABLE_NAME, null, record)
                    } catch (e: Exception) {}
                }
                Log.d("TAG", "AbsRecentResultsDao.import complete")
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}