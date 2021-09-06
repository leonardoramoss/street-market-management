package com.streetmarket.adapters.inbound.httproutes.v1

import com.streetmarket.adapters.inbound.httproutes.v1.request.SearchFilter
import com.streetmarket.adapters.outbound.persistence.repository.StreetMarketRepository
import io.ktor.application.call
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.util.getOrFail

fun Route.streetMarketQueryRoutes() {

    val repository = StreetMarketRepository

    route("/v1/market") {

        get("/{register}") {
            val register = call.parameters.getOrFail("register")
            runCatching { repository.findById(register) }
                .onSuccess { it?.run { call.respond(OK, it) } ?: call.response.status(NotFound) }
                .getOrThrow()
        }

        get("/search") {
            val page = call.parameters["page"]?.toLong() ?: 0
            val limit = call.parameters["size"]?.toInt() ?: 10

            val filters = SearchFilter.values()
                .mapNotNull {
                    call.parameters[it.name.lowercase()]
                        ?.let { value -> it.name.lowercase() to value }
                }.toMap()

            runCatching { repository.search(filters, page, limit) }
                .onSuccess { call.respond(it) }
                .getOrThrow()
        }
    }
}
