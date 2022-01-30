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
import com.arthurivanets.googleplayscraper.util.fetchContinuously
import com.arthurivanets.googleplayscraper.util.peekBody
import com.arthurivanets.googleplayscraper.util.ScraperError
import okhttp3.OkHttpClient
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern
import okhttp3.Request as OkHttpRequest
import okhttp3.Response as OkHttpResponse

class SearchAppsParams(
    val query: String,
    val language: String = DefaultParams.LANGUAGE,
    val country: String = DefaultParams.COUNTRY,
    val limit: Int = DefaultParams.LIMIT
)

internal class SearchAppsRequest(
    private val params: SearchAppsParams,
    private val baseUrl: String,
    private val httpClient: OkHttpClient,
    private val appsLoadingRequestFactory: AppsLoadingRequestFactory,
    private val initialRequestResultParser: ResultParser<String, PagedResult<List<App>>>,
    private val requestResultParser: ResultParser<String, PagedResult<List<App>>>
) : Request<List<App>> {

    private companion object {

        private val CLUSTER_PAGE_PATTERN by lazy {
            Pattern.compile("href=\"(/store/apps/collection/search_collection_more_results_cluster?(.*?))\"")
        }

    }

    override fun execute(): Response<List<App>, ScraperError> = response {
        fetchContinuously(
            itemCount = params.limit,
            initialRequestExecutor = ::executeInitialRequest,
            initialRequestResultParser = initialRequestResultParser,
            subsequentRequestExecutor = ::executeRequest,
            subsequentRequestResultParser = requestResultParser
        )
    }

    private fun executeInitialRequest(): OkHttpResponse {
        fun executeRequest(url: String): OkHttpResponse {
            return OkHttpRequest.Builder()
                .url(url)
                .get()
                .build()
                .let(httpClient::newCall)
                .execute()
        }

        val response = executeRequest(createInitialRequestUrl())

        return if (response.isSuccessful) {
            // sometimes the first result page is a cluster of subsections,
            // so we need to skip to the full results page
            val body = response.peekBody().string()
            val matcher = CLUSTER_PAGE_PATTERN.matcher(body)

            if (matcher.find()) {
                executeRequest(baseUrl + matcher.group(1))
            } else {
                response
            }
        } else {
            response
        }
    }

    private fun createInitialRequestUrl(): String {
        val query = URLEncoder.encode(params.query, StandardCharsets.UTF_8)

        return buildString {
            append(baseUrl).append("/store/search")
            append("?c=apps")
            append("&q=").append(query)
            append("&hl=").append(params.language)
            append("&gl=").append(params.country)
        }
    }

    private fun executeRequest(token: String): OkHttpResponse {
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

}