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

import com.google.gson.*
import java.math.BigDecimal
import java.math.BigInteger

internal inline fun <reified T : Any> Map<String, JsonElement>.require(key: String): T {
    return this.getValue(key).inferType()
}

internal inline fun <reified T : Any> Map<String, JsonElement>.getOrDefault(key: String, defaultValue: T): T {
    return getOrNull(key) ?: defaultValue
}

internal inline fun <reified T : Any> Map<String, JsonElement>.getOrNull(key: String): T? {
    val item = this[key]

    return if ((item is JsonNull) && (T::class != JsonNull::class)) {
        null
    } else {
        item?.inferType()
    }
}

internal inline fun <reified T : Any> JsonElement.inferType(): T {
    return when (T::class) {
        Boolean::class -> this.asBoolean as T
        String::class -> this.asString as T
        Number::class -> this.asNumber as T
        Double::class -> this.asDouble as T
        Float::class -> this.asFloat as T
        Long::class -> this.asLong as T
        Int::class -> this.asInt as T
        Byte::class -> this.asByte as T
        Short::class -> this.asShort as T
        BigDecimal::class -> this.asBigDecimal as T
        BigInteger::class -> this.asBigInteger as T
        JsonNull::class -> this.asJsonNull as T
        JsonPrimitive::class -> this.asJsonPrimitive as T
        JsonArray::class -> this.asJsonArray as T
        JsonObject::class -> this.asJsonObject as T
        else -> throw IllegalArgumentException("Unsupported Type = ${T::class.qualifiedName}")
    }
}