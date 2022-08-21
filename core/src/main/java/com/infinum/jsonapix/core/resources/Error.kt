package com.infinum.jsonapix.core.resources

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Error(
    @SerialName("error") val data: Data
) {
    @Serializable
    data class Data(
        @SerialName("code") val code: String,
        @SerialName("title") val title: String,
        @SerialName("detail") val detail: String,
        @SerialName("status") val status: String
    )
}
