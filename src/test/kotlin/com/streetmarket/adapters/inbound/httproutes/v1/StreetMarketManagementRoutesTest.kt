package com.streetmarket.adapters.inbound.httproutes.v1

import com.streetmarket.configuration.IntegrationTest
import com.streetmarket.configuration.JsonFixture.Companion.loadJsonFile
import com.streetmarket.module
import io.ktor.application.Application
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.NoContent
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertAll
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@DisplayName("Street Market Management - Routes")
internal class StreetMarketManagementRoutesTest : IntegrationTest(Application::module) {

    private val fromClauseStreetMarket = "MARKET.STREET_MARKET"
    private val fromClauseCensus = "MARKET.CENSUS"

    private val managementEndpoint = "/v1/market"

    @BeforeTest
    fun beforeTest() {
        executeScript("scripts/clear.sql")
    }

    @Test
    fun `should boarding new street market`() {
        post(managementEndpoint, loadJsonFile("mock/mock_streetmarket_4041-2.json")) {
            assertAll(
                { assertEquals(Created, status()) },
                { assertDatabase(fromClauseStreetMarket, "street_market_4041-2.xml") },
                { assertDatabase(fromClauseCensus, "street_market_census_4041-2.xml") }
            )
        }
    }

    @Test
    fun `should fail with 409 - Conflict status when already has street market`() {

        val streetMarketRegister = "4041-2"

        executeScript("scripts/data_$streetMarketRegister.sql")

        post(managementEndpoint, loadJsonFile("mock/mock_streetmarket_$streetMarketRegister.json")) {
            assertAll(
                { assertEquals(Conflict, status()) },
                { assertDatabase(fromClauseStreetMarket, "street_market_$streetMarketRegister.xml") },
                { assertDatabase(fromClauseCensus, "street_market_census_$streetMarketRegister.xml") }
            )
        }
    }

    @Test
    fun `should replace an existing street market`() {
        val streetMarketRegister = "4041-2"

        executeScript("scripts/data_$streetMarketRegister.sql")

        val json = loadJsonFile("mock/mock_replace_streetmarket_$streetMarketRegister.json")

        put("$managementEndpoint/$streetMarketRegister", json) {
            assertAll(
                { assertEquals(OK, status()) },
                { assertDatabase(fromClauseStreetMarket, "replaced_street_market_$streetMarketRegister.xml") },
                { assertDatabase(fromClauseCensus, "replaced_street_market_census_$streetMarketRegister.xml") }
            )
        }
    }

    @Test
    fun `should fail with 400 - Bad Request when register in URI conflict with register in the body`() {

        val streetMarketRegister = "4041-2"
        val replaceStreetMarketRegister = "4045-2"

        executeScript("scripts/data_$streetMarketRegister.sql")

        val json = loadJsonFile("mock/mock_replace_streetmarket_$streetMarketRegister.json")

        put("$managementEndpoint/$replaceStreetMarketRegister", json) {
            assertAll(
                { assertEquals(BadRequest, status()) },
                { assertDatabase(fromClauseStreetMarket, "street_market_$streetMarketRegister.xml") },
                { assertDatabase(fromClauseCensus, "street_market_census_$streetMarketRegister.xml") }
            )
        }
    }

    @Test
    fun `should fail with 404 - Not Found when request to replace a nonexistent street market`() {

        val replaceStreetMarketRegister = "4045-2"

        val json = loadJsonFile("mock/mock_streetmarket_$replaceStreetMarketRegister.json")

        put("$managementEndpoint/$replaceStreetMarketRegister", json) {
            assertAll(
                { assertEquals(NotFound, status()) }
            )
        }
    }

    @Test
    fun `should remove street market when exists`() {

        val streetMarketRegister = "4041-2"

        executeScript("scripts/data_$streetMarketRegister.sql")

        delete("$managementEndpoint/$streetMarketRegister") {
            assertAll(
                { assertEquals(NoContent, status()) },
                { assertDatabase(fromClauseStreetMarket, "street_market_empty.xml") },
                { assertDatabase(fromClauseCensus, "street_market_census_empty.xml") }
            )
        }
    }

    @Test
    fun `should fail with 404 - Not Found when request to remove a nonexistent street market`() {

        val streetMarketRegister = "4041-2"

        delete("$managementEndpoint/$streetMarketRegister") { assertEquals(NotFound, status()) }
    }
}
