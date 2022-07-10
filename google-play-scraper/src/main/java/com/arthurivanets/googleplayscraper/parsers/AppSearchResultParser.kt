/*
 * Copyright 2022 Arthur Ivanets, arthur.ivanets.work@gmail.com
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

import com.arthurivanets.googleplayscraper.model.App
import com.arthurivanets.googleplayscraper.modelfactories.ModelFactory
import com.arthurivanets.googleplayscraper.specs.AppSearchResponseSpec
import com.arthurivanets.googleplayscraper.specs.AppSpec
import com.arthurivanets.googleplayscraper.util.PagedResult
import com.arthurivanets.googleplayscraper.util.Path
import com.arthurivanets.googleplayscraper.util.PathProcessor
import com.arthurivanets.googleplayscraper.util.ResponseJsonExtractor
import com.google.gson.JsonArray
import com.google.gson.JsonElement

internal class AppSearchResultParser(
    private val responseSpec: AppSearchResponseSpec,
    private val featuredAppSpec: AppSpec,
    private val appSpec: AppSpec,
    private val responseJsonExtractor: ResponseJsonExtractor,
    private val pathProcessor: PathProcessor<JsonElement>,
    private val appModelFactory: ModelFactory<AppSpec, JsonElement, App>,
) : ResultParser<String, PagedResult<List<App>>> {

    override fun parse(input: String): PagedResult<List<App>> {
        require(input.isNotBlank()) {
            "The Input MUST BE a non-blank String"
        }

        val contentJson = responseJsonExtractor.extract(input)

        return when {
            hasNoResults(contentJson) -> PagedResult(emptyList(), null)
            hasFeaturedApp(contentJson) -> parseResultWithFeaturedApp(contentJson)
            else -> parseResult(contentJson)
        }
    }

    private fun parseResultWithFeaturedApp(contentJson: JsonElement): PagedResult<List<App>> {
        val featuredApps = pathProcessor.extract<JsonElement>(contentJson, responseSpec.featuredApp)
            ?.let { rawApp -> appModelFactory.create(featuredAppSpec, rawApp) }
            ?.let(::listOf)
            ?: emptyList()

        return PagedResult(
            result = (featuredApps + extractApps(contentJson, responseSpec.moreApps)),
            nextToken = null
        )
    }

    private fun parseResult(contentJson: JsonElement): PagedResult<List<App>> {
        return PagedResult(
            result = extractApps(contentJson, responseSpec.apps),
            nextToken = extractToken(contentJson)
        )
    }

    private fun hasNoResults(contentJson: JsonElement): Boolean {
        val indicator = pathProcessor.extract<JsonElement>(contentJson, responseSpec.noResultsSectionIndicator)
        return (indicator?.isJsonPrimitive == true)
    }

    private fun hasFeaturedApp(contentJson: JsonElement): Boolean {
        val indicator = pathProcessor.extract<JsonElement>(contentJson, responseSpec.featuredAppsSectionIndicator)
        return (indicator?.isJsonPrimitive == true)
    }

    private fun extractApps(contentJson: JsonElement, spec: Path): List<App> {
        val rawApps = pathProcessor.extract<JsonArray>(contentJson, spec)
        return rawApps?.map { app -> appModelFactory.create(appSpec, app) } ?: emptyList()
    }

    private fun extractToken(contentJson: JsonElement): String? {
        return pathProcessor.extract<JsonElement>(contentJson, responseSpec.moreResultsToken)?.let { token ->
            if (token.isJsonNull) null else token.asString
        }
    }

}