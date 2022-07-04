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

import com.arthurivanets.googleplayscraper.model.*
import com.arthurivanets.googleplayscraper.modelfactories.AppDetailsModelFactory
import com.arthurivanets.googleplayscraper.modelfactories.AppModelFactory
import com.arthurivanets.googleplayscraper.modelfactories.AppReviewModelFactory
import com.arthurivanets.googleplayscraper.modelfactories.CategoryModelFactory
import com.arthurivanets.googleplayscraper.parsers.*
import com.arthurivanets.googleplayscraper.requests.*
import com.arthurivanets.googleplayscraper.util.*
import com.google.gson.Gson
import okhttp3.OkHttpClient
import java.time.Duration

class GooglePlayScraper(private val config: Config = Config()) {

    private val baseUrl = "https://play.google.com"
    private val httpClient by lazy(::createHttpClient)

    private val gson by lazy { Gson() }
    private val pathProcessor by lazy { JsonPathProcessor() }
    private val appModelFactory by lazy { AppModelFactory(baseUrl, pathProcessor) }
    private val appReviewModelFactory by lazy { AppReviewModelFactory(pathProcessor) }
    private val jsonNormalizer by lazy { IterativeJsonNormalizer(gson) }

    private val responseJsonExtractor by lazy {
        DefaultResponseJsonExtractor(
            gson = gson,
            scriptDataParser = DefaultScriptDataParser(gson),
            jsonNormalizer = jsonNormalizer
        )
    }

    private val appsResponseJsonExtractor by lazy {
        AppsResponseJsonExtractor(
            gson = gson,
            jsonNormalizer = jsonNormalizer,
            defaultResponseJsonExtractor = responseJsonExtractor,
        )
    }

    private val appsLoadingRequestFactory by lazy { DefaultAppsLoadingRequestFactory(baseUrl) }

    private val categoriesResultParser by lazy {
        CategoriesResultParser(
            responseSpec = Specs.CATEGORIES_RESPONSE,
            categorySpec = Specs.CATEGORY,
            pathProcessor = pathProcessor,
            responseJsonExtractor = responseJsonExtractor,
            categoryModelFactory = CategoryModelFactory(pathProcessor),
        )
    }

    private val appsClusterUrlResultParser by lazy {
        AppsClusterUrlResultParser(
            clusterSpec = Specs.CLUSTER_URL,
            responseJsonExtractor = responseJsonExtractor,
            pathProcessor = pathProcessor
        )
    }

    private val appsInitialRequestResultParser by lazy {
        AppsResultParser(
            responseSpec = Specs.APPS_INITIAL_RESPONSE,
            appSpec = Specs.APP_INITIAL_REQUEST,
            pathProcessor = pathProcessor,
            appModelFactory = appModelFactory,
            responseJsonExtractor = appsResponseJsonExtractor
        )
    }

    private val appsDevIdNanInitialRequestResultParser by lazy {
        AppsResultParser(
            responseSpec = Specs.APPS_DEV_ID_NAN_INITIAL_RESPONSE,
            appSpec = Specs.APP_DEV_ID_NAN,
            pathProcessor = pathProcessor,
            appModelFactory = appModelFactory,
            responseJsonExtractor = appsResponseJsonExtractor
        )
    }

    private val appsResultParser by lazy {
        AppsResultParser(
            responseSpec = Specs.APPS_RESPONSE,
            appSpec = Specs.APP,
            pathProcessor = pathProcessor,
            appModelFactory = appModelFactory,
            responseJsonExtractor = appsResponseJsonExtractor
        )
    }

    private val appsListingRequestResultParser by lazy {
        AppsResultParser(
            responseSpec = Specs.APPS_LISTING_RESPONSE,
            appSpec = Specs.APP_LISTING,
            pathProcessor = pathProcessor,
            appModelFactory = appModelFactory,
            responseJsonExtractor = appsResponseJsonExtractor
        )
    }

    private val appDetailsResultParser by lazy {
        AppDetailsResultParser(
            appDetailsSpec = Specs.APP_DETAILS,
            responseJsonExtractor = responseJsonExtractor,
            appDetailsModelFactory = AppDetailsModelFactory(pathProcessor)
        )
    }

    private val permissionsResultParser by lazy {
        PermissionsResultParser(
            pathProcessor = pathProcessor,
            responseJsonExtractor = responseJsonExtractor,
            responseSpec = Specs.PERMISSIONS_RESPONSE
        )
    }

    private val appReviewsRequestResultParser by lazy {
        AppReviewsResultParser(
            responseSpec = Specs.APP_REVIEWS_RESPONSE,
            appReviewSpec = Specs.APP_REVIEW,
            pathProcessor = pathProcessor,
            appReviewModelFactory = appReviewModelFactory,
            responseJsonExtractor = responseJsonExtractor
        )
    }

    private val requestParamsValidator by lazy { RequestParamsValidator() }

    class Config(
        val throttler: RequestThrottler = NoRequestThrottling,
        val userAgentProvider: UserAgentProvider = ResourceFileUserAgentProvider("default_user_agents.txt")
    )

    companion object {

        private val HTTP_CONNECT_TIMEOUT = Duration.ofSeconds(60)
        private val HTTP_READ_TIMEOUT = Duration.ofSeconds(60)
        private val HTTP_WRITE_TIMEOUT = Duration.ofSeconds(60)

    }

    fun getCategories(): Request<List<Category>> {
        return GetCategoriesRequest(
            baseUrl = baseUrl,
            httpClient = httpClient,
            requestResultParser = categoriesResultParser
        )
    }

    fun getAppDetails(params: GetAppDetailsParams): Request<AppDetails> {
        requestParamsValidator.validate(params)

        return GetAppDetailsRequest(
            params = params,
            baseUrl = baseUrl,
            httpClient = httpClient,
            requestResultParser = appDetailsResultParser
        )
    }

    fun getDeveloperApps(params: GetDeveloperAppsParams): Request<List<App>> {
        requestParamsValidator.validate(params)

        val isDevIdNumeric = (params.devId.toLongOrNull() != null)

        return GetDeveloperAppsRequest(
            params = params,
            baseUrl = baseUrl,
            httpClient = httpClient,
            appsLoadingRequestFactory = appsLoadingRequestFactory,
            initialAppsResultParser = if (isDevIdNumeric) {
                appsInitialRequestResultParser
            } else {
                appsDevIdNanInitialRequestResultParser
            },
            appsResultParser = appsResultParser
        )
    }

    fun getSimilarApps(params: GetSimilarAppsParams): Request<List<App>> {
        requestParamsValidator.validate(params)

        return GetSimilarAppsRequest(
            params = params,
            baseUrl = baseUrl,
            httpClient = httpClient,
            appsLoadingRequestFactory = appsLoadingRequestFactory,
            clusterUrlResultParser = appsClusterUrlResultParser,
            initialAppsResultParser = appsInitialRequestResultParser,
            appsResultParser = appsResultParser
        )
    }

    fun getApps(params: GetAppsParams): Request<List<App>> {
        requestParamsValidator.validate(params)

        return GetAppsRequest(
            params = params,
            baseUrl = baseUrl,
            httpClient = httpClient,
            requestResultParser = appsListingRequestResultParser
        )
    }

    fun searchApps(params: SearchAppsParams): Request<List<App>> {
        requestParamsValidator.validate(params)

        return SearchAppsRequest(
            params = params,
            baseUrl = baseUrl,
            httpClient = httpClient,
            appsLoadingRequestFactory = appsLoadingRequestFactory,
            initialRequestResultParser = appsInitialRequestResultParser,
            requestResultParser = appsResultParser
        )
    }

    fun getAppPermissions(params: GetAppPermissionsParams): Request<List<Permission>> {
        requestParamsValidator.validate(params)

        return GetAppPermissionsRequest(
            params = params,
            baseUrl = baseUrl,
            httpClient = httpClient,
            permissionsResultParser = permissionsResultParser,
        )
    }

    fun getAppReviews(params: GetAppReviewsParams): Request<List<AppReview>> {
        requestParamsValidator.validate(params)

        return GetAppReviewsRequest(
            params = params,
            baseUrl = baseUrl,
            httpClient = httpClient,
            requestResultParser = appReviewsRequestResultParser
        )
    }

    private fun createHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(GooglePlayRequestInterceptor(config.throttler, config.userAgentProvider))
            .connectTimeout(HTTP_CONNECT_TIMEOUT)
            .readTimeout(HTTP_READ_TIMEOUT)
            .writeTimeout(HTTP_WRITE_TIMEOUT)
            .build()
    }

}