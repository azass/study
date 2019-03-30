package com.example.study.model.eba
import java.io.Serializable

class EbaWebLecture (
    val _id: Int = 0,
    val key: String = "",
    val url: String = "",
    var title: String = "") : Serializable {
    constructor(
        url: String,
        title: String
    ) : this(
        0,
        "",
        url,
        title
    )
}