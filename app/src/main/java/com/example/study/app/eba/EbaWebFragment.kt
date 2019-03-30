package com.example.study.app.eba

import android.app.AlertDialog
import android.content.DialogInterface
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.study.*
import com.example.study.model.eba.EbaDailyWebLog
import com.example.study.model.eba.EbaWebLecture
import com.example.study.model.eba.EbaWebMemo
import com.example.study.persistence.BlockStudyDatabase
import com.example.study.persistence.eba.EbaDailyWebLogsDao
import com.example.study.persistence.eba.EbaTotalWebLogsDao
import com.example.study.persistence.eba.EbaWebLogsDao
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.dialog_webmemo.view.*
import java.io.Serializable

class EbaWebFragment: Fragment() {
    private var startTime = System.currentTimeMillis()
    private lateinit var ebaWebLecture: EbaWebLecture
    private var mWebLectureReference: DatabaseReference? = null

    companion object {
        fun newInstance(ebaWebLecture: EbaWebLecture): EbaWebFragment {
            val args = Bundle()
            args.putSerializable("EbaWebLecture", ebaWebLecture as Serializable)
            val fragment = EbaWebFragment()
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mWebLectureReference = FirebaseDatabase.getInstance().reference.child("EBA_WebLecture")

        val ebaWeb = inflater.inflate(R.layout.fragment_eba_web, container,false)
        val webView = ebaWeb.findViewById<WebView>(R.id.eba_web)

        ebaWebLecture = arguments?.getSerializable("EbaWebLecture") as EbaWebLecture
//        val builder = CustomTabsIntent.Builder()
//        val chromeIntent = builder.build()
//        chromeIntent.launchUrl(context, Uri.parse("https://eba.learning-ware.jp"))


        // リンクをタップしたときに標準ブラウザを起動させない
        webView?.webViewClient = WebViewClient()

        // 最初に投稿を表示
        webView?.loadUrl(ebaWebLecture.url)

        // jacascriptを許可する
        webView?.settings?.javaScriptEnabled = true

        readyStopWebButton(ebaWeb, webView)
        readyMemoWebButton(ebaWeb, webView)

        return ebaWeb
    }

    fun readyStopWebButton(ebaWeb: View, webView: WebView) {
        val stopWebButton = ebaWeb.findViewById<Button>(R.id.stopWebButton)
        stopWebButton.setOnClickListener {

            AlertDialog.Builder(context).apply {
                setMessage("停止してよろしいですか？")
                setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                    val endTime = System.currentTimeMillis()
                    val watchTime = ((endTime - startTime)/1000).toInt()
                    val db = BlockStudyDatabase(context).writableDatabase

                    logEbaWeb(db, endTime, watchTime)
                    db.close()

                    fragmentManager!!
                        .beginTransaction()
                        .replace(R.id.content_main,
                            EbaWebStopFragment.newInstance(
                                webView!!.url,
                                watchTime.toString()
                            )
                        )
                        .commit()
                })
                setNegativeButton("Cancel", null)
                show()
            }
        }
    }

    fun logEbaWeb(db: SQLiteDatabase, endTime: Long, watchTimeBySecond: Int) {

        val execDate = DateUtils.getNowDateLabel()

        val ebaWebLogsDao = EbaWebLogsDao(db)
        ebaWebLogsDao.insert(startTime, endTime)

        val ebaDailyWebLogsDao = EbaDailyWebLogsDao(db)
        var ebaDailyWebLog = ebaDailyWebLogsDao.select(execDate)
        if (ebaDailyWebLog == null) {
            ebaDailyWebLog = EbaDailyWebLog(execDate, watchTimeBySecond)
            ebaDailyWebLogsDao.insert(ebaDailyWebLog)
        } else {
            ebaDailyWebLog.watchTime = ebaDailyWebLog.watchTime + watchTimeBySecond
            ebaDailyWebLogsDao.update(ebaDailyWebLog)
        }

        val ebaTotalWebLogsDao = EbaTotalWebLogsDao(db)
        val ebaTotalWebLog = ebaTotalWebLogsDao.select()
        ebaTotalWebLog!!.watchTime = ebaTotalWebLog!!.watchTime + watchTimeBySecond
        if (!execDate.equals(ebaTotalWebLog.lastExecDate)) {
            ebaTotalWebLog!!.days++
            ebaTotalWebLog.lastExecDate = execDate
        }
        ebaTotalWebLogsDao.update(ebaTotalWebLog)
    }
    /* メモボタン */
    fun readyMemoWebButton(ebaWeb: View, webView: WebView) {
        val memoWebButton = ebaWeb.findViewById<Button>(R.id.memoWebButton)
        memoWebButton.setOnClickListener { it: View? ->
            val url = webView!!.url
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_webmemo, null)
//            val db = BlockStudyDatabase(context!!).writableDatabase
//            val dao = EbaWebMemoDao(db)
//            var webMemo = dao.getWebUrl(url)
//            if (webMemo == null) {
//                webMemo = dao.insertUrl(url, "")
//            }
            dialogView.urlSpan.text = url
            dialogView.inputTitle.setText(ebaWebLecture.title, TextView.BufferType.EDITABLE)
            val dialog = createEditDialog(context!!, dialogView, "")
            dialogView.saveMemo.setOnClickListener {
                dialog.dismiss()
                val title = dialogView.inputTitle.text.toString()

                val webMemo = EbaWebMemo()
                webMemo.memo = dialogView.inputMemo.text.toString()

                closeKeyboard(activity!!)
                webMemo.fileName = saveCapture(captureView(ebaWeb)!!)

                if (title != ebaWebLecture.title) {
                    if (ebaWebLecture.key == "") {
                        var newRef = mWebLectureReference!!.push()
                        ebaWebLecture = EbaWebLecture(0, newRef!!.key!!, url, title)
                        newRef.setValue(ebaWebLecture)
                    } else {
                        ebaWebLecture.title = title
                    }
//                    dao.updateUrl(webMemo.url_id, title)
                }

                val memoRef = mWebLectureReference!!.child(ebaWebLecture.key + "/memoList").push()
                webMemo.key = memoRef.key!!
                memoRef.setValue(webMemo)
//                dao.insert(webMemo!!)
//                db.close()

                Toast.makeText(context, "完了", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun captureView(view: View): Bitmap? {
        view.isDrawingCacheEnabled = true

        val cache = view.drawingCache
        if (cache == null) {
            return null
        }
        val screenShot = Bitmap.createBitmap(cache)
        view.isDrawingCacheEnabled = false
        return screenShot
    }
}