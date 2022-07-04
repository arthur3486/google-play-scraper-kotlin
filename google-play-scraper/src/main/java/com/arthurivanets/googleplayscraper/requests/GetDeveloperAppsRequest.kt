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

import com.arthurivanets.googleplayscraper.model.App
import com.arthurivanets.googleplayscraper.parsers.ResultParser
import com.arthurivanets.googleplayscraper.util.PagedResult
import com.arthurivanets.googleplayscraper.util.ScraperError
import com.arthurivanets.googleplayscraper.util.fetchContinuously
import okhttp3.OkHttpClient
import okhttp3.Request as OkHttpRequest
import okhttp3.Response as OkHttpResponse

class GetDeveloperAppsParams(
    val devId: String,
    val language: String = DefaultParams.LANGUAGE,
    val country: String = DefaultParams.COUNTRY,
    val limit: Int = DefaultParams.LIMIT
)

internal class GetDeveloperAppsRequest(
    private val params: GetDeveloperAppsParams,
    private val baseUrl: String,
    private val httpClient: OkHttpClient,
    private val appsLoadingRequestFactory: AppsLoadingRequestFactory,
    private val initialAppsResultParser: ResultParser<String, PagedResult<List<App>>>,
    private val appsResultParser: ResultParser<String, PagedResult<List<App>>>
) : Request<List<App>> {

    override fun execute(): Response<List<App>, ScraperError> = response {
        fetchContinuously(
            itemCount = params.limit,
            initialRequestExecutor = ::executeInitialAppsRequest,
            initialRequestResultParser = initialAppsResultParser,
            subsequentRequestExecutor = ::executeAppsRequest,
            subsequentRequestResultParser = appsResultParser
        )
    }

    private fun executeInitialAppsRequest(): OkHttpResponse {
        return OkHttpRequest.Builder()
            .url(createInitialAppsRequestUrl())
            .get()
            .build()
            .let(httpClient::newCall)
            .execute()
    }

    private fun executeAppsRequest(token: String): OkHttpResponse {
        return appsLoadingRequestFactory.create(
            AppsLoadingRequestFactory.Input(
                limit = 50,
                paginationToken = token,
                country = params.country,
                language = params.language
            )
        )
            .let(httpClient::newCall)
            .execute()
    }

    private fun createInitialAppsRequestUrl(): String {
        return buildString {
            append(baseUrl).append("/store/apps")

            if (params.devId.toLongOrNull() != null) {
                append("/dev")
            } else {
                append("/developer")
            }

            append("?id=").append(params.devId)
            append("&hl=").append(params.language)
            append("&gl=").append(params.country)
        }
    }

}