package com.arthurivanets.googleplayscraper.util

import com.arthurivanets.googleplayscraper.specs.Spec
import com.google.gson.JsonElement
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class JsonPathProcessorTests {

    private val pathProcessor = JsonPathProcessor()

    @Test
    fun `json element extraction from json string by path (element present)`() {
        val jsonString = readResourceAsString("path_processor_tests/simple_user_1.json")

        assertEquals("john.doe", pathProcessor.extract<JsonElement>(jsonString, path("username"))?.asString)
        assertEquals("John", pathProcessor.extract<JsonElement>(jsonString, path("name", "first_name"))?.asString)
    }

    @Test
    fun `json element extraction from json string by path (element absent)`() {
        val jsonString = readResourceAsString("path_processor_tests/simple_user_1.json")

        assertNull(pathProcessor.extract<JsonElement>(jsonString, path("name", "middle_name")))
    }

    @Test
    fun `json element extraction from json element by path (element present)`() {
        val jsonTree = readResourceAsJson("path_processor_tests/simple_user_1.json")

        assertEquals("john.doe", pathProcessor.extract<JsonElement>(jsonTree, path("username"))?.asString)
        assertEquals("John", pathProcessor.extract<JsonElement>(jsonTree, path("name", "first_name"))?.asString)
    }

    @Test
    fun `json element extraction from json element by path (element absent)`() {
        val jsonTree = readResourceAsString("path_processor_tests/simple_user_1.json")

        assertNull(pathProcessor.extract<JsonElement>(jsonTree, path("name", "middle_name")))
    }

    @Test
    fun `json data extraction from json string by spec (all elements present)`() {
        val userSpec = UserSpec(
            id = path("id"),
            username = path("username"),
            firstName = path("name", "first_name"),
            lastName = path("name", "last_name"),
        )

        val jsonString = readResourceAsString("path_processor_tests/simple_user_1.json")
        val userData = pathProcessor.extract(jsonString, userSpec)

        assertEquals(123, userData.getValue("id").asLong)
        assertEquals("john.doe", userData.getValue("username").asString)
        assertEquals("John", userData.getValue("first_name").asString)
        assertEquals("Doe", userData.getValue("last_name").asString)
    }

    @Test
    fun `json data extraction from json string by spec (element absent)`() {
        val userSpec = UserSpec(
            id = path("id"),
            username = path("username"),
            firstName = path("name", "first_name"),
            lastName = path("name", "last_name", "absent"),
        )

        val jsonString = readResourceAsString("path_processor_tests/simple_user_1.json")
        val userData = pathProcessor.extract(jsonString, userSpec)

        assertEquals(123, userData.getValue("id").asLong)
        assertEquals("john.doe", userData.getValue("username").asString)
        assertEquals("John", userData.getValue("first_name").asString)
        assertNull(userData["last_name"])
    }

    @Test
    fun `json data extraction from json element by spec (all elements present)`() {
        val userSpec = UserSpec(
            id = path("id"),
            username = path("username"),
            firstName = path("name", "first_name"),
            lastName = path("name", "last_name"),
        )

        val jsonTree = readResourceAsJson("path_processor_tests/simple_user_1.json")
        val userData = pathProcessor.extract(jsonTree, userSpec)

        assertEquals(123, userData.getValue("id").asLong)
        assertEquals("john.doe", userData.getValue("username").asString)
        assertEquals("John", userData.getValue("first_name").asString)
        assertEquals("Doe", userData.getValue("last_name").asString)
    }

    @Test
    fun `json data extraction from json element by spec (element absent)`() {
        val userSpec = UserSpec(
            id = path("id"),
            username = path("username"),
            firstName = path("name", "first_name"),
            lastName = path("name", "last_name", "absent"),
        )

        val jsonTree = readResourceAsJson("path_processor_tests/simple_user_1.json")
        val userData = pathProcessor.extract(jsonTree, userSpec)

        assertEquals(123, userData.getValue("id").asLong)
        assertEquals("john.doe", userData.getValue("username").asString)
        assertEquals("John", userData.getValue("first_name").asString)
        assertNull(userData["last_name"])
    }

}

private class UserSpec(
    val id: Path,
    val username: Path,
    val firstName: Path,
    val lastName: Path
) : Spec {

    override val attributeMappings = mapOf(
        Key.ID to id,
        Key.USERNAME to username,
        Key.FIRST_NAME to firstName,
        Key.LAST_NAME to lastName
    )

    object Key {

        const val ID = "id"
        const val USERNAME = "username"
        const val FIRST_NAME = "first_name"
        const val LAST_NAME = "last_name"

    }

}