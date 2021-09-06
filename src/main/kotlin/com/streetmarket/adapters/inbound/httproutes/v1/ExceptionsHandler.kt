package com.streetmarket.adapters.inbound.httproutes.v1

import com.streetmarket.core.exceptions.StreetMarketException
import com.streetmarket.core.exceptions.StreetMarketException.StreetMarketNotFoundException
import com.streetmarket.core.exceptions.StreetMarketException.StreetMarketStateException
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import org.jetbrains.exposed.exceptions.ExposedSQLException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun Application.handleHttpExceptions() {
    install(StatusPages) {
        exception<StreetMarketException> {
            when (it) {
                is StreetMarketNotFoundException -> call.respondFailure(HttpStatusCode.NotFound, it)
                is StreetMarketStateException -> call.respondFailure(HttpStatusCode.BadRequest, it)
            }
        }

        exception<ExposedSQLException> {
            val duplicateKeyCode = "23505"
            when (it.sqlState) {
                duplicateKeyCode -> call.respondFailure(HttpStatusCode.Conflict, it)
            }
        }
    }
}

suspend inline fun ApplicationCall.respondFailure(status: HttpStatusCode, exception: Throwable) {
    response.status(status)
    respond(
        mapOf(
            "code" to status.value,
            "message" to (exception.cause.takeIf { it != null }?.message ?: exception.message),
            "timestamp" to DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
        )
    )
}
