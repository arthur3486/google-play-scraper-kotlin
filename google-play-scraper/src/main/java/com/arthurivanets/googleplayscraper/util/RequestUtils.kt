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

import com.arthurivanets.googleplayscraper.parsers.ResultParser
import okhttp3.Response
import java.util.*

internal class PagedResult<T>(
    val result: T,
    val nextToken: String?
)

private class PendingRequest<T>(
    val executor: (() -> Response),
    val resultParser: ResultParser<String, PagedResult<List<T>>>
)

internal fun <T> fetchContinuously(
    itemCount: Int,
    initialRequestExecutor: (() -> Response),
    initialRequestResultParser: ResultParser<String, PagedResult<List<T>>>,
    subsequentRequestExecutor: ((pagingToken: String) -> Response),
    subsequentRequestResultParser: ResultParser<String, PagedResult<List<T>>>,
): List<T> {
    val fetchedItems = mutableListOf<T>()
    val pendingRequests = LinkedList<PendingRequest<T>>()

    pendingRequests.offer(
        PendingRequest(
            executor = initialRequestExecutor,
            resultParser = initialRequestResultParser
        )
    )

    while (pendingRequests.isNotEmpty()) {
        val request = pendingRequests.poll()
        val response = request.executor()

        if (response.isSuccessful) {
            val rawBody = response.requireBody().string()
            val result = request.resultParser.parse(rawBody)
            val pagingToken = result.nextToken

            fetchedItems.addAll(result.result)

            if ((fetchedItems.size < itemCount) && (pagingToken != null)) {
                pendingRequests.offer(
                    PendingRequest(
                        executor = { subsequentRequestExecutor(pagingToken) },
                        resultParser = subsequentRequestResultParser
                    )
                )
            }
        } else {
            throw httpError(response)
        }
    }

    return fetchedItems.take(itemCount)
}