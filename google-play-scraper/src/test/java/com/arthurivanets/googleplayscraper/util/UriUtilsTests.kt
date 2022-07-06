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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UriUtilsTests {

    @Test
    fun `uri component encoding - with reserved characters`() {
        val reservedChars = ";,/?:@&=+\$"
        val expectedResult = "%3B%2C%2F%3F%3A%40%26%3D%2B%24"
        val encodedValue = UriComponent(reservedChars).encode()

        assertEquals(expectedResult, encodedValue)
    }

    @Test
    fun `uri component encoding - with unescaped characters`() {
        val unescapedChars = "-_.!~*'()"
        val encodedValue = UriComponent(unescapedChars).encode()

        assertEquals(unescapedChars, encodedValue)
    }

    @Test
    fun `uri component encoding - with number sign character`() {
        val character = "#"
        val expectedResult = "%23"
        val encodedValue = UriComponent(character).encode()

        assertEquals(expectedResult, encodedValue)
    }

    @Test
    fun `uri component encoding - with alphanumeric characters including space character`() {
        val chars = "ABC abc 123"
        val expectedResult = "ABC%20abc%20123"
        val encodedValue = UriComponent(chars).encode()

        assertEquals(expectedResult, encodedValue)
    }

    @Test
    fun `uri component encoding - empty string`() {
        val input = ""
        val encodedValue = UriComponent(input).encode()

        assertEquals(input, encodedValue)
    }

    @Test
    fun `uri component encoding - with space characters`() {
        val input = "   "
        val expectedResult = "%20%20%20"
        val encodedValue = UriComponent(input).encode()

        assertEquals(expectedResult, encodedValue)
    }

}