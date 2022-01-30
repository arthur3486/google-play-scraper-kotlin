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

package com.arthurivanets.googleplayscraper.parsers

import com.arthurivanets.googleplayscraper.model.Permission
import com.arthurivanets.googleplayscraper.specs.PermissionsCategorySpec
import com.arthurivanets.googleplayscraper.specs.PermissionsResponseSpec
import com.arthurivanets.googleplayscraper.specs.PermissionsSectionSpec
import com.arthurivanets.googleplayscraper.util.PathProcessor
import com.arthurivanets.googleplayscraper.util.ResponseJsonExtractor
import com.arthurivanets.googleplayscraper.util.inferType
import com.google.gson.JsonArray
import com.google.gson.JsonElement

internal class PermissionsResultParser(
    private val pathProcessor: PathProcessor<JsonElement>,
    private val responseJsonExtractor: ResponseJsonExtractor,
    private val responseSpec: PermissionsResponseSpec,
) : ResultParser<String, List<Permission>> {

    companion object {

        private const val DEFAULT_CATEGORY_TYPE = "Other"

    }

    override fun parse(input: String): List<Permission> {
        require(input.isNotBlank()) {
            "The Input MUST BE a non-blank String"
        }

        val contentJson = responseJsonExtractor.extract(input)
        val categorizedPermissions = contentJson.extractPermissions(responseSpec.categorized)
        val otherPermissions = contentJson.extractPermissions(responseSpec.other)
        val leftoverPermissions = contentJson.extractPermissions(responseSpec.leftover)

        return (categorizedPermissions + otherPermissions + leftoverPermissions)
    }

    private fun JsonElement.extractPermissions(spec: PermissionsSectionSpec): List<Permission> {
        val rawPermissionsSection = pathProcessor.extract<JsonArray>(this, spec.section) ?: return emptyList()

        return rawPermissionsSection.map { rawCategory ->
            rawCategory.extractPermissions(spec.category)
        }
            .flatten()
    }

    private fun JsonElement.extractPermissions(spec: PermissionsCategorySpec): List<Permission> {
        val type = spec.categoryType?.let { typeSpec ->
            pathProcessor.extract<JsonElement>(this, typeSpec)?.inferType()
        } ?: DEFAULT_CATEGORY_TYPE

        val rawPermissions = pathProcessor.extract<JsonArray>(this, spec.permissions) ?: JsonArray()

        return rawPermissions.map { rawPermission ->
            Permission(
                type = type,
                description = requireNotNull(
                    pathProcessor.extract<JsonElement>(
                        rawPermission,
                        spec.permissionDescription
                    )?.inferType()
                ) {
                    "Permission description extraction failed. No valid description found. (Category type = $type)"
                }
            )
        }
    }

}