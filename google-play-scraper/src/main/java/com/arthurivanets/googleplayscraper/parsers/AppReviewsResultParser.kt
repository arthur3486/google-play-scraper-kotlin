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

package com.arthurivanets.googleplayscraper.parsers

import com.arthurivanets.googleplayscraper.model.AppReview
import com.arthurivanets.googleplayscraper.modelfactories.ModelFactory
import com.arthurivanets.googleplayscraper.specs.AppReviewSpec
import com.arthurivanets.googleplayscraper.specs.AppReviewsResponseSpec
import com.arthurivanets.googleplayscraper.util.PagedResult
import com.arthurivanets.googleplayscraper.util.PathProcessor
import com.arthurivanets.googleplayscraper.util.ResponseJsonExtractor
import com.google.gson.JsonArray
import com.google.gson.JsonElement

internal class AppReviewsResultParser(
    private val responseSpec: AppReviewsResponseSpec,
    private val appReviewSpec: AppReviewSpec,
    private val responseJsonExtractor: ResponseJsonExtractor,
    private val pathProcessor: PathProcessor<JsonElement>,
    private val appReviewModelFactory: ModelFactory<AppReviewSpec, JsonElement, AppReview>,
) : ResultParser<String, PagedResult<List<AppReview>>> {

    override fun parse(input: String): PagedResult<List<AppReview>> {
        require(input.isNotBlank()) {
            "The Input MUST BE a non-blank String"
        }

        val contentJson = responseJsonExtractor.extract(input)
        val parsedToken = extractToken(contentJson)
        val rawAppReviews = pathProcessor.extract<JsonArray>(contentJson, responseSpec.reviews)
        val parsedAppReviews = rawAppReviews?.map { appReview ->
            appReviewModelFactory.create(appReviewSpec, appReview)
        } ?: emptyList()

        return PagedResult(
            result = parsedAppReviews,
            nextToken = parsedToken
        )
    }

    private fun extractToken(contentJson: JsonElement): String? {
        return pathProcessor.extract<JsonElement>(contentJson, responseSpec.token)?.let { token ->
            if (token.isJsonNull) null else token.asString
        }
    }

}