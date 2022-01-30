package com.arthurivanets.googleplayscraper.util

import com.google.gson.Gson
import com.google.gson.JsonElement

internal interface ResponseJsonExtractor {

    fun extract(rawResponse: String): JsonElement

}

internal class DefaultResponseJsonExtractor(
    private val gson: Gson,
    private val scriptDataParser: ScriptDataParser,
    private val jsonNormalizer: JsonNormalizer,
): ResponseJsonExtractor {

    override fun extract(rawResponse: String): JsonElement {
        val rawJson: JsonElement = try {
            val potentiallyJsonString = (if (rawResponse.length > 6) rawResponse.substring(6) else rawResponse)

            gson.fromJson(potentiallyJsonString, JsonElement::class.java).let { result ->
                if (result.isJsonPrimitive) scriptDataParser.parse(rawResponse) else result
            }
        } catch (error: Throwable) {
            scriptDataParser.parse(rawResponse)
        }

        return jsonNormalizer.normalize(rawJson)
    }

}