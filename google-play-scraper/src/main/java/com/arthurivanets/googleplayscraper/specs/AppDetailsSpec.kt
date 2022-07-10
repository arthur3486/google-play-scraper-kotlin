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

internal class AppDetailsSpec(
    val title: Path,
    val description: Path,
    val summary: Path,
    val installs: Path,
    val minInstalls: Path,
    val maxInstalls: Path,
    val score: Path,
    val scoreText: Path,
    val ratingCount: Path,
    val reviewCount: Path,
    val ratingsHistogram: Path,
    val priceText: Path,
    val price: Path,
    val currency: Path,
    val isAvailable: Path,
    val offersInAppPurchases: Path,
    val inAppPurchasesPriceRange: Path,
    val appSize: Path,
    val androidVersion: Path,
    val androidMinSdkVersion: Path,
    val androidTargetSdkVersion: Path,
    val developer: Path,
    val developerId: Path,
    val developerInternalId: Path,
    val developerEmail: Path,
    val developerWebsite: Path,
    val developerAddress: Path,
    val privacyPolicyUrl: Path,
    val genre: Path,
    val genreId: Path,
    val familyGenre: Path,
    val familyGenreId: Path,
    val iconUrl: Path,
    val headerImageUrl: Path,
    val screenshotsUrlsContainer: Path,
    val _screenshotUrl: Path,
    val videoUrl: Path,
    val videoThumbnailUrl: Path,
    val contentRating: Path,
    val contentRatingDescription: Path,
    val containsAds: Path,
    val releaseDate: Path,
    val lastUpdateTimestamp: Path,
    val appVersion: Path,
    val lastUpdateChangelog: Path,
    val isEditorsChoice: Path,
) : Spec {

    override val attributeMappings = mapOf(
        Key.TITLE to title,
        Key.DESCRIPTION to description,
        Key.SUMMARY to summary,
        Key.SCORE to score,
        Key.SCORE_TEXT to scoreText,
        Key.INSTALLS to installs,
        Key.MIN_INSTALLS to minInstalls,
        Key.MAX_INSTALLS to maxInstalls,
        Key.ICON_URL to iconUrl,
        Key.HEADER_IMAGE_URL to headerImageUrl,
        Key.SCREENSHOTS_URLS_CONTAINER to screenshotsUrlsContainer,
        Key.VIDEO_URL to videoUrl,
        Key.VIDEO_THUMBNAIL_URL to videoThumbnailUrl,
        Key.DEVELOPER to developer,
        Key.DEVELOPER_ID to developerId,
        Key.DEVELOPER_INTERNAL_ID to developerInternalId,
        Key.DEVELOPER_EMAIL to developerEmail,
        Key.DEVELOPER_WEBSITE to developerWebsite,
        Key.DEVELOPER_ADDRESS to developerAddress,
        Key.PRICE_TEXT to priceText,
        Key.PRICE to price,
        Key.CURRENCY to currency,
        Key.RATING_COUNT to ratingCount,
        Key.REVIEW_COUNT to reviewCount,
        Key.RATINGS_HISTOGRAM to ratingsHistogram,
        Key.IS_AVAILABLE to isAvailable,
        Key.OFFERS_IAP to offersInAppPurchases,
        Key.IAP_PRICE_RANGE to inAppPurchasesPriceRange,
        Key.APP_SIZE to appSize,
        Key.ANDROID_VERSION to androidVersion,
        Key.ANDROID_MIN_SDK_VERSION to androidMinSdkVersion,
        Key.ANDROID_TARGET_SDK_VERSION to androidTargetSdkVersion,
        Key.PRIVACY_POLICY_URL to privacyPolicyUrl,
        Key.GENRE to genre,
        Key.GENRE_ID to genreId,
        Key.FAMILY_GENRE to familyGenre,
        Key.FAMILY_GENRE_ID to familyGenreId,
        Key.CONTENT_RATING to contentRating,
        Key.CONTENT_RATING_DESCRIPTION to contentRatingDescription,
        Key.CONTAINS_ADS to containsAds,
        Key.RELEASE_DATE to releaseDate,
        Key.LAST_UPDATE_TIMESTAMP to lastUpdateTimestamp,
        Key.APP_VERSION to appVersion,
        Key.LAST_UPDATE_CHANGELOG to lastUpdateChangelog,
        Key.IS_EDITORS_CHOICE to isEditorsChoice,
    )

    object Key {

        const val TITLE = "title"
        const val DESCRIPTION = "description"
        const val SUMMARY = "summary"
        const val SCORE = "score"
        const val SCORE_TEXT = "score_text"
        const val INSTALLS = "installs"
        const val MIN_INSTALLS = "min_installs"
        const val MAX_INSTALLS = "max_installs"
        const val ICON_URL = "icon_url"
        const val HEADER_IMAGE_URL = "header_image_url"
        const val SCREENSHOTS_URLS_CONTAINER = "screenshots_urls_container"
        const val VIDEO_URL = "video_url"
        const val VIDEO_THUMBNAIL_URL = "video_thumbnail_url"
        const val DEVELOPER = "developer"
        const val DEVELOPER_ID = "developer_id"
        const val DEVELOPER_INTERNAL_ID = "developer_internal_id"
        const val DEVELOPER_EMAIL = "developer_email"
        const val DEVELOPER_WEBSITE = "developer_website"
        const val DEVELOPER_ADDRESS = "developer_address"
        const val PRICE_TEXT = "price_text"
        const val PRICE = "price"
        const val CURRENCY = "currency"
        const val RATING_COUNT = "rating_count"
        const val REVIEW_COUNT = "review_count"
        const val RATINGS_HISTOGRAM = "ratings_histogram"
        const val IS_AVAILABLE = "is_available"
        const val OFFERS_IAP = "offers_iap"
        const val IAP_PRICE_RANGE = "iap_price_range"
        const val APP_SIZE = "app_size"
        const val ANDROID_VERSION = "android_version"
        const val ANDROID_MIN_SDK_VERSION = "android_min_sdk_version"
        const val ANDROID_TARGET_SDK_VERSION = "android_target_sdk_version"
        const val PRIVACY_POLICY_URL = "privacy_policy_url"
        const val GENRE = "genre"
        const val GENRE_ID = "genre_id"
        const val FAMILY_GENRE = "family_genre"
        const val FAMILY_GENRE_ID = "family_genre_id"
        const val CONTENT_RATING = "content_rating"
        const val CONTENT_RATING_DESCRIPTION = "content_rating_description"
        const val CONTAINS_ADS = "contains_ads"
        const val RELEASE_DATE = "release_date"
        const val LAST_UPDATE_TIMESTAMP = "last_update_timestamp"
        const val APP_VERSION = "version"
        const val LAST_UPDATE_CHANGELOG = "last_update_changelog"
        const val IS_EDITORS_CHOICE = "is_editors_choice"

    }

}