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

package com.arthurivanets.googleplayscraper.util

import com.arthurivanets.googleplayscraper.specs.Spec
import com.google.gson.JsonElement
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.Option
import com.jayway.jsonpath.spi.json.GsonJsonProvider
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider

internal class JsonPathProcessor : PathProcessor<JsonElement> {

    private val jsonPathConfig by lazy {
        Configuration.builder()
            .jsonProvider(GsonJsonProvider())
            .mappingProvider(GsonMappingProvider())
            .options(Option.SUPPRESS_EXCEPTIONS)
            .build()
    }

    private val parser by lazy { JsonPath.using(jsonPathConfig) }

    override fun <T> extract(jsonString: String, path: Path): T? {
        return parser.parse(jsonString).read(path.toJsonPath())
    }

    override fun <T> extract(jsonElement: JsonElement, path: Path): T? {
        return parser.parse(jsonElement).read(path.toJsonPath())
    }

    override fun extract(jsonString: String, spec: Spec): Map<String, JsonElement> {
        return parser.parse(jsonString).extractParams(spec)
    }

    override fun extract(jsonElement: JsonElement, spec: Spec): Map<String, JsonElement> {
        return parser.parse(jsonElement).extractParams(spec)
    }

    private fun DocumentContext.extractParams(spec: Spec): Map<String, JsonElement> {
        val extractedParams = mutableMapOf<String, JsonElement>()

        spec.attributeMappings.forEach { (paramName, path) ->
            read<JsonElement>(path.toJsonPath())?.let { value -> extractedParams[paramName] = value }
        }

        return extractedParams
    }

    private fun Path.toJsonPath(): String {
        return buildString {
            append("$")

            elements.forEachIndexed { elementIndex, element ->
                when (element) {
                    is Path.Element.Idx -> {
                        if (elementIndex == 0) {
                            append(".")
                        }

                        append("[").append(element.index).append("]")
                    }
                    is Path.Element.Key -> append("['").append(element.key).append("']")
                }
            }
        }
    }

}