package com.aliza.alizacoin.apiManager

import com.aliza.alizacoin.apiManager.model.CoinsData
import com.aliza.alizacoin.apiManager.model.NewsData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("v2/news/")
    fun getTopNews(
        @Query("sortOrder") sortOrder: String = "popular"
    ): Call<NewsData>

}