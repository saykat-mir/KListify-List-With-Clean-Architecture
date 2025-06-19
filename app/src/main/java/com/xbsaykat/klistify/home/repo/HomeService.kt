package com.xbsaykat.klistify.home.repo

import com.xbsaykat.klistify.home.data.model.ProductResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.QueryMap

interface HomeService {

    @GET("products")
    suspend fun getProductList(
        @HeaderMap headerMap: Map<String, String>,
        @QueryMap queryMap: Map<String, String>
    ): Response<ProductResponse>

}