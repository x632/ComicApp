package com.poema.comicapp.ui.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import com.poema.comicapp.R

class ExplanationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explanation)

        webViewSetup()
    }

    private fun webViewSetup() {
        val webView=findViewById<WebView>(R.id.webView)
        webView.apply{
            loadUrl("https://www.explainxkcd.com/wiki/index.php/2511:_Recreate_the_Conditions")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                settings.safeBrowsingEnabled = true
            }
        }
    }

}