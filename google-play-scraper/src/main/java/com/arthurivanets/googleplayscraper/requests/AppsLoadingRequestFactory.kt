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

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

internal fun interface AppsLoadingRequestFactory {

    class Input(
        val limit: Int,
        val paginationToken: String,
        val country: String,
        val language: String,
    )

    fun create(input: Input): Request

}

internal class DefaultAppsLoadingRequestFactory(
    private val baseUrl: String
) : AppsLoadingRequestFactory {

    override fun create(input: AppsLoadingRequestFactory.Input): Request {
        return Request.Builder()
            .url(createRequestUrl(input))
            .post(createRequestBody(input))
            .build()
    }

    private fun createRequestUrl(input: AppsLoadingRequestFactory.Input): String {
        return buildString {
            append(baseUrl).append("/_/PlayStoreUi/data/batchexecute")
            append("?rpcids=qnKhOb")
            append("&f.sid=-697906427155521722")
            append("&bl=boq_playuiserver_20190903.08_p0")
            append("&hl=").append(input.language)
            append("&gl=").append(input.country)
            append("&authuser")
            append("&soc-app=121")
            append("&soc-platform=1")
            append("&soc-device=1")
            append("&_reqid=1065213")
        }
    }

    private fun createRequestBody(input: AppsLoadingRequestFactory.Input): RequestBody {
        val body = "f.req=%5B%5B%5B%22qnKhOb%22%2C%22%5B%5Bnull%2C%5B%5B10%2C%5B10%2C${input.limit}%5D%5D%2Ctrue%2Cnull%2C%5B96%2C27%2C4%2C8%2C57%2C30%2C110%2C79%2C11%2C16%2C49%2C1%2C3%2C9%2C12%2C104%2C55%2C56%2C51%2C10%2C34%2C77%5D%5D%2Cnull%2C%5C%22${input.paginationToken}%5C%22%5D%5D%22%2Cnull%2C%22generic%22%5D%5D%5D"
        val mediaType = "application/x-www-form-urlencoded;charset=UTF-8"

        return body.toRequestBody(mediaType.toMediaType())
    }

}