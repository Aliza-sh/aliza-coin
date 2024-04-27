package com.aliza.alizacoin.features.coinActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.aliza.alizacoin.R
import com.aliza.alizacoin.apiManager.ALL
import com.aliza.alizacoin.apiManager.ApiManager
import com.aliza.alizacoin.apiManager.HOUR
import com.aliza.alizacoin.apiManager.HOURS24
import com.aliza.alizacoin.apiManager.MONTH
import com.aliza.alizacoin.apiManager.MONTH3
import com.aliza.alizacoin.apiManager.WEEK
import com.aliza.alizacoin.apiManager.YEAR
import com.aliza.alizacoin.apiManager.model.ChartData
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
import com.aliza.alizacoin.features.WebActivity

class CoinActivity : BaseActivity<ActivityCoinBinding>() {
    override fun inflateBinding() = ActivityCoinBinding.inflate(layoutInflater)
    private lateinit var dataThisCoin: CoinsData.Data
    private lateinit var dataThisCoinAbout: CoinAboutItem
    private val apiManager = ApiManager()

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
        initChartUi()
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

    @SuppressLint("SetTextI18n")
    private fun initChartUi() {
        var period: String = HOUR
        binding.layoutChart.radio12h.isChecked = true
        requestAndShowChart(period)
        binding.layoutChart.radioGroupMain.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_12h -> {
                    period = HOUR
                }
                R.id.radio_1d -> {
                    period = HOURS24
                }
                R.id.radio_1w -> {
                    period = WEEK
                }
                R.id.radio_1m -> {
                    period = MONTH
                }
                R.id.radio_3m -> {
                    period = MONTH3
                }
                R.id.radio_1y -> {
                    period = YEAR
                }
                R.id.radio_all -> {
                    period = ALL
                }
            }
            requestAndShowChart(period)
        }

        binding.layoutChart.txtChartPrice.text = dataThisCoin.dISPLAY.uSD.pRICE
        binding.layoutChart.txtChartChange1.text = " " + dataThisCoin.dISPLAY.uSD.cHANGE24HOUR

        if (dataThisCoin.coinInfo.fullName == "BUSD") {
            binding.layoutChart.txtChartChange2.text = "0%"
        } else {
            binding.layoutChart.txtChartChange2.text = dataThisCoin.rAW.uSD.cHANGEPCT24HOUR.toString().substring(0, 5) + "%"
        }

        val change = dataThisCoin.rAW.uSD.cHANGEPCT24HOUR
        if (change > 0) {
            binding.layoutChart.txtChartChange2.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorGain
                )
            )
            binding.layoutChart.txtChartUpdown.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorGain
                )
            )

            binding.layoutChart.txtChartUpdown.text = "▲"
            binding.layoutChart.sparkviewMain.lineColor = ContextCompat.getColor(
                binding.root.context,
                R.color.colorGain
            )

        } else if (change < 0) {
            binding.layoutChart.txtChartChange2.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorLoss
                )
            )
            binding.layoutChart.txtChartUpdown.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorLoss
                )
            )

            binding.layoutChart.txtChartUpdown.text = "▼"
            binding.layoutChart.sparkviewMain.lineColor = ContextCompat.getColor(
                binding.root.context,
                R.color.colorLoss
            )
        }

        binding.layoutChart.sparkviewMain.setScrubListener {
            // show price kamel
            if ( it == null ) {
                binding.layoutChart.txtChartPrice.text = dataThisCoin.dISPLAY.uSD.pRICE
            } else {
                // show price this dot
                binding.layoutChart.txtChartPrice.text = "$ " + (it as ChartData.Data).close.toString()
            }
        }
    }

    fun requestAndShowChart(period: String) {

        apiManager.getChartData(
            dataThisCoin.coinInfo.name,
            period,
            object : ApiManager.ApiCallback<Pair<List<ChartData.Data>, ChartData.Data?>> {
                override fun onSuccess(data: Pair<List<ChartData.Data>, ChartData.Data?>) {
                    val chartAdapter = ChartAdapter(data.first, data.second?.open.toString())
                    binding.layoutChart.sparkviewMain.adapter = chartAdapter
                }

                override fun onError(errorMessage: String) {
                    showSnacbar(binding.root, "error => $errorMessage").show()
                }
            })

    }

}