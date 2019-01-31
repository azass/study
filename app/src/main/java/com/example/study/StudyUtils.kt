package com.example.study

fun outOfTerm(status: Int, elapsedTime: Long): Boolean {
    if (status == 1 && elapsedTime >= 24 * 3600000) {
        return true
    } else if (status == 2 && elapsedTime >= 168 * 3600000) {
        return true
    } else if (status == 3 && elapsedTime >= 720 * 3600000) {
        return true
    } else {
        return false
    }
}

