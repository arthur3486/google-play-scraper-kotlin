package com.arthurivanets.googleplayscraper

import java.io.FileNotFoundException

class ResourceFileUserAgentProvider(private val resourceFile: String) : UserAgentProvider {

    private val userAgents: List<String> by lazy(::loadUserAgents)

    override fun provide(): String {
        return userAgents.random()
    }

    private fun loadUserAgents(): List<String> {
        return Thread.currentThread()
            .contextClassLoader
            .getResourceAsStream(resourceFile)
            ?.bufferedReader()
            ?.readLines()
            ?: throw FileNotFoundException("The Resource File (name = $resourceFile) Not Found")
    }

}