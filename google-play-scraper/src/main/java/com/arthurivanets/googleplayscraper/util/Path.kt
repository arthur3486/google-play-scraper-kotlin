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

internal fun path(element: Any, vararg elements: Any): Path {
    fun validateElement(el: Any, message: String) {
        require((el is Int) || (el is String)) {
            "Path Element MUST BE of the following types (Int, String). $message"
        }
    }

    fun Any.toPathElement(): Path.Element {
        return when (this) {
            is String -> Path.Element.Key(this)
            is Int -> Path.Element.Idx(this)
            else -> throw IllegalArgumentException()
        }
    }

    validateElement(element, "Element = $element")
    elements.forEachIndexed { index, el -> validateElement(el, "Elements[$index] = $el") }

    return Path(listOf(element.toPathElement()) + elements.map(Any::toPathElement))
}

internal data class Path(val elements: List<Element>) {

    sealed class Element {

        data class Idx(val index: Int) : Element()

        data class Key(val key: String) : Element()

    }

    operator fun plus(other: Path): Path {
        return Path(this.elements + other.elements)
    }

}