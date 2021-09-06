package com.streetmarket.adapters.outbound.persistence

import com.streetmarket.core.domain.StreetMarket

data class Page(
    val page: Long,
    val size: Int,
    val total: Long,
    val filter: Map<String, String>,
    val data: Collection<StreetMarket>
)
