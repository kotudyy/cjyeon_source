package com.example.chap18.retrofit

import com.example.chap18.model.PageListModel
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkService {
    @GET("/v2/everything")
    fun getList(
        @Query("q") q: String?,
        @Query("apiKey") apiKey: String?,
        @Query("page") page: Long,
        @Query("pageSize") pageSize: Int
    ): retrofit2.Call<PageListModel>
}