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

package com.arthurivanets.googleplayscraper.specs

import com.arthurivanets.googleplayscraper.util.Path

internal class AppSpec(
    val appId: Path,
    val title: Path,
    val summary: Path,
    val score: Path,
    val scoreText: Path,
    val url: Path,
    val iconUrl: Path,
    val developer: Path,
    val priceText: Path,
    val price: Path,
    val currency: Path,
) : Spec {

    override val attributeMappings = mapOf(
        Key.APP_ID to appId,
        Key.TITLE to title,
        Key.SUMMARY to summary,
        Key.SCORE to score,
        Key.SCORE_TEXT to scoreText,
        Key.URL to url,
        Key.ICON_URL to iconUrl,
        Key.DEVELOPER to developer,
        Key.PRICE_TEXT to priceText,
        Key.PRICE to price,
        Key.CURRENCY to currency
    )

    object Key {

        const val APP_ID = "app_id"
        const val TITLE = "title"
        const val SUMMARY = "summary"
        const val SCORE = "score"
        const val SCORE_TEXT = "score_text"
        const val URL = "url"
        const val ICON_URL = "icon_url"
        const val DEVELOPER = "developer"
        const val PRICE_TEXT = "price_text"
        const val PRICE = "price"
        const val CURRENCY = "currency"

    }

}