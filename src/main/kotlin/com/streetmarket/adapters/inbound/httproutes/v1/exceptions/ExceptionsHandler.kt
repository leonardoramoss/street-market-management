package com.streetmarket.adapters.inbound.httproutes.v1.exceptions

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.streetmarket.core.exceptions.StreetMarketException
import com.streetmarket.core.exceptions.StreetMarketException.StreetMarketAlreadyExistsException
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

        exception<MissingKotlinParameterException> {
            val field = it.message
                ?.substringAfterLast("[")
                ?.substringBefore("]")
                ?.replace("\"", "'")

            environment.log.error("Request error: $it")

            if (field != null) {
                call.respondFailure(HttpStatusCode.BadRequest, StreetMarketStateException("field $field not found"))
            } else {
                call.respondFailure(HttpStatusCode.BadRequest, it)
            }
        }

        exception<StreetMarketException> {
            when (it) {
                is StreetMarketNotFoundException -> call.respondFailure(HttpStatusCode.NotFound, it)
                is StreetMarketStateException -> call.respondFailure(HttpStatusCode.BadRequest, it)
                is StreetMarketAlreadyExistsException -> call.respondFailure(HttpStatusCode.Conflict, it)
            }
        }

        exception<ExposedSQLException> { exposedSQLException ->
            val duplicateKeyCode = "23505"
            when (exposedSQLException.sqlState) {
                duplicateKeyCode -> {
                    val message =
                        (exposedSQLException.cause.takeIf { it != null }?.message ?: exposedSQLException.message)
                            ?.substringAfter("Detail: ")
                    if (message != null) {
                        call.respondFailure(HttpStatusCode.Conflict, StreetMarketStateException(message))
                    } else {
                        call.respondFailure(HttpStatusCode.Conflict, exposedSQLException)
                    }
                }
                else -> call.respondFailure(
                    HttpStatusCode.InternalServerError, StreetMarketStateException("InternalServerError")
                )
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
