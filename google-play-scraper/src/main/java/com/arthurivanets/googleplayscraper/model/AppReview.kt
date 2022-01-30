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

import java.time.OffsetDateTime

data class AppReview(
    val id: String,
    val url: String,
    val authorUsername: String,
    val authorImageUrl: String,
    val timestamp: OffsetDateTime,
    val score: Int,
    val scoreText: String,
    val title: String,
    val text: String,
    val replyTimestamp: OffsetDateTime?,
    val replyText: String?,
    val appVersion: String?,
    val appId: String,
    val thumbsUpCount: Int,
    val criteria: List<String>
)