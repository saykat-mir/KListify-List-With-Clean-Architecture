package com.xbsaykat.klistify.home.domain.usecase

import android.util.Log
import com.xbsaykat.klistify.base.utils.ErrorHandler
import com.xbsaykat.klistify.base.utils.ResponseHandler
import com.xbsaykat.klistify.home.domain.repository.HomeRepo
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repo: HomeRepo
)
{
    operator fun invoke(map: Map<String, String>) = flow {
        try {
            emit(ResponseHandler.Loading())
            val response = repo.getProducts(map)
            if(response.isSuccessful){
                emit(ResponseHandler.Success(response))
            }else {
                emit(
                    ResponseHandler.Error(
                        ErrorHandler(
                            serverError = response.raw(),
                            httpError = response.errorBody()
                        )
                    )
                )
            }
        } catch (e: HttpException) {
            emit(
                ResponseHandler.Error(
                    ErrorHandler(
                        localError = (e.localizedMessage ?: "An unexpected error occurred")
                    )
                )
            )
        } catch (e: IOException) {
            emit(
                ResponseHandler.Error(
                    ErrorHandler(
                        localError = (e.localizedMessage ?: "Internet connection error occurred")
                    )
                )
            )
        }
    }
}