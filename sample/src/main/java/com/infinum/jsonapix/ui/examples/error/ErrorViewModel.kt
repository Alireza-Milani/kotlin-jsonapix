package com.infinum.jsonapix.ui.examples.error

import com.infinum.jsonapix.data.api.SampleApiService
import com.infinum.jsonapix.data.assets.JsonAssetReader
import com.infinum.jsonapix.retrofit.asJsonXHttpException
import com.infinum.jsonapix.ui.shared.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class ErrorViewModel @Inject constructor(
    private val sampleApiService: SampleApiService,
    private val jsonAssetReader: JsonAssetReader
) : BaseViewModel<ErrorState, ErrorEvent>() {

    @Suppress("UnusedPrivateMember")
    fun fetchError() {
        launch {
            showLoading()
            val bodyString = io { jsonAssetReader.readJsonAsset("responses/error.json") }
            try {
                val person = io { sampleApiService.fetchError() }
            } catch (e: HttpException) {
                showError(e.asJsonXHttpException().error?.data?.detail ?: "")
            }

            hideLoading()
        }
    }
}
