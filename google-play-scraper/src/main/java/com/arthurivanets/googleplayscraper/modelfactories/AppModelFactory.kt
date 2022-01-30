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

import com.arthurivanets.googleplayscraper.model.App
import com.arthurivanets.googleplayscraper.specs.AppSpec
import com.arthurivanets.googleplayscraper.util.PathProcessor
import com.arthurivanets.googleplayscraper.util.getOrDefault
import com.arthurivanets.googleplayscraper.util.getOrNull
import com.arthurivanets.googleplayscraper.util.require
import com.google.gson.JsonElement
import java.util.regex.Pattern

internal class AppModelFactory(
    private val baseUrl: String,
    private val pathProcessor: PathProcessor<JsonElement>
) : ModelFactory<AppSpec, JsonElement, App> {

    companion object {

        private val PATTERN_PRICE = Pattern.compile("([0-9]+[.,][0-9]*)")
        private val PATTERN_CURRENCY = Pattern.compile("([^0-9.,\\s]+)")

    }

    override fun create(spec: AppSpec, rawItem: JsonElement): App {
        val extractedData = pathProcessor.extract(rawItem, spec)
        val priceText = extractedData.getOrNull<String>(AppSpec.Key.PRICE_TEXT)
        val price = priceText?.extractPrice()

        return App(
            appId = extractedData.require(AppSpec.Key.APP_ID),
            title = extractedData.require(AppSpec.Key.TITLE),
            summary = extractedData.getOrDefault(AppSpec.Key.SUMMARY, ""),
            score = extractedData.getOrNull(AppSpec.Key.SCORE),
            scoreText = extractedData.getOrNull(AppSpec.Key.SCORE_TEXT),
            url = (baseUrl + extractedData.require<String>(AppSpec.Key.URL)),
            iconUrl = extractedData.require(AppSpec.Key.ICON_URL),
            developer = extractedData.require(AppSpec.Key.DEVELOPER),
            developerId = extractedData.require<String>(AppSpec.Key.DEVELOPER_ID).extractDeveloperId(),
            priceText = priceText,
            price = price,
            currency = priceText?.extractCurrency(),
            isFree = (price == null)
        )
    }

    private fun String.extractDeveloperId(): String {
        return this.split("?id=").lastOrNull() ?: this
    }

    private fun String.extractPrice(): Double? {
        val matcher = PATTERN_PRICE.matcher(this)
        return if (matcher.find()) matcher.group().toDouble() else null
    }

    private fun String.extractCurrency(): String? {
        val matcher = PATTERN_CURRENCY.matcher(this)
        return if (matcher.find()) matcher.group() else null
    }

}