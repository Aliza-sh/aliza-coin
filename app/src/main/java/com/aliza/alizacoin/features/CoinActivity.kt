package com.aliza.alizacoin.features

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.aliza.alizacoin.apiManager.model.CoinAboutItem
import com.aliza.alizacoin.apiManager.model.CoinsData
import com.aliza.alizacoin.base.ALL_COIN_DATA
import com.aliza.alizacoin.base.BASE_URL_TWITTER
import com.aliza.alizacoin.base.BaseActivity
import com.aliza.alizacoin.base.COIN_ABOUT_DATA
import com.aliza.alizacoin.base.COIN_BUNDLE
import com.aliza.alizacoin.base.NetworkChecker
import com.aliza.alizacoin.base.URL_DATA
import com.aliza.alizacoin.base.WEBSITE
import com.aliza.alizacoin.base.showSnacbar
import com.aliza.alizacoin.databinding.ActivityCoinBinding

class CoinActivity : BaseActivity<ActivityCoinBinding>() {
    override fun inflateBinding() = ActivityCoinBinding.inflate(layoutInflater)
    private lateinit var dataThisCoin: CoinsData.Data
    lateinit var dataThisCoinAbout: CoinAboutItem

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val fromIntent = intent.getBundleExtra(COIN_BUNDLE)!!
        dataThisCoin = fromIntent.getParcelable(ALL_COIN_DATA, CoinsData.Data::class.java)!!

        dataThisCoinAbout = if (fromIntent.getParcelable(COIN_ABOUT_DATA,CoinAboutItem::class.java) != null) {
            fromIntent.getParcelable(COIN_ABOUT_DATA,CoinAboutItem::class.java)!!
        } else {
            CoinAboutItem()
        }

        networkChecker()
        configToolBar()

        binding.swipeRefreshMain.setOnRefreshListener {
            networkChecker()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.swipeRefreshMain.isRefreshing = false
            }, 1500)

        }
    }

    private fun configToolBar() {
        setSupportActionBar(binding.layoutToolbar.toolbar)
        binding.layoutToolbar.toolbar.title = dataThisCoin.coinInfo.fullName
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

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun networkChecker() {
        if (NetworkChecker(applicationContext).isInternetConnected) {
            initUi()
        } else {
            showSnacbar(binding.root, "No Internet!")
                .setAction("Retry") {
                    networkChecker()
                }
                .show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun initUi() {
        initStatisticsUi()
        initAboutUi()
    }


    @SuppressLint("SetTextI18n")
    private fun initStatisticsUi() {
        binding.layoutStatistics.tvOpenAmount.text = dataThisCoin.dISPLAY.uSD.oPEN24HOUR
        binding.layoutStatistics.tvTodaysHighAmount.text = dataThisCoin.dISPLAY.uSD.hIGH24HOUR
        binding.layoutStatistics.tvTodayLowAmount.text = dataThisCoin.dISPLAY.uSD.lOW24HOUR
        binding.layoutStatistics.tvChangeTodayAmount.text = dataThisCoin.dISPLAY.uSD.cHANGE24HOUR
        binding.layoutStatistics.tvAlgorithm.text = dataThisCoin.coinInfo.algorithm
        binding.layoutStatistics.tvTotalVolume.text = dataThisCoin.dISPLAY.uSD.tOTALVOLUME24H
        binding.layoutStatistics.tvAvgMarketCapAmount.text = dataThisCoin.dISPLAY.uSD.mKTCAP
        binding.layoutStatistics.tvSupplyNumber.text = dataThisCoin.dISPLAY.uSD.sUPPLY
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun initAboutUi() {

        binding.layoutAbout.txtWebsite.text = dataThisCoinAbout.coinWebsite
        binding.layoutAbout.txtGithub.text = dataThisCoinAbout.coinGithub
        binding.layoutAbout.txtReddit.text = dataThisCoinAbout.coinReddit
        binding.layoutAbout.txtTwitter.text = "@" + dataThisCoinAbout.coinTwitter
        binding.layoutAbout.txtAboutCoin.text = dataThisCoinAbout.coinDesc

        binding.layoutAbout.txtWebsite.setOnClickListener {
            openWebsiteDataCoin(dataThisCoinAbout.coinWebsite!!)
        }
        binding.layoutAbout.txtGithub.setOnClickListener {
            openWebsiteDataCoin(dataThisCoinAbout.coinGithub!!)
        }
        binding.layoutAbout.txtReddit.setOnClickListener {
            openWebsiteDataCoin(dataThisCoinAbout.coinReddit!!)
        }
        binding.layoutAbout.txtTwitter.setOnClickListener {
            openWebsiteDataCoin(BASE_URL_TWITTER + dataThisCoinAbout.coinWebsite!!)
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun openWebsiteDataCoin(url: String) {
        val intent = Intent(this, WebActivity::class.java)
        val bundle = Bundle()
        bundle.putString(WEBSITE, url)
        intent.putExtra(URL_DATA, bundle)
        startActivity(intent)

    }
}