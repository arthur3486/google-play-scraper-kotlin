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

import com.arthurivanets.googleplayscraper.model.Permission
import com.arthurivanets.googleplayscraper.parsers.ResultParser
import com.arthurivanets.googleplayscraper.util.requireBody
import com.arthurivanets.googleplayscraper.util.ScraperError
import com.arthurivanets.googleplayscraper.util.httpError
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Request as OkHttpRequest
import okhttp3.Response as OkHttpResponse

class GetAppPermissionsParams(
    val appId: String,
    val language: String = DefaultParams.LANGUAGE,
)

internal class GetAppPermissionsRequest(
    private val params: GetAppPermissionsParams,
    private val baseUrl: String,
    private val httpClient: OkHttpClient,
    private val permissionsResultParser: ResultParser<String, List<Permission>>
) : Request<List<Permission>> {

    override fun execute(): Response<List<Permission>, ScraperError> = response {
        val response = executeRequest()

        if (response.isSuccessful) {
            response.requireBody()
                .string()
                .let(permissionsResultParser::parse)
        } else {
            throw httpError(response)
        }
    }

    private fun executeRequest(): OkHttpResponse {
        return OkHttpRequest.Builder()
            .url(createRequestUrl())
            .post(createRequestBody())
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
            append("&authuser")
            append("&soc-app=121")
            append("&soc-platform=1")
            append("&soc-device=1")
            append("&_reqid=1065213")
        }
    }

    private fun createRequestBody(): RequestBody {
        val body = "f.req=%5B%5B%5B%22xdSrCf%22%2C%22%5B%5Bnull%2C%5B%5C%22${params.appId}%5C%22%2C7%5D%2C%5B%5D%5D%5D%22%2Cnull%2C%221%22%5D%5D%5D"
        val mediaType = "application/x-www-form-urlencoded;charset=UTF-8"

        return body.toRequestBody(mediaType.toMediaType())
    }

}