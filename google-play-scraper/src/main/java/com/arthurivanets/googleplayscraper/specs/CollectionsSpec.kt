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

import com.arthurivanets.googleplayscraper.model.Collection
import com.arthurivanets.googleplayscraper.util.Path

internal class CollectionSpec(
    spec: Pair<Collection, Path>,
    vararg specs: Pair<Collection, Path>
) {

    private val specsByCollection = (mapOf(spec) + specs.toMap())

    operator fun get(collection: Collection): Path {
        return specsByCollection.getValue(collection)
    }

}

internal class CollectionsSpec(
    private val clusterUrl: Path,
    private val initialApps: Path,
    private val collections: Map<Int, CollectionSpec>
) {

    fun clusterUrl(collectionsContainerSize: Int, collection: Collection): Path {
        val collectionPath = collections.getValue(collectionsContainerSize)[collection]
        return (collectionPath + clusterUrl)
    }

    fun initialApps(collectionsContainerSize: Int, collection: Collection): Path {
        val collectionPath = collections.getValue(collectionsContainerSize)[collection]
        return (collectionPath + initialApps)
    }

}