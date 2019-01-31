package com.example.study

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private const val DB_NAME = "BlockStudyDatabae"
private const val DB_VERSION = 1

class BlockStudyDatabase(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("""
            CREATE TABLE Records (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                blockNo TEXT NOT NULL,
                itemNo INTEGER NOT NULL,
                accuracyRate REAL NOT NULL,
                startTime INTEGER NOT NULL,
                endTime INTEGER NOT NULL,
                elapsedTime INTEGER NOT NULL);
            """)

        db?.execSQL("""
            CREATE TABLE RecordDetails (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                blockNo TEXT NOT NULL,
                itemNo INTEGER NOT NULL,
                seqNo INTEGER NOT NULL,
                result INTEGER NOT NULL,
                startTime INTEGER NOT NULL,
                endTime INTEGER NOT NULL,
                elapsedTime INTEGER NOT NULL);
            """)

        db?.execSQL("""
            CREATE TABLE RecentResults (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                blockNo TEXT NOT NULL,
                itemNo INTEGER NOT NULL,
                correctTimes INTEGER NOT NULL,
                status INTEGER NOT NULL,
                answerResult INTEGER NOT NULL,
                beginTime INTEGER NOT NULL,
                updateTime INTEGER NOT NULL);
            """)

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}