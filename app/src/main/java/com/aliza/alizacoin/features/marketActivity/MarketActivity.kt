package com.aliza.alizacoin.features.marketActivity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.aliza.alizacoin.apiManager.ApiManager
import com.aliza.alizacoin.base.BaseActivity
import com.aliza.alizacoin.base.NetworkChecker
import com.aliza.alizacoin.base.URL_DATA
import com.aliza.alizacoin.base.WEBSITE
import com.aliza.alizacoin.base.showSnacbar
import com.aliza.alizacoin.databinding.ActivityMarketBinding
import com.aliza.alizacoin.features.WebActivity

class MarketActivity : BaseActivity<ActivityMarketBinding>() {
    override fun inflateBinding() = ActivityMarketBinding.inflate(layoutInflater)
    private val apiManager = ApiManager()
    lateinit var dataNews: ArrayList<Pair<String, String>>
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setSupportActionBar(binding.layoutToolbar.toolbar)

        networkChecker()

    }
    private fun networkChecker() {
        if (NetworkChecker(applicationContext).isInternetConnected) {
            initUi()
        } else {
            showSnacbar(binding.root,"No Internet!")
                .setAction("Retry") {
                    networkChecker()
                }
                .show()
        }
    }
    private fun initUi() {
        getNewsFromApi()

    }

    private fun getNewsFromApi() {
        apiManager.getNews(object : ApiManager.ApiCallback<ArrayList<Pair<String, String>>> {
            @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
            override fun onSuccess(data: ArrayList<Pair<String, String>>) {
                this@MarketActivity.dataNews = data
                refreshNews()
            }
            override fun onError(errorMessage: String) {
                showSnacbar(binding.root, "error => $errorMessage")
            }
        })
    }
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun refreshNews() {
        val randomAccess = (0 until dataNews.size).random()
        binding.layoutNews.txtNews.text = dataNews[randomAccess].first
        binding.layoutNews.imgNews.setOnClickListener {
            val intent = Intent(this, WebActivity::class.java)
            val bundle = Bundle()
            bundle.putString(WEBSITE, dataNews[randomAccess].second)
            intent.putExtra(URL_DATA, bundle)
            startActivity(intent)
        }
        binding.layoutNews.txtNews.setOnClickListener {
            refreshNews()
        }
    }
}