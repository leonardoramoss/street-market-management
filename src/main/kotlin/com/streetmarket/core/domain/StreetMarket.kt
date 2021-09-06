package com.streetmarket.core.domain

import java.math.BigDecimal

data class StreetMarket(
    val register: String,
    val name: String,
    val census: Census,
    val address: Address,
    val geolocation: Geolocation
) {

    data class Census(
        val sector: Long,
        val weightingArea: Long,
        val districtCode: Long,
        val district: String,
        val boroughCode: Long,
        val borough: String,
        val region5: String,
        val region8: String
    )

    data class Address(
        val street: String,
        val number: String? = "S/N",
        val neighborhood: String,
        val landmark: String? = null,
    )

    data class Geolocation(
        val lat: BigDecimal,
        val long: BigDecimal
    )
}
