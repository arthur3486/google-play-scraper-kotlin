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
        require(rawResponse.isNotBlank()) {
            "The Raw Response MUST BE a non-blank String"
        }

        val rawJson: JsonElement = try {
            val potentiallyJsonString = (if (rawResponse.length > 6) rawResponse.substring(6) else rawResponse)

            gson.fromJson(potentiallyJsonString, JsonElement::class.java).let { result ->
                if (result.isJsonPrimitive) scriptDataParser.parse(rawResponse) else result
            }
        } catch (error: Exception) {
            scriptDataParser.parse(rawResponse)
        }

        return jsonNormalizer.normalize(rawJson)
    }

}

internal class AppsResponseJsonExtractor(
    private val gson: Gson,
    private val jsonNormalizer: JsonNormalizer,
    private val defaultResponseJsonExtractor: ResponseJsonExtractor
): ResponseJsonExtractor {

    override fun extract(rawResponse: String): JsonElement {
        require(rawResponse.isNotBlank()) {
            "The Raw Response MUST BE a non-blank String"
        }

        val extractedJson: JsonElement? = try {
            rawResponse
                .split("\n")
                .getOrNull(3)
                ?.let { potentiallyJsonString -> gson.fromJson(potentiallyJsonString, JsonElement::class.java) }
                ?.let(jsonNormalizer::normalize)
        } catch (error: Exception) {
            null
        }

        return extractedJson ?: defaultResponseJsonExtractor.extract(rawResponse)
    }

}