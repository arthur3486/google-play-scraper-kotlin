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

package com.arthurivanets.googleplayscraper

class HumanBehaviorRequestThrottler(
    private val baseDelayMs: Long = DEFAULT_BASE_DELAY_MS,
    jitterMinMs: Long = DEFAULT_JITTER_MIN,
    jitterMaxMs: Long = DEFAULT_JITTER_MAX
) : RequestThrottler {

    private val jitterRange: LongRange

    companion object {

        const val DEFAULT_BASE_DELAY_MS = 5_000L
        const val DEFAULT_JITTER_MIN = 0L
        const val DEFAULT_JITTER_MAX = 3_000L

    }

    init {
        require(jitterMinMs >= 0L) { "Min Jitter MUST BE >= 0" }
        require(jitterMaxMs >= jitterMinMs) { "Max Jitter MUST BE >= Min Jitter" }

        jitterRange = jitterMinMs..jitterMaxMs
    }

    override fun awaitContinue() {
        Thread.sleep(baseDelayMs + (jitterRange.randomOrNull() ?: 0L))
    }

}