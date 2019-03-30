package com.example.study.model.abs

import android.database.sqlite.SQLiteDatabase
import com.example.study.DateUtils
import com.example.study.StudyUtils
import com.example.study.persistence.abs.*

class AbsWriterOfLog(val db: SQLiteDatabase, val blockItem: BlockItem) {

    fun register(correctCount: Int, openTime: Long, endTime: Long, execTime: Int) {
        val isCorrect = (correctCount == blockItem.contents!!.size)

        insertAnswerLog(correctCount, openTime, endTime, execTime)
        registerRecentResult(isCorrect, endTime)
        updateResultRecord(isCorrect, execTime)
    }

    fun insertAnswerLog(correctCount: Int, openTime: Long, endTime: Long, execTime: Int) {
        val answerLog = AbsAnswerLog(
            blockItem.blockSheet!!.blockNo,
            blockItem.itemNo,
            100 * correctCount / blockItem.contents!!.size,
            DateUtils.formatDateAndTime(openTime),
            DateUtils.formatDateAndTime(endTime),
            execTime
        )
        val dao = AbsAnswerLogDao(db)
        dao.insert(answerLog)
    }

    fun insertCardAnswerLog(seqNo: Int, result: Int, startTime: Long, endTime: Long) {
        val cardAnswerLog = AbsCardAnswerLog(
            blockItem.blockSheet!!.blockNo,
            blockItem.itemNo,
            seqNo,
            result,
            DateUtils.formatDateAndTime(startTime),
            DateUtils.formatDateAndTime(endTime),
            (endTime - startTime).toInt()
        )
        val dao = AbsCardAnswerLogDao(db)
        dao.insert(cardAnswerLog)
    }

    // 抽象化ブロックシートの各項目の最新学習結果を登録する
    fun registerRecentResult(isCorrect: Boolean, endTime: Long) {

        val dao = AbsRecentResultsDao(db)
        val recentResult = dao.select(blockItem.blockSheet!!.blockNo, Integer.parseInt(blockItem.itemNo.toString()))

        if (recentResult != null) {
            var correctTimes = recentResult.correctTimes
            var status = recentResult.status
            var beginTime = recentResult.beginTime
            var answerResult = 0

            // 全正解の場合
            if (isCorrect) {
                correctTimes++
                answerResult = 1
                if (status < StudyUtils.COMPLETE_STATUS && StudyUtils.outOfTerm(status, endTime - beginTime)) status++
                beginTime = if (status == StudyUtils.INITIAL_STATUS) endTime else beginTime

            } else {
                status = if (status == StudyUtils.COMPLETE_STATUS || StudyUtils.outOfTerm(
                        status,
                        endTime - beginTime
                    )
                ) StudyUtils.INITIAL_STATUS else status
            }
            recentResult.correctTimes = correctTimes
            recentResult.status = status
            recentResult.answerResult = answerResult
            recentResult.beginTime = beginTime

            dao.update(recentResult)

        } else {
            if (isCorrect) {
                val correctTimes = if (isCorrect) 1 else 0
                val status = if (isCorrect) StudyUtils.ONE_DAY_STATUS else StudyUtils.INITIAL_STATUS
                val answerResult = if (isCorrect) 1 else 0
                dao.insert(
                    AbsRecentResult(
                        blockItem.blockSheet!!.blockNo,
                        Integer.parseInt(blockItem.itemNo.toString()),
                        correctTimes,
                        status,
                        answerResult,
                        endTime,
                        endTime
                    )
                )
            }
        }
    }

    fun updateResultRecord(isCorrect: Boolean, execTime: Int) {
        val nowDateLabel = DateUtils.getNowDateLabel()
        val dailyResultsDao = AbsDailyResultsDao(db)
        var resultRecord = dailyResultsDao.select(nowDateLabel)
        if (resultRecord == null) {
            resultRecord = AbsResultRecord(nowDateLabel, 1, if (isCorrect) 1 else 0, execTime)
            dailyResultsDao.insert(resultRecord)
        } else {
            resultRecord.answerCount++
            if (isCorrect) {
                resultRecord.correctCount++
            }
            resultRecord.execTime = resultRecord.execTime + execTime
            dailyResultsDao.update(resultRecord)
        }

        val totalResultsDao = AbsTotalResultsDao(db)
        val totalResult = totalResultsDao.select()
        totalResult!!.answerCount++
        if (isCorrect) {
            totalResult!!.correctCount++
        }
        totalResult!!.execTime = totalResult!!.execTime + execTime
        if (!nowDateLabel.equals(totalResult.lastExecDate)) {
            totalResult!!.days++
            totalResult.lastExecDate = nowDateLabel
        }
        totalResultsDao.update(totalResult)
    }
}