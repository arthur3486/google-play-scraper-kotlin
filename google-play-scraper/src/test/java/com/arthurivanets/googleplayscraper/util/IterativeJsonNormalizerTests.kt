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