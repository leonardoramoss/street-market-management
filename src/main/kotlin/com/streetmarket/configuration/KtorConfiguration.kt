package com.streetmarket.configuration

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.request.path
import io.ktor.server.engine.ShutDownUrl
import org.slf4j.event.Level

fun Application.ktorConfiguration() {

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
            propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
        }
    }

    install(ShutDownUrl.ApplicationCallFeature) {
        exitCodeSupplier = { 0 }
    }

    install(com.streetmarket.configuration.feature.Database)
}
