package com.poema.comicapp.ui.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.navArgs
import com.poema.comicapp.R
import com.poema.comicapp.databinding.FragmentDetailBinding
import com.poema.comicapp.databinding.FragmentExplanationBinding


class ExplanationFragment : Fragment() {

    private lateinit var binding: FragmentExplanationBinding
    val args: ExplanationFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExplanationBinding.inflate(inflater, container, false)

        val number = args.id
        val title = args.title

        val url = "https://www.explainxkcd.com/wiki/index.php/${number}:_${title}"

        webViewSetup(url)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val a = activity as AppCompatActivity
        a.supportActionBar?.show()
    }

    override fun onStop() {
        super.onStop()
        val a = activity as AppCompatActivity
        a.supportActionBar?.hide()
    }

    private fun webViewSetup(url:String) {

        val webView=binding.webView
        webView.webViewClient = WebViewClient()
        webView.apply{
            loadUrl(url)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                settings.safeBrowsingEnabled = true
            }
        }
    }
}