package com.poema.comicapp.ui.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebView.setWebContentsDebuggingEnabled
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.poema.comicapp.R
import com.poema.comicapp.data_sources.model.GlobalList
import com.poema.comicapp.databinding.FragmentDetailBinding
import com.poema.comicapp.databinding.FragmentExplanationBinding
import com.poema.comicapp.other.Utility.isInternetAvailable


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
        setHasOptionsMenu(true)
        webViewSetup(url)
        setWebContentsDebuggingEnabled(false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val temp = activity as AppCompatActivity
        temp.supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
            show()
        }

    }

    override fun onStop() {
        super.onStop()
        val temp = activity as AppCompatActivity
        temp.supportActionBar?.hide()
    }

    private fun webViewSetup(url: String) {

        val webView = binding.webView
        webView.webViewClient = WebViewClient()
        webView.apply {
            loadUrl(url)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                settings.safeBrowsingEnabled = true

            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        findNavController().popBackStack()
        return super.onOptionsItemSelected(item)
    }
}