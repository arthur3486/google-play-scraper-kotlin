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

import com.arthurivanets.googleplayscraper.model.AppDetails
import com.arthurivanets.googleplayscraper.parsers.ResultParser
import com.arthurivanets.googleplayscraper.util.ScraperError
import com.arthurivanets.googleplayscraper.util.consumeSafely
import com.arthurivanets.googleplayscraper.util.httpError
import com.arthurivanets.googleplayscraper.util.requireBody
import okhttp3.OkHttpClient
import okhttp3.Request as OkHttpRequest
import okhttp3.Response as OkHttpResponse

class GetAppDetailsParams(
    val appId: String,
    val language: String = DefaultParams.LANGUAGE,
    val country: String = DefaultParams.COUNTRY,
)

internal class GetAppDetailsRequest(
    private val params: GetAppDetailsParams,
    private val baseUrl: String,
    private val httpClient: OkHttpClient,
    private val requestResultParser: ResultParser<String, AppDetails>
) : Request<AppDetails> {

    override fun execute(): Response<AppDetails, ScraperError> = response {
        executeRequest().consumeSafely { response ->
            if (response.isSuccessful) {
                response.requireBody()
                    .string()
                    .let(requestResultParser::parse)
                    .appendDetails()
            } else {
                throw httpError(response)
            }
        }
    }

    private fun executeRequest(): OkHttpResponse {
        return OkHttpRequest.Builder()
            .url(createRequestUrl())
            .get()
            .build()
            .let(httpClient::newCall)
            .execute()
    }

    private fun createRequestUrl(): String {
        return buildString {
            append(baseUrl).append("/store/apps/details")
            append("?id=").append(params.appId)
            append("&hl=").append(params.language)
            append("&gl=").append(params.country)
        }
    }

    private fun AppDetails.appendDetails(): AppDetails {
        return this.copy(
            appId = params.appId,
            url = createRequestUrl()
        )
    }

}