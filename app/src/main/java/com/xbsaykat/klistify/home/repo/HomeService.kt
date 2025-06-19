package com.xbsaykat.klistify.home.repo

import com.xbsaykat.klistify.home.data.dto.products.ProductResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface HomeService {

    @GET("products")
    suspend fun getProductList(
        @QueryMap queryMap: Map<String, String>
    ): Response<ProductResponse>

}