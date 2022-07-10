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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PathTests {

    @Test
    fun `invalid path creation`() {
        assertThrows<IllegalArgumentException> {
            path(0.5, 0.1)
        }
    }

    @Test
    fun `valid path creation`() {
        val path1 = path("a", "b", 1, 2)

        path1.assertElementAtIndexEqualsTo<Path.Element.Key>(0, "a")
        path1.assertElementAtIndexEqualsTo<Path.Element.Key>(1, "b")
        path1.assertElementAtIndexEqualsTo<Path.Element.Idx>(2, 1)
        path1.assertElementAtIndexEqualsTo<Path.Element.Idx>(3, 2)

        val path2 = path(1, 2, 3)

        path2.assertElementAtIndexEqualsTo<Path.Element.Idx>(0, 1)
        path2.assertElementAtIndexEqualsTo<Path.Element.Idx>(1, 2)
        path2.assertElementAtIndexEqualsTo<Path.Element.Idx>(2, 3)
    }

    @Test
    fun `path addition`() {
        val path1 = path("a", "b", 1, 2)
        val path2 = path(5, 6, "d")
        val path3 = (path1 + path2)

        path3.assertElementAtIndexEqualsTo<Path.Element.Key>(0, "a")
        path3.assertElementAtIndexEqualsTo<Path.Element.Key>(1, "b")
        path3.assertElementAtIndexEqualsTo<Path.Element.Idx>(2, 1)
        path3.assertElementAtIndexEqualsTo<Path.Element.Idx>(3, 2)
        path3.assertElementAtIndexEqualsTo<Path.Element.Idx>(4, 5)
        path3.assertElementAtIndexEqualsTo<Path.Element.Idx>(5, 6)
        path3.assertElementAtIndexEqualsTo<Path.Element.Key>(6, "d")
    }

    private inline fun <reified ExpectedType : Path.Element> Path.assertElementAtIndexEqualsTo(
        index: Int,
        expectedValue: Any
    ) {
        assertTrue(this.elements[index] is ExpectedType) {
            "Path.elements[$index] is not of type ${ExpectedType::class.java.simpleName}"
        }

        when (ExpectedType::class) {
            Path.Element.Key::class -> assertEquals((this.elements[index] as Path.Element.Key).key, expectedValue)
            Path.Element.Idx::class -> assertEquals((this.elements[index] as Path.Element.Idx).index, expectedValue)
        }
    }

}