package com.example.study.persistence

import android.database.sqlite.SQLiteDatabase

class DailyResultsDao(val db: SQLiteDatabase) {

    fun find(startDate: String, endDate: String): Map<String, FloatArray> {
        val result = mutableMapOf<String, FloatArray>()

        val sql = StringBuffer()
            .append("SELECT c.execDate, abs.execTime, eba.watchTime ")
            .append("FROM (SELECT execDate FROM ABS_DailyResults UNION SELECT execDate FROM EBA_DailyWebLogs) AS c ")
            .append("LEFT OUTER JOIN ABS_DailyResults abs ON c.execDate = abs.execDate ")
            .append("LEFT OUTER JOIN EBA_DailyWebLogs eba ON c.execDate = eba.execDate ")
            .append("WHERE c.execDate BETWEEN ? AND ? ORDER BY c.execDate")

        db.rawQuery(sql.toString(), arrayOf(startDate, endDate)).use { c ->
            while (c.moveToNext()) {
                val execTime = c.getInt(c.getColumnIndex("execTime")).toFloat()
                val watchTime = c.getInt(c.getColumnIndex("watchTime")).toFloat()
                result.put(
                    c.getString(c.getColumnIndex("execDate")),
                    floatArrayOf(
                        if (execTime == 0f) 1f else execTime,
                        if (watchTime == 0f) 1f else watchTime
                    )
                )
            }
        }
        return result
    }
}