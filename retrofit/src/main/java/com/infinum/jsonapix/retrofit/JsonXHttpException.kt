package com.infinum.jsonapix.retrofit

import com.infinum.jsonapix.core.resources.Error
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import retrofit2.Response

data class JsonXHttpException(val response: Response<*>?, val error: Error?)

fun HttpException.asJsonXHttpException(): JsonXHttpException {
    return JsonXHttpException(
        response(),
        response()?.errorBody()?.charStream()?.readText()?.let { Json.decodeFromString<Error>(it) }
    )
}
