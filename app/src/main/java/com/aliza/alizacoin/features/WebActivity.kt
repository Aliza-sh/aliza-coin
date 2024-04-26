package com.aliza.alizacoin.features

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.aliza.alizacoin.base.BaseActivity
import com.aliza.alizacoin.base.URL_DATA
import com.aliza.alizacoin.base.WEBSITE
import com.aliza.alizacoin.databinding.ActivityWebBinding

class WebActivity : BaseActivity<ActivityWebBinding>() {
    override fun inflateBinding(): ActivityWebBinding = ActivityWebBinding.inflate(layoutInflater)

    inner class MyWebViewClient : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            binding.progressBarWebView.visibility = View.VISIBLE
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            binding.progressBarWebView.visibility = View.GONE
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        configToolBar()

        val bundle = intent.getBundleExtra(URL_DATA)!!
        val url = bundle.getString(WEBSITE)!!

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient = MyWebViewClient()
        if (url != null) {
            binding.webView.loadUrl(url)
        }

        binding.layoutToolbar.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun configToolBar(){
        setSupportActionBar(binding.layoutToolbar.toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return true
    }
}