package com.streetmarket.core.service

import com.streetmarket.core.domain.StreetMarket
import com.streetmarket.core.spi.Repository

class RemoveStreetMarket(private val repository: Repository<StreetMarket, String>) {
    fun remove(register: String): Result<Unit> = runCatching { repository.delete(register) }
}
