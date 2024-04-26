package com.aliza.alizacoin.features.marketActivity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.aliza.alizacoin.apiManager.ApiManager
import com.aliza.alizacoin.apiManager.model.CoinAboutData
import com.aliza.alizacoin.apiManager.model.CoinAboutItem
import com.aliza.alizacoin.apiManager.model.CoinsData
import com.aliza.alizacoin.base.ALL_COIN_DATA
import com.aliza.alizacoin.base.BaseActivity
import com.aliza.alizacoin.base.COIN_ABOUT_DATA
import com.aliza.alizacoin.base.COIN_BUNDLE
import com.aliza.alizacoin.base.NetworkChecker
import com.aliza.alizacoin.base.URL_DATA
import com.aliza.alizacoin.base.WEBSITE
import com.aliza.alizacoin.base.showSnacbar
import com.aliza.alizacoin.databinding.ActivityMarketBinding
import com.aliza.alizacoin.features.CoinActivity
import com.aliza.alizacoin.features.WebActivity
import com.google.gson.Gson

class MarketActivity : BaseActivity<ActivityMarketBinding>(),MarketAdapter.RecyclerCallback {
    override fun inflateBinding() = ActivityMarketBinding.inflate(layoutInflater)
    private val apiManager = ApiManager()
    lateinit var dataNews: ArrayList<Pair<String, String>>
    private lateinit var marketAdapter: MarketAdapter
    lateinit var aboutDataMap: MutableMap<String, CoinAboutItem>

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setSupportActionBar(binding.layoutToolbar.toolbar)

        binding.layoutWatchlist.btnShowMore.setOnClickListener {
            val intent = Intent(this, WebActivity::class.java)
            val bundle = Bundle()
            bundle.putString(WEBSITE, "https://www.livecoinwatch.com/")
            intent.putExtra(URL_DATA, bundle)
            startActivity(intent)
        }

        binding.swipeRefreshMain.setOnRefreshListener {
            networkChecker()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.swipeRefreshMain.isRefreshing = false
            }, 1500)

        }

        networkChecker()

        getAboutDataFromAssets()
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
        getTopCoinsFromApi()
    }

    private fun getNewsFromApi() {
        apiManager.getNews(object : ApiManager.ApiCallback<ArrayList<Pair<String, String>>> {
            @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
            override fun onSuccess(data: ArrayList<Pair<String, String>>) {
                this@MarketActivity.dataNews = data
                refreshNews()
            }
            override fun onError(errorMessage: String) {
                showSnacbar(binding.root, "error => $errorMessage").show()
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

    private fun getTopCoinsFromApi() {
        apiManager.getCoinsList(object : ApiManager.ApiCallback<List<CoinsData.Data>> {
            override fun onSuccess(data: List<CoinsData.Data>) {
                showDataInRecycler(data)
            }
            override fun onError(errorMessage: String) {
                showSnacbar(binding.root, "error => $errorMessage").show()
                Log.v("testLog", errorMessage)
            }
        })
    }
    private fun showDataInRecycler(data: List<CoinsData.Data>) {

        marketAdapter = MarketAdapter(ArrayList(data), this)
        binding.layoutWatchlist.recyclerMain.adapter = marketAdapter
        binding.layoutWatchlist.recyclerMain.layoutManager = LinearLayoutManager(this)

    }
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCoinItemClicked(dataCoin: CoinsData.Data) {
        val intent = Intent(this, CoinActivity::class.java)

        val allCoinDataBundle = Bundle()
        allCoinDataBundle.putParcelable(ALL_COIN_DATA, dataCoin)
        allCoinDataBundle.putParcelable(COIN_ABOUT_DATA, aboutDataMap[dataCoin.coinInfo.name])
        intent.putExtra(COIN_BUNDLE, allCoinDataBundle)

        startActivity(intent)

    }

    private fun getAboutDataFromAssets() {

        val fileInString = applicationContext.assets
            .open("currencyinfo.json")
            .bufferedReader()
            .use { it.readText() }

        aboutDataMap = mutableMapOf<String, CoinAboutItem>()

        val gson = Gson()
        val dataAboutAll = gson.fromJson(fileInString, CoinAboutData::class.java)

        dataAboutAll.forEach {
            aboutDataMap[it.currencyName] = CoinAboutItem(
                it.info.desc,
                it.info.web,
                it.info.twt,
                it.info.reddit,
                it.info.github
            )
        }

    }

}