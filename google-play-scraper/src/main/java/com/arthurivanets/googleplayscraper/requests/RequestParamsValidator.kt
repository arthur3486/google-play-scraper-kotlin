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

package com.arthurivanets.googleplayscraper.requests

internal class RequestParamsValidator {

    companion object {

        private val REGEX_APP_ID: Regex by lazy {
            val idPartPattern = "[a-zA-Z][a-zA-Z_0-9]+"
            Regex("($idPartPattern)(\\.$idPartPattern)*")
        }
        private val REGEX_COUNTRY_CODE: Regex by lazy { Regex("[a-zA-Z]{2}") }
        private val REGEX_LANGUAGE: Regex by lazy { Regex("[a-zA-Z]{2}") }

    }

    fun validate(params: GetAppDetailsParams) {
        validateAppId(params.appId)
        validateCountry(params.country)
        validateLanguage(params.language)
    }

    fun validate(params: GetDeveloperAppsParams) {
        validateDevId(params.devId)
        validateCountry(params.country)
        validateLanguage(params.language)
        validateLimit(params.limit)
    }

    fun validate(params: GetSimilarAppsParams) {
        validateAppId(params.appId)
        validateCountry(params.country)
        validateLanguage(params.language)
        validateLimit(params.limit)
    }

    fun validate(params: GetAppsParams) {
        validateCountry(params.country)
        validateLanguage(params.language)
        validateLimit(params.limit)
    }

    fun validate(params: SearchAppsParams) {
        require(params.query.isNotBlank()) {
            "The query must not be blank"
        }
        validateCountry(params.country)
        validateLanguage(params.language)
        validateLimit(params.limit)
    }

    fun validate(params: GetAppPermissionsParams) {
        validateAppId(params.appId)
        validateLanguage(params.language)
    }

    fun validate(params: GetAppReviewsParams) {
        validateAppId(params.appId)
        validateCountry(params.country)
        validateLanguage(params.language)
        validateLimit(params.limit)
    }

    private fun validateAppId(appId: String) {
        require(appId.matches(REGEX_APP_ID)) {
            "Invalid appId [ $appId ]."
        }
    }

    private fun validateCountry(country: String) {
        require(country.matches(REGEX_COUNTRY_CODE)) {
            "Invalid country code [ $country ]"
        }
    }

    private fun validateLanguage(language: String) {
        require(language.matches(REGEX_LANGUAGE)) {
            "Invalid language code [ $language ]"
        }
    }

    private fun validateDevId(devId: String) {
        require(devId.isNotBlank()) {
            "The developer id must not be blank."
        }
    }

    private fun validateLimit(limit: Int) {
        require(limit > 0) {
            "The limit must be >= 0."
        }
    }

}