package com.streetmarket.adapters.outbound.persistence.repository

import com.streetmarket.core.domain.StreetMarket
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.UpdateBuilder

internal object CensusRepository : Table("census") {

    val sector = long("sector")
    private val weightingArea = long("weighting_area")
    private val districtCode = long("district_code")
    private val district = varchar("district", 250)
    private val boroughCode = long("borough_code")
    private val borough = varchar("borough", 250)
    private val region5 = varchar("region5", 20)
    private val region8 = varchar("region8", 20)

    override val primaryKey = PrimaryKey(sector)

    fun domainMapper(row: ResultRow): StreetMarket.Census =
        StreetMarket.Census(
            row[sector],
            row[weightingArea],
            row[districtCode],
            row[district],
            row[boroughCode],
            row[borough],
            row[region5],
            row[region8]
        )

    fun entityMapper(
        builder: UpdateBuilder<*>,
        domain: StreetMarket.Census
    ) {
        builder[sector] = domain.sector
        builder[weightingArea] = domain.weightingArea
        builder[districtCode] = domain.districtCode
        builder[district] = domain.district
        builder[boroughCode] = domain.boroughCode
        builder[borough] = domain.borough
        builder[region5] = domain.region5
        builder[region8] = domain.region8
    }
}
