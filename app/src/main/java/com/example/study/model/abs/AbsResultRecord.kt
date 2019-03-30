package com.example.study.model.abs

import java.io.Serializable

data class AbsResultRecord(
    val _id: Int=0,
    var execDate: String?=null,
    var answerCount: Int=0,
    var correctCount: Int=0,
    var execTime: Int=0): Serializable {
    constructor(
        execDate: String?,
        answerCount: Int,
        correctCount: Int,
        execTime: Int
    ) : this(
        0,
        execDate,
        answerCount,
        correctCount,
        execTime
    )
}