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

internal class AppReviewSpec(
    val id: Path,
    val authorUsername: Path,
    val authorImageUrl: Path,
    val timestamp: Path,
    val score: Path,
    val title: Path,
    val text: Path,
    val replyTimestamp: Path,
    val replyText: Path,
    val appVersion: Path,
    val thumbsUpCount: Path,
    val criteria: Path
) : Spec {

    override val attributeMappings = mapOf(
        Key.ID to id,
        Key.AUTHOR_USERNAME to authorUsername,
        Key.AUTHOR_IMAGE_URL to authorImageUrl,
        Key.TIMESTAMP to timestamp,
        Key.SCORE to score,
        Key.TITLE to title,
        Key.TEXT to text,
        Key.REPLY_TIMESTAMP to replyTimestamp,
        Key.REPLY_TEXT to replyText,
        Key.APP_VERSION to appVersion,
        Key.THUMBS_UP_COUNT to thumbsUpCount,
        Key.CRITERIA to criteria,
    )

    object Key {

        const val ID = "id"
        const val AUTHOR_USERNAME = "author_username"
        const val AUTHOR_IMAGE_URL = "author_image_url"
        const val TIMESTAMP = "timestamp"
        const val SCORE = "score"
        const val TITLE = "title"
        const val TEXT = "text"
        const val REPLY_TIMESTAMP = "reply_timestamp"
        const val REPLY_TEXT = "reply_text"
        const val APP_VERSION = "app_version"
        const val THUMBS_UP_COUNT = "thumbs_up_count"
        const val CRITERIA = "criteria"

    }

}