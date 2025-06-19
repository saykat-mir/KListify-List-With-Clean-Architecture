package com.xbsaykat.klistify.home.data.model

data class ProductResponse(
    val limit: Int,
    val products: List<ProductData>,
    val skip: Int,
    val total: Int
)