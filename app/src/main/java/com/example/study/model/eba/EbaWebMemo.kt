package com.example.study.model.eba

import java.io.Serializable

class EbaWebMemo(
    val _id: Int =0,
    var key: String = "",
    val url_id: Int =0,
    val url: String ="",
    var title: String ="",
    var memo: String? ="",
    var seekTime: String? ="",
    var fileName: String? ="") : Serializable {
    constructor(
        url_id: Int,
        url: String,
        title: String,
        memo: String?,
        seekTime: String,
        fileName: String?
    ) : this(
        0,
        "",
        url_id,
        url,
        title,
        memo,
        seekTime,
        fileName
    )
}