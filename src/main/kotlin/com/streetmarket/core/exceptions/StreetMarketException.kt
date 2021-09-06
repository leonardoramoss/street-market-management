package com.streetmarket.core.exceptions

sealed class StreetMarketException(message: String) : RuntimeException(message) {

    data class StreetMarketNotFoundException(
        val id: String,
        override val message: String = "Street market with id $id not found"
    ) : StreetMarketException(message)

    data class StreetMarketStateException(
        override val message: String
    ) : StreetMarketException(message)
}
