package com.aliza.alizacoin.apiManager

import com.aliza.alizacoin.apiManager.model.ChartData
import com.aliza.alizacoin.apiManager.model.CoinsData
import com.aliza.alizacoin.apiManager.model.NewsData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("v2/news/")
    fun getTopNews(
        @Query("sortOrder") sortOrder: String = "popular"
    ): Call<NewsData>

    @GET("top/totalvolfull")
    fun getTopCoins(
        @Query("tsym") to_symbol :String = "USD" ,
        @Query("limit") limit_data :Int = 45
    ) :Call<CoinsData>

    @GET("{period}")
    fun getChartData(
        @Path("period") period :String,
        @Query("fsym") fromSymbol :String,
        @Query("limit") limit :Int,
        @Query("aggregate")  aggregate:Int,
        @Query("tsym") toSymbol :String = "USD"
    ) :Call<ChartData>
}