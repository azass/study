package com.example.study.model

import java.io.Serializable

class Quotation(
    val _id: Int=0,
    var quote: String="",
    var author: String="",
    var likeCount: Int=0,
    var status1: Int=0,
    var status2: Int=0,
    var status3: Int=0) :
    Serializable {
    constructor(
        quote: String,
        author: String,
        likeCount: Int,
        status1: Int,
        status2: Int,
        status3: Int
    ) : this(
        0,
        quote,
        author,
        likeCount,
        status1,
        status2,
        status3
    )
}