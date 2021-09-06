package com.streetmarket

import com.streetmarket.adapters.inbound.httproutes.v1.handleHttpExceptions
import com.streetmarket.adapters.inbound.httproutes.v1.streetMarketManagementRoutes
import com.streetmarket.adapters.inbound.httproutes.v1.streetMarketQueryRoutes
import com.streetmarket.configuration.koinConfiguration
import com.streetmarket.configuration.ktorConfiguration
import io.ktor.application.Application
import io.ktor.http.content.files
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.routing.routing

fun main(args: Array<String>): Unit =
    io.ktor.server.cio.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    koinConfiguration()
    ktorConfiguration()
    routing {
        streetMarketManagementRoutes()
        streetMarketQueryRoutes()
        static("/docs") {
            resources("static")
            files("static")
        }
    }
    handleHttpExceptions()
}
