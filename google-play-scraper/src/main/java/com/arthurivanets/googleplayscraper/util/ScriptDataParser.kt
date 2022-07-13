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

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.util.regex.Pattern

internal interface ScriptDataParser {

    fun parse(rawHtmlResponse: String): JsonObject

}

internal class DefaultScriptDataParser(private val gson: Gson) : ScriptDataParser {

    companion object {

        private val PATTERN_SCRIPT by lazy {
            Pattern.compile(
                ">AF_initDataCallback[\\s\\S]*?</script",
                Pattern.MULTILINE
            )
        }
        private val PATTERN_KEY by lazy { Pattern.compile("'(ds:.*?)'") }
        private val PATTERN_VALUE by lazy { Pattern.compile("data:([\\s\\S]*?), sideChannel: \\{\\}\\}\\);</") }

    }

    override fun parse(rawHtmlResponse: String): JsonObject {
        require(rawHtmlResponse.isNotBlank()) {
            "HTML Response MUST a Non-empty string"
        }

        val outObject = JsonObject()
        val scriptMatcher = PATTERN_SCRIPT.matcher(rawHtmlResponse)

        while (scriptMatcher.find()) {
            val matchedData = scriptMatcher.group()
            val keyMatcher = PATTERN_KEY.matcher(matchedData)
            val valueMatcher = PATTERN_VALUE.matcher(matchedData)

            if (keyMatcher.find() && valueMatcher.find()) {
                val key = keyMatcher.group(1)
                val value = valueMatcher.group(1)
                val jsonValue = gson.fromJson(value, JsonElement::class.java)

                outObject.add(key, jsonValue)
            }
        }

        return outObject
    }

}