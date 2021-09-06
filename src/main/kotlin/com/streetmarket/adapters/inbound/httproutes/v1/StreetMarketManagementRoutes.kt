package com.streetmarket.adapters.inbound.httproutes.v1

import com.streetmarket.core.service.BoardingStreetMarket
import com.streetmarket.core.service.RemoveStreetMarket
import com.streetmarket.core.service.ReplaceStreetMarket
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route
import io.ktor.util.getOrFail
import org.koin.ktor.ext.inject

fun Route.streetMarketManagementRoutes() {

    val boardingStreetMarket: BoardingStreetMarket by inject()
    val replaceStreetMarket: ReplaceStreetMarket by inject()
    val removeStreetMarket: RemoveStreetMarket by inject()

    val registerPath = "/{register}"

    route("/v1/market") {

        post {
            boardingStreetMarket.onboard(call.receive())
                .onSuccess { call.respond(HttpStatusCode.Created, it) }
                .getOrThrow()
        }

        put(registerPath) {
            val register = call.parameters.getOrFail("register")
            replaceStreetMarket.replace(register, call.receive())
                .onSuccess { call.response.status(HttpStatusCode.OK) }
                .getOrThrow()
        }

        delete(registerPath) {
            val register = call.parameters.getOrFail("register")
            removeStreetMarket.remove(register)
                .onSuccess { call.response.status(HttpStatusCode.NoContent) }
                .getOrThrow()
        }
    }
}
