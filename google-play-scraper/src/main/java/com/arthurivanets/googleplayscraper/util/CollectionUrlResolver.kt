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

package com.arthurivanets.googleplayscraper.util

import com.arthurivanets.googleplayscraper.model.Collection

internal typealias CollectionUrlResolver = ((Collection) -> String)

internal class DefaultCollectionUrlResolver(
    private val baseUrl: String
) : CollectionUrlResolver {

    override fun invoke(collection: Collection): String {
        val clusterBaseUrl = "$baseUrl/store/apps"
        val newAppsCollections = setOf(
            Collection.NEW_FREE,
            Collection.NEW_PAID,
            Collection.NEW_FREE_GAMES,
            Collection.NEW_PAID_GAMES,
        )

        return if (collection in newAppsCollections) {
            "$clusterBaseUrl/new"
        } else {
            "$clusterBaseUrl/top"
        }
    }

}