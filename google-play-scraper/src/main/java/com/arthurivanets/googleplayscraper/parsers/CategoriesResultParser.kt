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

import com.arthurivanets.googleplayscraper.model.Category
import com.arthurivanets.googleplayscraper.modelfactories.ModelFactory
import com.arthurivanets.googleplayscraper.specs.CategoriesResponseSpec
import com.arthurivanets.googleplayscraper.specs.CategorySpec
import com.arthurivanets.googleplayscraper.util.PathProcessor
import com.arthurivanets.googleplayscraper.util.ResponseJsonExtractor
import com.google.gson.JsonArray
import com.google.gson.JsonElement

internal class CategoriesResultParser(
    private val responseSpec: CategoriesResponseSpec,
    private val categorySpec: CategorySpec,
    private val pathProcessor: PathProcessor<JsonElement>,
    private val responseJsonExtractor: ResponseJsonExtractor,
    private val categoryModelFactory: ModelFactory<CategorySpec, JsonElement, Category>,
) : ResultParser<String, List<Category>> {

    override fun parse(input: String): List<Category> {
        require(input.isNotBlank()) {
            "The Input MUST BE a non-blank String"
        }

        val contentJson = responseJsonExtractor.extract(input)
        val rawAppCategories = pathProcessor.extract<JsonArray>(contentJson, responseSpec.apps)
        val rawGameCategories = pathProcessor.extract<JsonArray>(contentJson, responseSpec.games)
        val rawFamilyCategories = pathProcessor.extract<JsonArray>(contentJson, responseSpec.family)
        val appCategories = extractCategories(rawAppCategories)
        val gameCategories = extractCategories(rawGameCategories)
        val familyCategories = extractCategories(rawFamilyCategories)

        return (appCategories + gameCategories + familyCategories)
    }

    private fun extractCategories(rawCategories: JsonArray?): List<Category> {
        return rawCategories?.map { category -> categoryModelFactory.create(categorySpec, category) } ?: emptyList()
    }

}