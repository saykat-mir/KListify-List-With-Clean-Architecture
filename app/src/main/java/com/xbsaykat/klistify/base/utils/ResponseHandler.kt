package com.xbsaykat.klistify.base.utils

sealed class ResponseHandler<T>(val data: T? = null, val error: ErrorHandler? = null) {
    class Success<T>(data: T? = null) : ResponseHandler<T>(data = data)
    class Error<T>(err: ErrorHandler) : ResponseHandler<T>(error = err)
    class Loading<T>() : ResponseHandler<T>()
    class Empty<T>() : ResponseHandler<T>()
}


sealed class UIState<T>(var data: T? = null, errorType: ErrorHandler.ErrorType? = null, val error: String? = null) {
    class Success<T>(data: T? = null) : UIState<T>(data = data)
    class Error<T>(error: String, errorType: ErrorHandler.ErrorType = ErrorHandler.ErrorType.UNKNOWN) : UIState<T>(error = error, errorType = errorType)
    class Loading<T> : UIState<T>()
    class Empty<T> : UIState<T>()
}