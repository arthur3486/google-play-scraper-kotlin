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

package com.arthurivanets.googleplayscraper.model

import java.time.LocalDate
import java.time.OffsetDateTime

data class AppDetails(
    val appId: String,
    val url: String,
    val title: String,
    val descriptionHtml: String,
    val summary: String,
    val installs: String,
    val minInstalls: Long,
    val maxInstalls: Long,
    val score: Double?,
    val scoreText: String?,
    val ratingCount: Long,
    val reviewCount: Long,
    val ratingsHistogram: Map<String, Long>,
    val priceText: String?,
    val price: Double?,
    val currency: String?,
    val isFree: Boolean,
    val isAvailable: Boolean,
    val offersInAppPurchases: Boolean,
    val inAppPurchasesPriceRange: String?,
    val appSize: String,
    val androidVersion: String?,
    val androidVersionText: String?,
    val developer: String,
    val developerId: String,
    val developerInternalId: String,
    val developerEmail: String,
    val developerWebsite: String?,
    val developerAddress: String?,
    val privacyPolicyUrl: String?,
    val genre: String,
    val genreId: String,
    val familyGenre: String?,
    val familyGenreId: String?,
    val iconUrl: String,
    val headerImageUrl: String,
    val screenshotsUrls: List<String>,
    val videoUrl: String?,
    val videoThumbnailUrl: String?,
    val contentRating: String,
    val contentRatingDescription: String,
    val containsAds: Boolean,
    val releaseDate: LocalDate,
    val lastUpdateTimestamp: OffsetDateTime,
    val appVersion: String,
    val lastUpdateChangelog: String,
    val isEditorsChoice: Boolean,
)