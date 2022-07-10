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

import com.arthurivanets.googleplayscraper.model.AppDetails
import com.arthurivanets.googleplayscraper.specs.AppDetailsSpec
import com.arthurivanets.googleplayscraper.specs.AppDetailsSpec.Key
import com.arthurivanets.googleplayscraper.util.*
import com.arthurivanets.googleplayscraper.util.getOrDefault
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.associate
import kotlin.collections.emptyList
import kotlin.collections.firstOrNull
import kotlin.collections.lastOrNull
import kotlin.collections.map
import kotlin.collections.mapIndexed
import kotlin.collections.toMap

internal class AppDetailsModelFactory(
    private val pathProcessor: PathProcessor<JsonElement>
) : ModelFactory<AppDetailsSpec, JsonElement, AppDetails> {

    companion object {

        private val RELEASE_DATE_PATTERN = DateTimeFormatter.ofPattern("MMM d, yyyy")

    }

    override fun create(spec: AppDetailsSpec, rawItem: JsonElement): AppDetails {
        val extractedData = pathProcessor.extract(rawItem, spec)
        val rawPrice = extractedData.getOrDefault(Key.PRICE, 0L)
        val isAppFree = (rawPrice == 0L)
        val rawAppVersion = extractedData.getOrNull<String>(Key.ANDROID_VERSION)
        val screenshotUrlsContainer = extractedData.getOrNull<JsonArray>(Key.SCREENSHOTS_URLS_CONTAINER)
        val rawRatingsHistogram = extractedData.getOrNull<JsonArray>(Key.RATINGS_HISTOGRAM)
        val rawReleaseDate = extractedData.require<String>(Key.RELEASE_DATE)
        val rawLastUpdateTimestamp = (extractedData.getOrDefault(Key.LAST_UPDATE_TIMESTAMP, 0L) * 1000L)

        return AppDetails(
            appId = "",
            url = "",
            title = extractedData.require(Key.TITLE),
            descriptionHtml = extractedData.require(Key.DESCRIPTION),
            summary = extractedData.getOrDefault(Key.SUMMARY, ""),
            installs = extractedData.getOrDefault(Key.INSTALLS, "0"),
            minInstalls = extractedData.getOrDefault(Key.MIN_INSTALLS, 0L),
            maxInstalls = extractedData.getOrDefault(Key.MAX_INSTALLS, 0L),
            score = extractedData.getOrNull(Key.SCORE),
            scoreText = extractedData.getOrNull(Key.SCORE_TEXT),
            ratingCount = extractedData.getOrDefault(Key.RATING_COUNT, 0L),
            reviewCount = extractedData.getOrDefault(Key.REVIEW_COUNT, 0L),
            ratingsHistogram = createRatingsHistogram(rawRatingsHistogram),
            priceText = (if (isAppFree) null else extractedData.getOrNull(Key.PRICE_TEXT)),
            price = (if (isAppFree) null else (rawPrice / 1_000_000.0)),
            currency = (if (isAppFree) null else extractedData.getOrNull(Key.CURRENCY)),
            isFree = isAppFree,
            isAvailable = (extractedData.getOrNull<String>(Key.IS_AVAILABLE) != null),
            offersInAppPurchases = (extractedData.getOrNull<String>(Key.OFFERS_IAP) != null),
            inAppPurchasesPriceRange = extractedData.getOrNull(Key.IAP_PRICE_RANGE),
            appSize = "",//TODO Unavailable since 05/2022: extractedData.require(Key.APP_SIZE),
            androidVersion = rawAppVersion?.normalizeAndroidVersion(),
            androidVersionText = rawAppVersion,
            androidMinSdkVersion = extractedData.getOrNull(Key.ANDROID_MIN_SDK_VERSION),
            androidTargetSdkVersion = extractedData.getOrNull(Key.ANDROID_TARGET_SDK_VERSION),
            developer = extractedData.require(Key.DEVELOPER),
            developerId = extractedData.require<String>(Key.DEVELOPER_ID).extractDeveloperId(),
            developerInternalId = extractedData.require(Key.DEVELOPER_INTERNAL_ID),
            developerEmail = extractedData.require(Key.DEVELOPER_EMAIL),
            developerWebsite = extractedData.getOrNull(Key.DEVELOPER_WEBSITE),
            developerAddress = extractedData.getOrNull(Key.DEVELOPER_ADDRESS),
            privacyPolicyUrl = extractedData.getOrNull(Key.PRIVACY_POLICY_URL),
            genre = extractedData.require(Key.GENRE),
            genreId = extractedData.require(Key.GENRE_ID),
            familyGenre = extractedData.getOrNull(Key.FAMILY_GENRE),
            familyGenreId = extractedData.getOrNull(Key.FAMILY_GENRE_ID),
            iconUrl = extractedData.require(Key.ICON_URL),
            headerImageUrl = extractedData.require(Key.HEADER_IMAGE_URL),
            screenshotsUrls = (screenshotUrlsContainer?.extractScreenshotUrls(spec._screenshotUrl) ?: emptyList()),
            videoUrl = extractedData.getOrNull(Key.VIDEO_URL),
            videoThumbnailUrl = extractedData.getOrNull(Key.VIDEO_THUMBNAIL_URL),
            contentRating = extractedData.require(Key.CONTENT_RATING),
            contentRatingDescription = extractedData.getOrDefault(Key.CONTENT_RATING_DESCRIPTION, ""),
            containsAds = (extractedData.getOrNull<String>(Key.CONTAINS_ADS) != null),
            releaseDate = LocalDate.parse(rawReleaseDate, RELEASE_DATE_PATTERN),
            lastUpdateTimestamp = OffsetDateTime.ofInstant(Instant.ofEpochMilli(rawLastUpdateTimestamp), utcZoneId()),
            appVersion = extractedData.require(Key.APP_VERSION),
            lastUpdateChangelog = extractedData.getOrDefault(Key.LAST_UPDATE_CHANGELOG, ""),
            isEditorsChoice = (extractedData.getOrNull<String>(Key.IS_EDITORS_CHOICE) != null),
        )
    }

    private fun String.normalizeAndroidVersion(): String {
        return this.split(" ").firstOrNull() ?: "Varies"
    }

    private fun JsonArray.extractScreenshotUrls(spec: Path): List<String> {
        return this.map { item ->
            requireNotNull(pathProcessor.extract<JsonElement>(item, spec)?.inferType()) {
                "AppDetailsModelFactory: Screenshot URL not found. Screenshots Container = $this"
            }
        }
    }

    private fun createRatingsHistogram(rawRatingsHistogram: JsonArray?): Map<String, Long> {
        val labels = (1..5).map(Int::toString)

        return if (rawRatingsHistogram == null) {
            labels.associate { label -> (label to 0L) }
        } else {
            labels.mapIndexed { index, label ->
                val ratings = rawRatingsHistogram[index + 1].asJsonArray[1].asLong
                (label to ratings)
            }
                .toMap()
        }
    }

    private fun String.extractDeveloperId(): String {
        return this.split("?id=").lastOrNull() ?: this
    }

}