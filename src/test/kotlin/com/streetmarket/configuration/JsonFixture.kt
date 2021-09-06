package com.streetmarket.configuration

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import net.minidev.json.JSONValue
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class JsonFixture private constructor() {
    companion object {

        private val objectMapper = ObjectMapper()

        fun loadJsonFile(path: String): String =
            JSONValue.parse(
                InputStreamReader(object {}.javaClass.getResourceAsStream("/$path"), StandardCharsets.UTF_8)
            ).toString()

        fun loadJsonFileAsJsonNode(path: String): JsonNode = objectMapper.readTree(loadJsonFile(path))
    }
}
