package com.example.study

import android.graphics.Bitmap
import android.os.Environment
import java.io.*

private fun getFilesDir(): File {
    val publicDir = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DOCUMENTS
    )

    if (publicDir != null) {
        // 存在しないディレクトリが返された場合は、作成する
        if (!publicDir.exists()) publicDir.mkdirs()
        return publicDir
    } else {
        val dir = File(Environment.getExternalStorageDirectory(), "StudyFiles")
        // まだ作成されていない場合は、作成する
        if (!dir.exists()) dir.mkdirs()
        return dir
    }
}

fun getFiles() = getFilesDir().listFiles().toList()

fun outputFile(fileName: String, extension: String, content: String): File {

    val file = getUniqueFile(fileName, extension)

    val writer = BufferedWriter(FileWriter(file))
    writer.use {
        it.write(content)
        it.flush()
    }

    return file
}

fun inputFile(file: File): String {
    val reader = BufferedReader(FileReader(file))
    return reader.readLines().joinToString("\n")
}

fun saveCapture(screenShot: Bitmap): String {
    // ファイル名は「screen-タイムスタンプ」とする
    val fileName = getUniqueFileName("screen", "png")
    try {
        val byteArrOutputStream = ByteArrayOutputStream()
        val file = File(getFilesDir(), fileName)
        var fos = FileOutputStream(file)
        screenShot.compress(Bitmap.CompressFormat.PNG, 100, byteArrOutputStream)
        fos.write(byteArrOutputStream.toByteArray())
        fos.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return fileName
}

fun getUniqueFileName(fileName: String, extension: String): String {
    val timeStamp = DateUtils.getTimestamp()
    return "$fileName-$timeStamp.$extension"
}

fun getUniqueFile(fileName: String, extension: String): File {
    return File(getFilesDir(), getUniqueFileName(fileName, extension))
}

fun writeCsv(fileName: String, header:String, data: List<List<String>>) {
    var fileWriter: FileWriter? = null
    try {
        fileWriter = FileWriter(getUniqueFile(fileName, "csv"))

        fileWriter.append(header).append('\n')

        for (line in data) {
            var sb = StringBuffer()
            for (elem in line) {
                sb.append(elem).append(',')
            }
            sb.setLength(sb.length - 1)
            fileWriter.append(sb.toString()).append('\n')
        }

    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        fileWriter!!.flush()
        fileWriter.close()
    }
}