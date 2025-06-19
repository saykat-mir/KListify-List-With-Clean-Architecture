package com.xbsaykat.klistify.base.utils

import android.text.TextUtils
import okhttp3.Response
import okhttp3.ResponseBody
import org.json.JSONObject

class ErrorHandler(
    val serverError: Response? = null,
    val localError: String? = null,
    val httpError: ResponseBody? = null
) {
    val msg: String
    var code: Int = -1
    val msgWithCode: String
    var technicalError: String = ""
    var curl: String? = ""

    init {
        if (httpError != null) {
            val (code, msg) = getHttpError(httpError.string())
            this.code = code
            this.msg = msg
        } else if (serverError != null) {
            code = serverError.code
            curl = "${serverError.request.url.host}${serverError.request.url.encodedPath}"
            msg = handleMessage(code, serverError.message)
        } else {
            msg = localError ?: "Unknown Error!"
            technicalError = "Locally error, $msg"
        }
        msgWithCode = "Code: $code, Error: $msg"
    }

    private fun handleMessage(code: Int, message: String): String {
        technicalError = message
        if (!TextUtils.isEmpty(message)) return message

        return when (code) {
            in 400..499 -> {
                "Unauthorized"
            }

            in 500..599 -> {
                "Server Error.Try after sometime"
            }

            else -> {
                "Unknown Error!"
            }
        }
    }

    private fun getHttpError(error: String): Pair<Int, String> {
        return try {
            val jsonObject = JSONObject(error)
            val message = jsonObject.getString("message")
            val code = jsonObject.getInt("code")
            Pair(code, message)
        } catch (e: Exception) {
            val msg = localError ?: handleMessage(code, "")
            Pair(code, msg)
        }
    }

    enum class ErrorType {
        SERVER_ERROR,
        NO_INTERNET,
        JSON_ERROR,
        HTTP_ERROR,
        EMPTY_DATA,
        UNKNOWN
    }
}