package com.arthurivanets.googleplayscraper.util

import com.google.gson.Gson
import com.google.gson.JsonElement
import java.io.FileNotFoundException

internal fun readResourceAsString(resourceName: String): String {
    return Thread
        .currentThread()
        .contextClassLoader
        .getResourceAsStream(resourceName)
        ?.bufferedReader()
        ?.readText()
        ?: throw FileNotFoundException("The Resource File (name = $resourceName) Not Found")
}

internal fun readResourceAsJson(resourceName: String): JsonElement {
    return Gson().fromJson(readResourceAsString(resourceName), JsonElement::class.java)
}