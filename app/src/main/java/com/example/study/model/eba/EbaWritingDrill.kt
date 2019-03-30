package com.example.study.model.eba

import java.io.Serializable

class EbaWritingDrill(
    val _id: Int = 0,
    val askDate: String = "",
    val question: String = "",
    val answers: List<String> = mutableListOf()
) :Serializable {
}