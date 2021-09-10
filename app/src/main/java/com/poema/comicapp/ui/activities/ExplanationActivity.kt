package com.poema.comicapp.ui.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.poema.comicapp.R



class ExplanationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explanation)

        val number = intent.getIntExtra("id", 0)
        val title = intent.getStringExtra("title")

        val url = "https://www.explainxkcd.com/wiki/index.php/${number}:_${title}"

        webViewSetup(url)
    }

    private fun webViewSetup(url:String) {

        val webView=findViewById<WebView>(R.id.webView)
        webView.webViewClient = WebViewClient()
        webView.apply{
            loadUrl(url)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                settings.safeBrowsingEnabled = true
            }
        }
    }


}