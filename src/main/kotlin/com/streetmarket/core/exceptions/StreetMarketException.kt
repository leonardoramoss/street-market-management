package com.streetmarket.core.exceptions

sealed class StreetMarketException(message: String) : RuntimeException(message) {

    data class StreetMarketNotFoundException(
        val register: String,
        override val message: String = "Street market with register $register not found"
    ) : StreetMarketException(message)

    data class StreetMarketAlreadyExistsException(
        val register: String,
        override val message: String = "Street market with register $register already exists"
    ) : StreetMarketException(message)

    data class StreetMarketStateException(
        override val message: String
    ) : StreetMarketException(message)
}
