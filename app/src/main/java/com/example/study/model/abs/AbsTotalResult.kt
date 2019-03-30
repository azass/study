package com.example.study.model.abs

import java.io.Serializable

class AbsTotalResult(
    val _id: Int =0,
    var lastExecDate: String? =null,
    var answerCount: Int =0,
    var correctCount: Int =0,
    var execTime: Int =0,
    var days:Int =0): Serializable {

    constructor(
        lastExecDate: String?,
        answerCount: Int,
        correctCount: Int,
        execTime: Int,
        days:Int
    ) : this(
        0,
        lastExecDate,
        answerCount,
        correctCount,
        execTime,
        days
    )
    fun averageAnswerCount(): Int? {
        if (days == 0) {
            return null
        } else {
            return answerCount/days
        }
    }

    fun averageCorrectCount(): Int? {
        if (days == 0) {
            return null
        } else {
            return correctCount/days
        }
    }

    fun averageExecTime(): Int? {
        if (days == 0) {
            return null
        } else {
            return execTime/days
        }
    }
}