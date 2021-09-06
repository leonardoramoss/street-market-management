package com.streetmarket.adapters.inbound.httproutes.v1

import com.streetmarket.configuration.JsonFixture.Companion.loadJsonFile
import com.streetmarket.configuration.database.DatabaseAssertion
import com.streetmarket.configuration.database.DatabaseFixture
import com.streetmarket.module
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertAll
import org.skyscreamer.jsonassert.JSONAssert.assertEquals
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@DisplayName("Street Market Query - Routes")
internal class StreetMarketQueryRoutesTest : DatabaseAssertion by DatabaseFixture() {

    private val baseEndpoint = "/v1/market"
    private val searchEndpoint = "$baseEndpoint/search"

    @BeforeTest
    fun beforeTest() {
        executeScript("scripts/clear.sql")
    }

    @Test
    fun `should detail street market`() {

        executeScript("scripts/data.sql")

        val streetMarketRegister = "4041-2"
        val jsonMock = "mock/mock_streetmarket_$streetMarketRegister.json"

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Get, "$baseEndpoint/$streetMarketRegister").apply {
                assertAll(
                    { assertEquals(HttpStatusCode.OK, response.status()) },
                    { assertEquals(loadJsonFile(jsonMock), response.content, false) }
                )
            }
        }
    }

    @Test
    fun `should fail with status 404 - Not Found `() {

        val unknownStreetMarketRegister = "4444"

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Get, "$baseEndpoint/$unknownStreetMarketRegister").apply {
                assertAll(
                    { assertEquals(HttpStatusCode.NotFound, response.status()) },
                    { assertNull(response.content) }
                )
            }
        }
    }

    @Test
    fun `should show list of all street markets in region5=Leste`() {

        executeScript("scripts/data.sql")

        val searchResponse = "expected/http/search_streetmartket_region5.json"

        val uriByQuery = "$searchEndpoint?region5=Leste"

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Get, uriByQuery).apply {
                assertAll(
                    { assertEquals(HttpStatusCode.OK, response.status()) },
                    { assertEquals(loadJsonFile(searchResponse), response.content, false) }
                )
            }
        }
    }

    @Test
    fun `should show list of all street markets in region5=Leste with pagination`() {

        executeScript("scripts/data.sql")

        val searchResponse = "expected/http/search_streetmartket_region5.json"

        val uriByQuery = "$searchEndpoint?region5=Leste&page=0&size=10"

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Get, uriByQuery).apply {
                assertAll(
                    { assertEquals(HttpStatusCode.OK, response.status()) },
                    { assertEquals(loadJsonFile(searchResponse), response.content, false) }
                )
            }
        }
    }
}
