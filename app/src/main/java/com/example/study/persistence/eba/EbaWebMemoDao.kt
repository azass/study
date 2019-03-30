package com.example.study.persistence.eba

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.study.model.eba.EbaWebMemo
import com.example.study.writeCsv
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EbaWebMemoDao(val db: SQLiteDatabase) {

    val TABLE_NAME = "EBA_WebMemo"
    val TABLE_NAME2 = "EBA_WebUrl"

    fun insert(ebaWebMemo: EbaWebMemo) {
        val record = ContentValues().apply {
            put("url_id", ebaWebMemo.url_id)
            put("memo", ebaWebMemo.memo)
            put("fileName", ebaWebMemo.fileName)
        }
        db.insert(TABLE_NAME, null, record)
    }

    fun update(ebaWebMemo: EbaWebMemo) {
        val updateSet = ContentValues().apply {
            put("seekTime", ebaWebMemo.seekTime)
            put("lastExecDate", ebaWebMemo.memo)
         }
        db.update(TABLE_NAME, updateSet, "_id = ?", arrayOf(ebaWebMemo._id.toString()))
    }

    fun delete(ebaWebMemo: EbaWebMemo) {
        db.delete(TABLE_NAME, "_id = ?", arrayOf(ebaWebMemo._id.toString()))
    }

    fun selectAll(): List<EbaWebMemo> {
        return findAll(null, null)
    }

    fun findAll(whereClause: String?, orderBy: String?): List<EbaWebMemo> {
        return find(whereClause, null, orderBy)
    }

    fun find(whereClause: String?, whereValues: Array<String>?, orderBy: String?): List<EbaWebMemo> {
        val list = mutableListOf<EbaWebMemo>()
        db.query(
            TABLE_NAME,
            arrayOf("_id", "url_id", "memo", "fileName"),
            whereClause,
            whereValues,
            null,
            null,
            orderBy,
            null
        ).use { c ->
            while (c.moveToNext()) {
                val resultRecord = EbaWebMemo(
                    c.getInt(c.getColumnIndex("_id")),"",
                    c.getInt(c.getColumnIndex("url_id")), "", "",
                    c.getString(c.getColumnIndex("memo")),
                    null,//c.getString(c.getColumnIndex("seekTime")) ?: "",
                    c.getString(c.getColumnIndex("fileName"))
                )
                list.add(resultRecord)
            }
        }
        return list
    }

    fun select(): EbaWebMemo? {
        db.query(
            TABLE_NAME,
            arrayOf("url_id", "memo", "seekTime", "fileName"),
            null,
            null,
            null,
            null,
            null,
            null
        ).use{ c ->
            if (c.moveToFirst()) {
                return EbaWebMemo(
                    c.getInt(c.getColumnIndex("url_id")), "", "",
                    c.getString(c.getColumnIndex("memo")),
                    c.getString(c.getColumnIndex("seekTime")) ?: "",
                    c.getString(c.getColumnIndex("fileName"))
                )
            }
        }
        return null
    }

    fun selectAllUrl(): List<EbaWebMemo> {
        val list = mutableListOf<EbaWebMemo>()
        db.query(
            TABLE_NAME2,
            arrayOf("_id", "url", "title"),
            null,
            null,
            null,
            null,
            null,
            null
        ).use{ c ->
            while (c.moveToNext()) {
                val resultRecord = EbaWebMemo(
                    c.getInt(c.getColumnIndex("_id")),"",
                    c.getInt(c.getColumnIndex("_id")),
                    c.getString(c.getColumnIndex("url")),
                    c.getString(c.getColumnIndex("title")), "", "", ""
                )
                list.add(resultRecord)
            }
        }
        return list
    }
    fun getWebUrl(url: String): EbaWebMemo? {
        db.query(
            TABLE_NAME2,
            arrayOf("_id", "url", "title"),
            "url = ?",
            arrayOf(url),
            null,
            null,
            null,
            null
        ).use{ c ->
            if (c.moveToFirst()) {
                return EbaWebMemo(
                    c.getInt(c.getColumnIndex("_id")),
                    c.getString(c.getColumnIndex("url")),
                    c.getString(c.getColumnIndex("title")), "", "", ""
                )
            }
        }
        return null
    }

    fun insertUrl(url: String, title: String): EbaWebMemo {
        val record = ContentValues().apply {
            put("url", url)
            put("title", title)
        }
        db.insert(TABLE_NAME2, null, record)
        return getWebUrl(url)!!
    }

    fun updateUrl(_id: Int, title: String) {
        val updateSet = ContentValues().apply {
            put("title", title)
        }
        db.update(TABLE_NAME2, updateSet, "_id = ?", arrayOf(_id.toString()))
    }

    fun deleteUrl(_id: Int) {
        val updateSet = ContentValues().apply {
            put("deleteFlg", 1)
        }
        db.update(TABLE_NAME2, updateSet, "_id = ?", arrayOf(_id.toString()))
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
        selectAllUrl().forEach {
            database.child(TABLE_NAME2).push().setValue(it)

            val updateSet = ContentValues().apply {
                put("bakFlg", 1)
            }
            db.update(TABLE_NAME, updateSet, "_id = ?", arrayOf(it._id.toString()))
        }
    }

    fun exportCsv() {
        val CSV_HEADER = "url_id,memo,fileName"
        val data = mutableListOf<List<String>>()
        selectAll().forEach {
            data.add(listOf<String>(it.url_id.toString(), it.memo.toString(), it.fileName.toString()))
        }
        writeCsv(TABLE_NAME, CSV_HEADER, data)

        val CSV_HEADER2 = "_id,url,title"
        val data2 = mutableListOf<List<String>>()
        selectAllUrl().forEach {
            data2.add(listOf<String>(it.url_id.toString(), it.url.toString(), it.title.toString()))
        }
        writeCsv(TABLE_NAME2, CSV_HEADER2, data2)
    }

    fun import() {
        val database = FirebaseDatabase.getInstance().reference
        database.child(TABLE_NAME).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val ebaWebMemo = it.getValue<EbaWebMemo>(EbaWebMemo::class.java)!!
                    db.delete(TABLE_NAME, "_id = ?", arrayOf(ebaWebMemo._id.toString()))
                    val record = ContentValues().apply {
                        put("_id", ebaWebMemo._id)
                        put("url_id", ebaWebMemo.url_id)
                        put("memo", ebaWebMemo.memo)
                        put("fileName", ebaWebMemo.fileName)
                        put("bakFlg", 1)
                    }
                    try {
                        db.insert(TABLE_NAME, null, record)
                    } catch (e: Exception) {}
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
        database.child(TABLE_NAME2).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val ebaWebMemo = it.getValue<EbaWebMemo>(EbaWebMemo::class.java)!!
                    db.delete(TABLE_NAME2, "_id = ?", arrayOf(ebaWebMemo._id.toString()))
                    val record = ContentValues().apply {
                        put("_id", ebaWebMemo._id)
                        put("url", ebaWebMemo.url)
                        put("title", ebaWebMemo.title)
                        put("bakFlg", 1)
                    }
                    try {
                        db.insert(TABLE_NAME2, null, record)
                    } catch (e: Exception) {}
                }
                Log.d("TAG", "EbaWebMemoDao.import complete")
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}