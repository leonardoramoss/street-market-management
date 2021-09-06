package com.streetmarket.core.service

import com.streetmarket.core.domain.StreetMarket
import com.streetmarket.core.exceptions.StreetMarketException.StreetMarketStateException
import com.streetmarket.core.spi.Repository

class ReplaceStreetMarket(private val repository: Repository<StreetMarket, String>) {
    fun replace(register: String, streetMarket: StreetMarket): Result<StreetMarket> =
        runCatching {
            if (register != streetMarket.register)
                throw StreetMarketStateException("$register not matches with ${streetMarket.register}")

            repository.replace(register, streetMarket)
        }
}
