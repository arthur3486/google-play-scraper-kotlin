/*
 * Copyright 2021 Arthur Ivanets, arthur.ivanets.work@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arthurivanets.googleplayscraper.requests

import com.arthurivanets.googleplayscraper.model.AppReview
import com.arthurivanets.googleplayscraper.parsers.ResultParser
import com.arthurivanets.googleplayscraper.util.PagedResult
import com.arthurivanets.googleplayscraper.util.fetchContinuously
import com.arthurivanets.googleplayscraper.util.ScraperError
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Request as OkHttpRequest
import okhttp3.Response as OkHttpResponse

class GetAppReviewsParams(
    val appId: String,
    val language: String = DefaultParams.LANGUAGE,
    val country: String = DefaultParams.COUNTRY,
    val limit: Int = DefaultParams.LIMIT,
    val sortingOrder: ReviewSortingOrder = ReviewSortingOrder.NEWEST
)

internal class GetAppReviewsRequest(
    private val params: GetAppReviewsParams,
    private val baseUrl: String,
    private val httpClient: OkHttpClient,
    private val requestResultParser: ResultParser<String, PagedResult<List<AppReview>>>
) : Request<List<AppReview>> {

    override fun execute(): Response<List<AppReview>, ScraperError> = response {
        fetchContinuously(
            itemCount = params.limit,
            initialRequestExecutor = { executeRequest(null) },
            initialRequestResultParser = requestResultParser,
            subsequentRequestExecutor = ::executeRequest,
            subsequentRequestResultParser = requestResultParser
        ).map { review -> review.appendDetails() }
    }

    private fun executeRequest(token: String?): OkHttpResponse {
        return OkHttpRequest.Builder()
            .url(createRequestUrl())
            .post(createRequestBody(token))
            .build()
            .let(httpClient::newCall)
            .execute()
    }

    private fun createRequestUrl(): String {
        return buildString {
            append(baseUrl).append("/_/PlayStoreUi/data/batchexecute")
            append("?rpcids=qnKhOb")
            append("&f.sid=-697906427155521722")
            append("&bl=boq_playuiserver_20190903.08_p0")
            append("&hl=").append(params.language)
            append("&gl=").append(params.country)
            append("&authuser")
            append("&soc-app=121")
            append("&soc-platform=1")
            append("&soc-device=1")
            append("&_reqid=1065213")
        }
    }

    private fun createRequestBody(
        token: String?
    ): RequestBody {
        val numberOfReviewsPerRequest = 150
        val sortingOrder = params.sortingOrder.value
        val appId = params.appId
        val body = if (token != null) {
            "f.req=%5B%5B%5B%22UsvDTd%22%2C%22%5Bnull%2Cnull%2C%5B2%2C${sortingOrder}%2C%5B${numberOfReviewsPerRequest}%2Cnull%2C%5C%22${token}%5C%22%5D%2Cnull%2C%5B%5D%5D%2C%5B%5C%22${appId}%5C%22%2C7%5D%5D%22%2Cnull%2C%22generic%22%5D%5D%5D"
        } else {
            "f.req=%5B%5B%5B%22UsvDTd%22%2C%22%5Bnull%2Cnull%2C%5B2%2C${sortingOrder}%2C%5B${numberOfReviewsPerRequest}%2Cnull%2Cnull%5D%2Cnull%2C%5B%5D%5D%2C%5B%5C%22${appId}%5C%22%2C7%5D%5D%22%2Cnull%2C%22generic%22%5D%5D%5D"
        }
        val mediaType = "application/x-www-form-urlencoded;charset=UTF-8"

        return body.toRequestBody(mediaType.toMediaType())
    }

    private fun AppReview.appendDetails(): AppReview {
        return this.copy(
            appId = params.appId,
            url = "$baseUrl/store/apps/details?id=${params.appId}&reviewId=${this.id}"
        )
    }

}