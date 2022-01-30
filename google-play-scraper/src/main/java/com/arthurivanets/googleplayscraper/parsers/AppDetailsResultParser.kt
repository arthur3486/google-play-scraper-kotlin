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

import com.arthurivanets.googleplayscraper.model.AppDetails
import com.arthurivanets.googleplayscraper.modelfactories.ModelFactory
import com.arthurivanets.googleplayscraper.specs.AppDetailsSpec
import com.arthurivanets.googleplayscraper.util.ResponseJsonExtractor
import com.google.gson.JsonElement

internal class AppDetailsResultParser(
    private val appDetailsSpec: AppDetailsSpec,
    private val responseJsonExtractor: ResponseJsonExtractor,
    private val appDetailsModelFactory: ModelFactory<AppDetailsSpec, JsonElement, AppDetails>,
) : ResultParser<String, AppDetails> {

    override fun parse(input: String): AppDetails {
        require(input.isNotBlank()) {
            "The Input MUST BE a non-blank String"
        }

        val result = responseJsonExtractor.extract(input)

        return appDetailsModelFactory.create(appDetailsSpec, result)
    }

}