package com.streetmarket.configuration

import com.streetmarket.configuration.database.DatabaseAssertion
import com.streetmarket.configuration.database.DatabaseFixture
import io.ktor.application.Application
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.TestApplicationResponse
import io.ktor.server.testing.createTestEnvironment
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class IntegrationTest(val applicationModule: Application.() -> Unit) : DatabaseAssertion by DatabaseFixture() {

    private val engine = TestApplicationEngine(createTestEnvironment())

    @BeforeAll
    internal fun startEnvironment() {
        engine.apply { applicationModule(application) }
        engine.start()
    }

    @AfterAll
    internal fun stopEnvironment() {
        engine.stop(0, 0)
    }

    internal fun post(
        uri: String,
        payload: String,
        assertion: TestApplicationResponse.() -> Unit = { TODO() }
    ) = request(uri, HttpMethod.Post, payload, assertion)

    internal fun put(
        uri: String,
        payload: String,
        assertion: TestApplicationResponse.() -> Unit = { TODO() }
    ) = request(uri, HttpMethod.Put, payload, assertion)

    internal fun delete(
        uri: String,
        assertion: TestApplicationResponse.() -> Unit
    ) = withTestEngine { handleRequest(HttpMethod.Delete, uri).apply { assertion(response) } }

    internal fun get(
        uri: String,
        assertion: TestApplicationResponse.() -> Unit = { TODO() }
    ) = withTestEngine { handleRequest(HttpMethod.Get, uri).apply { assertion(response) } }

    private fun request(
        uri: String,
        method: HttpMethod,
        payload: String,
        assertion: TestApplicationResponse.() -> Unit = { TODO() }
    ) = withTestEngine {
        handleRequest(method, uri) {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(payload)
        }.apply { assertion(response) }
    }

    private fun <R> withTestEngine(test: TestApplicationEngine.() -> R): R = engine.test()
}
