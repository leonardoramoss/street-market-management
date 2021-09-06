package com.streetmarket.configuration

import com.streetmarket.adapters.outbound.persistence.repository.StreetMarketRepository
import com.streetmarket.core.service.BoardingStreetMarket
import com.streetmarket.core.service.RemoveStreetMarket
import com.streetmarket.core.service.ReplaceStreetMarket
import com.streetmarket.core.spi.Repository
import io.ktor.application.Application
import io.ktor.application.install
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.ktor.ext.Koin

fun Application.koinConfiguration() {

    install(Koin) {
        modules(
            module {
                single { StreetMarketRepository } bind Repository::class
                single { BoardingStreetMarket(get()) }
                single { ReplaceStreetMarket(get()) }
                single { RemoveStreetMarket(get()) }
            }
        )
    }
}
