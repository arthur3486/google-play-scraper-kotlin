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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class IterativeJsonNormalizerTests {

    private val jsonNormalizer = IterativeJsonNormalizer(Gson())

    @Test
    fun `normalization of nested structures`() {
        val jsonTree = readResourceAsJson("json_normalizer/user_with_embedded_info.json").asJsonObject
        val normalizedJsonTree = jsonNormalizer.normalize(jsonTree).asJsonObject

        assertEquals("john.doe", jsonTree["username"].asString)

        // middle names
        val rawMiddleNames = jsonTree["name"].asJsonObject["middle_names"]
        val middleNames = normalizedJsonTree["name"].asJsonObject["middle_names"]
        val expectedMiddleNames = listOf("Johny", "Mark", "Josh")

        assertTrue(rawMiddleNames.isJsonPrimitive)
        assertTrue(middleNames.isJsonArray)

        val middleNamesArray = middleNames.asJsonArray

        expectedMiddleNames.forEachIndexed { index, name ->
            assertTrue(middleNamesArray[index].isJsonPrimitive)
            assertEquals(name, middleNamesArray[index].asString)
        }

        // meta
        val rawMeta = jsonTree["meta"]
        val meta = normalizedJsonTree["meta"].asJsonObject

        assertTrue(rawMeta.isJsonPrimitive)
        assertTrue(meta["var0"].isJsonArray)
        assertTrue(meta["var1"].isJsonArray)
        assertEquals(1, meta["var0"].asJsonArray[0].asLong)
        assertEquals(2, meta["var1"].asJsonArray[0].asLong)
    }

}