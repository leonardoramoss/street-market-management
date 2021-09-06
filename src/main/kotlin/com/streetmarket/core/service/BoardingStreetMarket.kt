package com.streetmarket.core.service

import com.streetmarket.core.domain.StreetMarket
import com.streetmarket.core.spi.Repository

class BoardingStreetMarket(private val repository: Repository<StreetMarket, String>) {
    fun onboard(streetMarket: StreetMarket): Result<StreetMarket> =
        runCatching { repository.save(streetMarket) }
}
