package com.xbsaykat.klistify.home.data.repository

import com.xbsaykat.klistify.home.data.dto.products.ProductResponse
import com.xbsaykat.klistify.home.domain.repository.HomeRepo
import com.xbsaykat.klistify.home.repo.HomeService
import retrofit2.Response
import javax.inject.Inject

class HomeRepoImp @Inject constructor(
    private val homeApiService: HomeService
): HomeRepo {
    override suspend fun getProducts(queryMap: Map<String, String>): Response<ProductResponse> {
        return homeApiService.getProductList(queryMap)
    }
}