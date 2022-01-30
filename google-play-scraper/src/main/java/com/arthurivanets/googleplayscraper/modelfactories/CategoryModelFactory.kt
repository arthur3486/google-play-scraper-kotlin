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

package com.arthurivanets.googleplayscraper.modelfactories

import com.arthurivanets.googleplayscraper.model.Category
import com.arthurivanets.googleplayscraper.specs.CategorySpec
import com.arthurivanets.googleplayscraper.util.PathProcessor
import com.arthurivanets.googleplayscraper.util.require
import com.google.gson.JsonElement

internal class CategoryModelFactory(
    private val pathProcessor: PathProcessor<JsonElement>
) : ModelFactory<CategorySpec, JsonElement, Category> {

    override fun create(spec: CategorySpec, rawItem: JsonElement): Category {
        val extractedData = pathProcessor.extract(rawItem, spec)

        return Category(
            id = normalizeId(extractedData.require(CategorySpec.Key.ID)),
            title = extractedData.require(CategorySpec.Key.TITLE),
        )
    }

    private fun normalizeId(rawId: String): String {
        return rawId.split("/").lastOrNull() ?: rawId
    }

}