package com.aliza.alizacoin.apiManager

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
                        apiCallback.onSuccess(data.data)
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

    interface ApiCallback<T> {
        fun onSuccess(data: T)
        fun onError(errorMessage: String)
    }
}