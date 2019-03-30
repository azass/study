package com.example.study.app.eba

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.study.R

class EbaWebActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_eba_web)
        val webView = findViewById<WebView>(R.id.eba_web)
        val url = intent.getStringExtra("url")

        // リンクをタップしたときに標準ブラウザを起動させない
        webView.webViewClient = WebViewClient()

        // 最初に投稿を表示
        webView.loadUrl(url)

        // jacascriptを許可する
        webView.settings.javaScriptEnabled = true
    }
}