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

import com.arthurivanets.googleplayscraper.model.Category
import com.arthurivanets.googleplayscraper.parsers.ResultParser
import com.arthurivanets.googleplayscraper.util.requireBody
import com.arthurivanets.googleplayscraper.util.ScraperError
import com.arthurivanets.googleplayscraper.util.httpError
import okhttp3.OkHttpClient
import okhttp3.Request as OkHttpRequest
import okhttp3.Response as OkHttpResponse

internal class GetCategoriesRequest(
    private val baseUrl: String,
    private val httpClient: OkHttpClient,
    private val requestResultParser: ResultParser<String, List<Category>>
) : Request<List<Category>> {

    override fun execute(): Response<List<Category>, ScraperError> = response {
        val response = executeRequest()

        if (response.isSuccessful) {
            response.requireBody()
                .string()
                .let(requestResultParser::parse)
        } else {
            throw httpError(response)
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
        return "$baseUrl/store/apps"
    }

}