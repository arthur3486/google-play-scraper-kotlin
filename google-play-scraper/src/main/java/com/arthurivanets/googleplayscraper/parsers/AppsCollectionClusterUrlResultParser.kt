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

import com.arthurivanets.googleplayscraper.model.App
import com.arthurivanets.googleplayscraper.model.Collection
import com.arthurivanets.googleplayscraper.modelfactories.ModelFactory
import com.arthurivanets.googleplayscraper.util.ScraperError
import com.arthurivanets.googleplayscraper.specs.AppSpec
import com.arthurivanets.googleplayscraper.specs.CollectionsSpec
import com.arthurivanets.googleplayscraper.util.Path
import com.arthurivanets.googleplayscraper.util.PathProcessor
import com.arthurivanets.googleplayscraper.util.ResponseJsonExtractor
import com.arthurivanets.googleplayscraper.util.inferType
import com.google.gson.JsonArray
import com.google.gson.JsonElement

internal class AppsCollectionClusterUrlResultParser(
    private val allCollectionsSpec: Path,
    private val collectionsNewSpec: CollectionsSpec,
    private val collectionsTopSpec: CollectionsSpec,
    private val appSpec: AppSpec,
    private val responseJsonExtractor: ResponseJsonExtractor,
    private val pathProcessor: PathProcessor<JsonElement>,
    private val appModelFactory: ModelFactory<AppSpec, JsonElement, App>,
) : ResultParser<Pair<Collection, String>, String> {

    override fun parse(input: Pair<Collection, String>): String = input.let { (collection, rawResponse) ->
        require(rawResponse.isNotBlank()) {
            "The Raw Response MUST BE a non-blank String"
        }

        val result = responseJsonExtractor.extract(rawResponse)
        val allCollections = pathProcessor.extract<JsonArray>(result, allCollectionsSpec)

        if (allCollections == null) {
            throw ScraperError.ResponseParsingError("Collection ($collection) contains no clusters")
        }

        val spec = resolveSpec(collection)
        val collectionClusterUrlSpec = spec.clusterUrl(allCollections.size(), collection)
        val collectionClusterUrl = pathProcessor.extract<JsonElement>(
            jsonElement = allCollections,
            path = collectionClusterUrlSpec
        )?.inferType<String>()

        if (collectionClusterUrl == null) {
            throw ScraperError.ResponseParsingError("Collection ($collection) Cluster URL not found")
        }

        return if (collection == Collection.TOP_PAID) {
            if (allCollections.containsOnlyPaidApps(collection, spec)) {
                collectionClusterUrl
            } else {
                throw ScraperError.ResponseParsingError("Collection ($collection) contains free apps when it should not")
            }
        } else {
            collectionClusterUrl
        }
    }

    private fun JsonArray.containsOnlyPaidApps(collection: Collection, spec: CollectionsSpec): Boolean {
        val rawApps = pathProcessor.extract<JsonArray>(this, spec.initialApps(this.size(), collection))
        return (rawApps?.all { rawApp -> !appModelFactory.create(appSpec, rawApp).isFree } == true)
    }

    private fun resolveSpec(collection: Collection): CollectionsSpec {
        val newAppsCollections = setOf(
            Collection.NEW_FREE,
            Collection.NEW_PAID,
            Collection.NEW_FREE_GAMES,
            Collection.NEW_PAID_GAMES,
        )

        return if (collection in newAppsCollections) {
            collectionsNewSpec
        } else {
            collectionsTopSpec
        }
    }

}