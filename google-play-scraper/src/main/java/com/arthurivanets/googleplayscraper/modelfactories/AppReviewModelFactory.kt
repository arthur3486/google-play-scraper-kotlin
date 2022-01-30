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

import com.arthurivanets.googleplayscraper.model.AppReview
import com.arthurivanets.googleplayscraper.specs.AppReviewSpec
import com.arthurivanets.googleplayscraper.util.PathProcessor
import com.arthurivanets.googleplayscraper.util.getOrDefault
import com.arthurivanets.googleplayscraper.util.getOrNull
import com.arthurivanets.googleplayscraper.util.require
import com.arthurivanets.googleplayscraper.util.utcZoneId
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import java.time.Instant
import java.time.OffsetDateTime

internal class AppReviewModelFactory(
    private val pathProcessor: PathProcessor<JsonElement>
) : ModelFactory<AppReviewSpec, JsonElement, AppReview> {

    override fun create(spec: AppReviewSpec, rawItem: JsonElement): AppReview {
        val extractedData = pathProcessor.extract(rawItem, spec)
        val rawScore = extractedData.require<Int>(AppReviewSpec.Key.SCORE)

        return AppReview(
            id = extractedData.require(AppReviewSpec.Key.ID),
            url = "",
            authorUsername = extractedData.require(AppReviewSpec.Key.AUTHOR_USERNAME),
            authorImageUrl = extractedData.require(AppReviewSpec.Key.AUTHOR_IMAGE_URL),
            timestamp = extractedData.require<JsonArray>(AppReviewSpec.Key.TIMESTAMP).toTimestamp(),
            score = rawScore,
            scoreText = rawScore.toString(),
            title = extractedData.getOrDefault(AppReviewSpec.Key.TITLE, ""),
            text = extractedData.require(AppReviewSpec.Key.TEXT),
            replyTimestamp = extractedData.getOrNull<JsonArray>(AppReviewSpec.Key.REPLY_TIMESTAMP)?.toTimestamp(),
            replyText = extractedData.getOrNull(AppReviewSpec.Key.REPLY_TEXT),
            appVersion = extractedData.getOrNull(AppReviewSpec.Key.APP_VERSION),
            appId = "",
            thumbsUpCount = extractedData.require(AppReviewSpec.Key.THUMBS_UP_COUNT),
            criteria = (extractedData.getOrNull<JsonArray>(AppReviewSpec.Key.CRITERIA)?.toCriteriaList() ?: emptyList())
        )
    }

    private fun JsonArray.toTimestamp(): OffsetDateTime {
        val secondsPart = this.get(0).asLong
        val millisecondsPart = if (this.size() > 1) {
            this.get(1).asString.substring(0, 3)
        } else {
            "000"
        }
        val rawTimestampMillis = "$secondsPart$millisecondsPart".toLong()

        return OffsetDateTime.ofInstant(Instant.ofEpochMilli(rawTimestampMillis), utcZoneId())
    }

    private fun JsonArray.toCriteriaList(): List<String> {
        return this.map { item -> item.asJsonArray.get(0).asString }
    }

}