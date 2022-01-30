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

import com.google.gson.Gson
import com.google.gson.JsonElement
import java.util.*

internal class IterativeJsonNormalizer(
    private val gson: Gson
) : JsonNormalizer {

    override fun normalize(json: JsonElement): JsonElement {
        if (json.isJsonPrimitive || json.isJsonNull) {
            return json
        }

        val normalizedJson = json.deepCopy()
        val pendingTraversals = LinkedList<JsonElement>()
        pendingTraversals.push(normalizedJson)

        while (pendingTraversals.isNotEmpty()) {
            val traversal = pendingTraversals.pop()

            if (traversal.isJsonArray) {
                val jsonArray = traversal.asJsonArray

                jsonArray.forEachIndexed { index, child ->
                    processElement(
                        outPendingTraversals = pendingTraversals,
                        element = child,
                        replacementAction = { newElement -> jsonArray.set(index, newElement) }
                    )
                }
            } else if (traversal.isJsonObject) {
                val jsonObject = traversal.asJsonObject

                jsonObject.entrySet().forEach { (key, child) ->
                    processElement(
                        outPendingTraversals = pendingTraversals,
                        element = child,
                        replacementAction = { newElement -> jsonObject.add(key, newElement) }
                    )
                }
            }
        }

        return normalizedJson
    }

    private fun processElement(
        outPendingTraversals: LinkedList<JsonElement>,
        element: JsonElement,
        replacementAction: ((JsonElement) -> Unit)
    ) {
        if (element.isJsonArray || element.isJsonObject) {
            outPendingTraversals.push(element)
        } else if (element.isJsonPrimitive) {
            val primitive = element.asJsonPrimitive

            if (primitive.isString) {
                try {
                    val normalizedPrimitive = gson.fromJson(primitive.asString, JsonElement::class.java)

                    if (normalizedPrimitive.isJsonArray || normalizedPrimitive.isJsonObject) {
                        replacementAction(normalizedPrimitive)
                        outPendingTraversals.push(normalizedPrimitive)
                    }
                } catch (error: Throwable) {
                    // do nothing
                }
            }
        }
    }

}