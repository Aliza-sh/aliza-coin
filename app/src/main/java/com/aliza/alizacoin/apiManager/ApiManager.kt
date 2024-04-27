package com.aliza.alizacoin.apiManager

import com.aliza.alizacoin.apiManager.model.ChartData
import com.aliza.alizacoin.apiManager.model.CoinsData
import com.aliza.alizacoin.apiManager.model.NewsData
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiManager {
    private val apiService: ApiService

    init {
        val okHttpClient = OkHttpClient.Builder().addInterceptor {
            val oldRequest = it.request()
            val newRequest = oldRequest.newBuilder()
            newRequest.addHeader("Apikey", API_KEY)
            it.proceed(newRequest.build())
        }.build()
        val retrofit = Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    fun getNews(apiCallback: ApiCallback<ArrayList<Pair<String, String>>>) {

        apiService.getTopNews().enqueue(object : Callback<NewsData> {
            override fun onResponse(call: Call<NewsData>, response: Response<NewsData>) {
                if (response.isSuccessful) {
                    val data = response.body()!!
                    if (data != null) {
                        val dataToSend: ArrayList<Pair<String, String>> = arrayListOf()
                        data.data.forEach {
                            dataToSend.add(Pair(it.title, it.url))
                        }
                        apiCallback.onSuccess(dataToSend)
                    } else {
                        // Handle api null
                        apiCallback.onError("data is null")
                    }
                } else {
                    // Handle api error
                    apiCallback.onError("Error: " + response.code())
                }
            }

            override fun onFailure(call: Call<NewsData>, t: Throwable) {
                apiCallback.onError(t.message!!)
            }
        })
    }

    fun getCoinsList(apiCallback: ApiCallback<List<CoinsData.Data>>) {
        apiService.getTopCoins().enqueue(object : Callback<CoinsData> {
            override fun onResponse(call: Call<CoinsData>, response: Response<CoinsData>) {
                if (response.isSuccessful) {
                    val data = response.body()!!
                    if (data != null) {
                        apiCallback.onSuccess(cleanCoinsData(data.data))
                    } else {
                        // Handle api null
                        apiCallback.onError("data is null")
                    }
                } else {
                    // Handle api error
                    apiCallback.onError("Error: " + response.code())
                }
            }

            override fun onFailure(call: Call<CoinsData>, t: Throwable) {
                apiCallback.onError(t.message!!)
            }
        })
    }

    private fun cleanCoinsData(data: List<CoinsData.Data>): List<CoinsData.Data> {

        val newData = mutableListOf<CoinsData.Data>()

        data.forEach {
            if (it.dISPLAY != null || it.dISPLAY != null) {
                newData.add(it)
            }
        }
        return newData
    }

    fun getChartData(
        symbol: String,
        period: String,
        apiCallback: ApiCallback<Pair<List<ChartData.Data>, ChartData.Data?>>
    ) {
        var histoPeriod = ""
        var limit = 30
        var aggregate = 1
        when (period) {
            HOUR -> {
                histoPeriod = HISTO_MINUTE
                limit = 60
                aggregate = 12
            }
            HOURS24 -> {
                histoPeriod = HISTO_HOUR
                limit = 24
            }
            MONTH -> {
                histoPeriod = HISTO_DAY
                limit = 30
            }
            MONTH3 -> {
                histoPeriod = HISTO_DAY
                limit = 90
            }
            WEEK -> {
                histoPeriod = HISTO_HOUR
                aggregate = 6
            }
            YEAR -> {
                histoPeriod = HISTO_DAY
                aggregate = 13
            }
            ALL -> {
                histoPeriod = HISTO_DAY
                aggregate = 30
                limit = 2000
            }
        }

        apiService.getChartData(histoPeriod, symbol, limit, aggregate)
            .enqueue(object : Callback<ChartData> {
                override fun onResponse(call: Call<ChartData>, response: Response<ChartData>) {
                    if (response.isSuccessful) {
                        val dataFull = response.body()!!
                        if (dataFull != null) {
                            val dataChart = dataFull.data
                            val dataBaseLineChart = dataFull.data.maxByOrNull { it.close.toFloat() }
                            val returningData = Pair(dataChart, dataBaseLineChart)

                            apiCallback.onSuccess(returningData)
                        } else {
                            // Handle api null
                            apiCallback.onError("data is null")
                        }
                    } else {
                        // Handle api error
                        apiCallback.onError("Error: " + response.code())
                    }
                }

                override fun onFailure(call: Call<ChartData>, t: Throwable) {
                    apiCallback.onError(t.message!!)
                }
            })
    }

    interface ApiCallback<T> {
        fun onSuccess(data: T)
        fun onError(errorMessage: String)
    }
}