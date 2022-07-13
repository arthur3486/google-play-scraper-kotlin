/*
 * Copyright 2022 Arthur Ivanets, arthur.ivanets.work@gmail.com
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

import java.net.URLEncoder

@JvmInline
value class UriComponent(val value: String)

fun UriComponent.encode(): String {
    if (this.value.isEmpty()) {
        return this.value
    }

    val encodedValue = try {
        URLEncoder.encode(this.value, "UTF-8")
    } catch (e: Exception) {
        throw e
    }

    val postProcessedValueBuilder = StringBuilder()
    val valueLength = encodedValue.length
    var charIndex = 0

    while (charIndex < valueLength) {
        val newChar = when (val char = encodedValue[charIndex]) {
            '+' -> "%20"

            '%' -> {
                if (charIndex > (valueLength - 2)) {
                    break
                }

                val escapedChar = encodedValue.substring((charIndex + 1), (charIndex + 3))
                charIndex += 2

                when (escapedChar) {
                    "21" -> "!"
                    "27" -> "'"
                    "28" -> "("
                    "29" -> ")"
                    "7E",
                    "7e" -> "~"
                    else -> "%$escapedChar"
                }
            }

            else -> char
        }

        postProcessedValueBuilder.append(newChar)
        charIndex++
    }

    return postProcessedValueBuilder.toString()
}