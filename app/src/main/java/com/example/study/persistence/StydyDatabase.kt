package com.example.study.persistence

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.study.model.abs.AbsTotalResult
import com.example.study.model.abs.AbstractionBlock
import com.example.study.model.eba.EbaTotalWebLog
import com.example.study.persistence.abs.*
import com.example.study.persistence.eba.EbaTotalWebLogsDao
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private const val DB_NAME = "BlockStudyDatabae"
private const val DB_VERSION = 1

class BlockStudyDatabase(val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("""
            CREATE TABLE ABS_AnswerLog (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                blockNo TEXT NOT NULL,
                itemNo INTEGER NOT NULL,
                accuracyRate REAL NOT NULL,
                startTime TEXT NOT NULL,
                endTime TEXT NOT NULL,
                elapsedTime INTEGER NOT NULL,
                bakFlg INTEGER default 0);
            """)

        db?.execSQL("""
            CREATE TABLE ABS_CardAnswerLog (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                blockNo TEXT NOT NULL,
                itemNo INTEGER NOT NULL,
                seqNo INTEGER NOT NULL,
                result INTEGER NOT NULL,
                startTime TEXT NOT NULL,
                endTime TEXT NOT NULL,
                elapsedTime INTEGER NOT NULL,
                bakFlg INTEGER default 0);
            """)

        db?.execSQL("""
            CREATE TABLE ABS_RecentResults (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                blockNo TEXT NOT NULL,
                itemNo INTEGER NOT NULL,
                correctTimes INTEGER NOT NULL,
                status INTEGER NOT NULL,
                answerResult INTEGER NOT NULL,
                beginTime INTEGER NOT NULL,
                updateTime INTEGER NOT NULL,
                bakFlg INTEGER default 0);
            """)

        db?.execSQL("""
            CREATE TABLE ABS_DailyResults (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                execDate TEXT NOT NULL,
                answerCount INTEGER NOT NULL,
                correctCount INTEGER NOT NULL,
                execTime INTEGER NOT NULL,
                bakFlg INTEGER default 0
            );
            """
        )

        db?.execSQL("""
            CREATE TABLE ABS_TotalResults (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                lastExecDate TEXT NOT NULL,
                answerCount INTEGER NOT NULL,
                correctCount INTEGER NOT NULL,
                execTime INTEGER NOT NULL,
                days INTEGER NOT NULL,
                bakFlg INTEGER default 0
            );
            """
        )

        db?.execSQL("""
            CREATE TABLE AbstractionBlock (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                absNo INTEGER NOT NULL,
                title TEXT NOT NULL);
            """)

        db?.execSQL("""
            CREATE TABLE BlockSheet (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                absNo INTEGER NOT NULL,
                blockNo TEXT NOT NULL,
                title TEXT NOT NULL);
            """)

        db?.execSQL("""
            CREATE TABLE BlockItem (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                blockNo TEXT NOT NULL,
                itemNo INTEGER NOT NULL,
                subtitle TEXT NOT NULL);
            """)

        db?.execSQL("""
            CREATE TABLE BlockCard (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                blockNo TEXT NOT NULL,
                itemNo INTEGER NOT NULL,
                seq INTEGER NOT NULL,
                Q TEXT NOT NULL,
                hint TEXT);
            """)

        db?.execSQL("""
            CREATE TABLE Dialogue (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                line TEXT NOT NULL,
                status_1 INTEGER NOT NULL,
                status_2 INTEGER NOT NULL,
                status_3 INTEGER NOT NULL
            )
            """
        )

        db?.execSQL("""
            CREATE TABLE Quotations (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                quote TEXT NOT NULL,
                author TEXT NOT NULL,
                likeCount INTEGER NOT NULL,
                status_1 INTEGER NOT NULL,
                status_2 INTEGER NOT NULL,
                status_3 INTEGER NOT NULL
            )
            """
        )

        db?.execSQL("""
            CREATE TABLE EBA_WebLogs (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                startTime INTEGER NOT NULL,
                endTime INTEGER NOT NULL,
                watchTime INTEGER NOT NULL,
                bakFlg INTEGER default 0);
            """)

        db?.execSQL("""
            CREATE TABLE EBA_DailyWebLogs (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                execDate TEXT NOT NULL,
                watchTime INTEGER NOT NULL,
                bakFlg INTEGER default 0);
            """)

        db?.execSQL("""
            CREATE TABLE EBA_TotalWebLogs (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                lastExecDate TEXT NOT NULL,
                watchTime INTEGER NOT NULL,
                days INTEGER NOT NULL,
                bakFlg INTEGER default 0);
            """)

        db?.execSQL("""
            CREATE TABLE EBA_WebUrl (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                url TEXT NOT NULL,
                title TEXT NOT NULL,
                deleteFlg INTEGER default 0,
                bakFlg INTEGER default 0);
        """)

        db?.execSQL("""
            CREATE TABLE EBA_WebMemo (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                url_id INTEGER,
                memo TEXT NOT NULL,
                fileName TEXT NOT NULL,
                seekTime TEXT,
                bakFlg INTEGER default 0);
        """)

        db?.execSQL("""
            CREATE TABLE EBA_WritingDrill (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                askDate TEXT NOT NULL,
                question TEXT NOT NULL,
                bakFlg INTEGER default 0
            );
        """)

        // JSONから初期データ投入
        val json: String = context.assets.open("data.json").bufferedReader().use { it.readText() }
        val typeToken = object : TypeToken<List<AbstractionBlock>>() {}
        val abstractionBlockList = Gson().fromJson<List<AbstractionBlock>>(json, typeToken.type)

        val abstractionBlockDao = AbstractionBlockDao(db!!)
        val blockSheetDao = BlockSheetDao(db!!)
        val blockItemDao = BlockItemDao(db)
        val blockCardDao = BlockCardDao(db!!)
        abstractionBlockList.forEach {
            val abstractionBlock = it
            abstractionBlockDao.insert(abstractionBlock)
            it.blocksheetList!!.forEach {
                val blockSheet = it
                blockSheet.absNo = abstractionBlock.absNo
                blockSheetDao.insert(blockSheet)
                it.itemList!!.forEach {
                    val blockItem = it
                    blockItem.blockNo = blockSheet.blockNo
                    blockItemDao.insert(blockItem)
                    blockItem.contents!!.forEachIndexed { index, blockCard ->
                        blockCard.blockNo = blockSheet.blockNo
                        blockCard.itemNo = blockItem.itemNo
                        blockCard.seq = index
                        blockCardDao.insert(blockCard)
                    }
                }
            }
        }

        val totalResultsDao = AbsTotalResultsDao(db)
        totalResultsDao.insert(AbsTotalResult("", 0, 0, 0, 0))

        val ebaTotalWebLogsDao = EbaTotalWebLogsDao(db)
        ebaTotalWebLogsDao.insert(EbaTotalWebLog("", 0, 0))
//        db.close()
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}