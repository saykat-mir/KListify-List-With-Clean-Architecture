package com.xbsaykat.klistify.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xbsaykat.klistify.base.utils.ResponseHandler
import com.xbsaykat.klistify.home.domain.usecase.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
): ViewModel() {

    fun getProducts(start: Int = 10, skip: Int = 0) {
        val map = mapOf(
            "limit" to start.toString(),
            "skip" to skip.toString(),
            "sortBy" to "price,title",
            "order" to "asc"
        )

        getProductsUseCase(map).onEach { response ->
            when(response){
                is ResponseHandler.Loading -> {

                }
                is ResponseHandler.Success -> {

                }
                is ResponseHandler.Error -> {

                }
                else -> Unit
            }
        }.launchIn(viewModelScope)
    }
}