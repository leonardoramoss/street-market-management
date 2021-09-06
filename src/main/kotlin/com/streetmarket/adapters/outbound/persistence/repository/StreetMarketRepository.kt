package com.streetmarket.adapters.outbound.persistence.repository

import com.streetmarket.adapters.outbound.persistence.Page
import com.streetmarket.core.domain.StreetMarket
import com.streetmarket.core.exceptions.StreetMarketException.StreetMarketAlreadyExistsException
import com.streetmarket.core.exceptions.StreetMarketException.StreetMarketNotFoundException
import com.streetmarket.core.spi.Repository
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object StreetMarketRepository : Table("street_market"), Repository<StreetMarket, String> {

    private val id = long("id").autoIncrement("sq_street_market")
    private val register = varchar("register", 10)
    private val name = varchar("name", 250)
    private val street = varchar("street", 250)
    private val number = varchar("number", 10).nullable()
    private val neighborhood = varchar("neighborhood", 250)
    private val landmark = varchar("landmark", 50).nullable()
    private val lat = decimal("lat", 10, 0)
    private val long = decimal("long", 10, 0)
    private val sector = long("sector")
    private val weightingArea = long("weighting_area")
    private val districtCode = long("district_code")
    private val district = varchar("district", 250)
    private val boroughCode = long("borough_code")
    private val borough = varchar("borough", 250)
    private val region5 = varchar("region5", 20)
    private val region8 = varchar("region8", 20)

    override val primaryKey = PrimaryKey(id)

    override fun save(domain: StreetMarket): StreetMarket =
        transaction {

            findById(domain.register)?.let { throw StreetMarketAlreadyExistsException(domain.register) }

            insert { entityMapper(it, domain) }

            domain
        }

    override fun replace(id: String, domain: StreetMarket): StreetMarket =
        transaction {

            findById(id) ?: throw StreetMarketNotFoundException(id)

            update { entityMapper(it, domain) }

            domain
        }

    override fun delete(id: String) {
        transaction {

            findById(id) ?: throw StreetMarketNotFoundException(id)

            deleteWhere { register eq id }
        }
    }

    override fun findById(id: String): StreetMarket? =
        transaction {
            StreetMarketRepository
                .select { register eq id }
                .map(domainMapper())
                .singleOrNull()
        }

    @Suppress("UNCHECKED_CAST")
    fun search(filters: Map<String, String>, page: Long, limit: Int): Page =
        transaction {
            val query = StreetMarketRepository.selectAll()

            filters.forEach { (column, value) ->
                columns.find { it.name == column }?.let {
                    query.andWhere { it as Column<String> eq value }
                }
            }

            val count = query.count()

            val offset = limit * page
            val result = query.limit(limit, offset).map(domainMapper())

            Page(page, result.count(), count, filters, result)
        }

    private fun domainMapper(): (ResultRow) -> StreetMarket = { row ->
        StreetMarket(
            row[register],
            row[name],
            StreetMarket.Census(
                row[sector],
                row[weightingArea],
                row[districtCode],
                row[district],
                row[boroughCode],
                row[borough],
                row[region5],
                row[region8]
            ),
            StreetMarket.Address(
                row[street],
                row[number],
                row[neighborhood],
                row[landmark]
            ),
            StreetMarket.Geolocation(
                row[lat],
                row[long]
            )
        )
    }

    private fun entityMapper(
        builder: UpdateBuilder<*>,
        domain: StreetMarket
    ) {
        val address = domain.address
        val geolocation = domain.geolocation
        val census = domain.census

        builder[register] = domain.register
        builder[name] = domain.name
        builder[street] = address.street
        builder[number] = address.number
        builder[neighborhood] = address.neighborhood
        builder[landmark] = address.landmark
        builder[lat] = geolocation.lat
        builder[long] = geolocation.long
        builder[sector] = census.sector
        builder[weightingArea] = census.weightingArea
        builder[districtCode] = census.districtCode
        builder[district] = census.district
        builder[boroughCode] = census.boroughCode
        builder[borough] = census.borough
        builder[region5] = census.region5
        builder[region8] = census.region8
    }
}
