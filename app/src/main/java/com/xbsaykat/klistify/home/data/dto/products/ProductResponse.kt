package com.xbsaykat.klistify.home.data.dto.products

data class ProductResponse(
    val limit: Int,
    val products: List<ProductData>,
    val skip: Int,
    val total: Int
)