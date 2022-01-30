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
import com.arthurivanets.googleplayscraper.model.Collection
import com.arthurivanets.googleplayscraper.parsers.ResultParser
import com.arthurivanets.googleplayscraper.util.*
import okhttp3.OkHttpClient
import okhttp3.Request as OkHttpRequest
import okhttp3.Response as OkHttpResponse

class GetAppsParams(
    val category: String? = null,
    val collection: Collection = Collection.TOP_FREE,
    val language: String = DefaultParams.LANGUAGE,
    val country: String = DefaultParams.COUNTRY,
    val limit: Int = DefaultParams.LIMIT
)

internal class GetAppsRequest(
    private val params: GetAppsParams,
    private val baseUrl: String,
    private val httpClient: OkHttpClient,
    private val appsLoadingRequestFactory: AppsLoadingRequestFactory,
    private val collectionUrlResolver: CollectionUrlResolver,
    private val collectionClusterUrlResultParser: ResultParser<Pair<Collection, String>, String>,
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
        val clusterUrl = getClusterUrl()

        return OkHttpRequest.Builder()
            .url(clusterUrl)
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

    private fun getClusterUrl(): String {
        val response = executeClusterUrlRequest()

        return if (response.isSuccessful) {
            val rawBody = response.requireBody().string()
            val clusterUrl = collectionClusterUrlResultParser.parse(params.collection to rawBody)

            buildString {
                append(baseUrl).append(clusterUrl)
                append("&gl=").append(params.country)
                append("&hl=").append(params.language)
            }
        } else {
            throw httpError(response)
        }
    }

    private fun executeClusterUrlRequest(): OkHttpResponse {
        return OkHttpRequest.Builder()
            .url(createClusterRequestUrl())
            .get()
            .build()
            .let(httpClient::newCall)
            .execute()
    }

    private fun createClusterRequestUrl(): String {
        return buildString {
            append(resolveCollectionUrl())
            append("?hl=").append(params.language)
            append("&gl=").append(params.country)
        }
    }

    private fun resolveCollectionUrl(): String {
        val collectionUrl = collectionUrlResolver(params.collection)

        return if (params.category != null) {
            "$collectionUrl/category/${params.category}"
        } else {
            collectionUrl
        }
    }

}