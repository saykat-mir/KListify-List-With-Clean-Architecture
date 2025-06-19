package com.xbsaykat.klistify.home.domain.repository

import com.xbsaykat.klistify.home.data.dto.products.ProductResponse
import retrofit2.Response

interface HomeRepo {
    suspend fun getProducts(queryMap: Map<String, String>): Response<ProductResponse>
}