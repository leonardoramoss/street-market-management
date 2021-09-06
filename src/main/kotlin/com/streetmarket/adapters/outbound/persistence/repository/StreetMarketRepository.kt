package com.streetmarket.adapters.outbound.persistence.repository

import com.streetmarket.adapters.outbound.persistence.Page
import com.streetmarket.core.domain.StreetMarket
import com.streetmarket.core.exceptions.StreetMarketException.StreetMarketNotFoundException
import com.streetmarket.core.spi.Repository
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.replace
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.util.UUID

object StreetMarketRepository : Table("street_market"), Repository<StreetMarket, String> {

    private val uuid = uuid("uuid")
    private val register = varchar("register", 10)
    private val name = varchar("name", 250)
    private val street = varchar("street", 250)
    private val number = varchar("number", 10).nullable()
    private val neighborhood = varchar("neighborhood", 250)
    private val landmark = varchar("landmark", 50).nullable()
    private val sectorId = long("sector_id").references(CensusRepository.sector, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(uuid)

    override fun save(domain: StreetMarket): StreetMarket =
        transaction {
            val sectorId = CensusRepository.insert {
                entityMapper(it, domain.census)
            } get CensusRepository.sector

            insert { entityMapper(it, domain, sectorId) }

            domain
        }

    override fun replace(id: String, domain: StreetMarket): StreetMarket =
        transaction {

            findById(id) ?: throw StreetMarketNotFoundException(id)

            val sectorId = CensusRepository.replace {
                entityMapper(it, domain.census)
            } get CensusRepository.sector

            replace { entityMapper(it, domain, sectorId) }

            domain
        }

    override fun delete(id: String) {
        transaction {

            val streetMarket = findById(id) ?: throw StreetMarketNotFoundException(id)

            CensusRepository.deleteWhere { CensusRepository.sector eq streetMarket.census.sector }
            deleteWhere { uuid eq UUID.nameUUIDFromBytes(id.toByteArray()) }
        }
    }

    override fun findById(id: String): StreetMarket? =
        transaction {
            (StreetMarketRepository innerJoin CensusRepository)
                .select { uuid eq UUID.nameUUIDFromBytes(id.toByteArray()) }
                .map(domainMapper())
                .singleOrNull()
        }

    @Suppress("UNCHECKED_CAST")
    fun search(filters: Map<String, String>, page: Long, limit: Int): Page =
        transaction {
            val query = (StreetMarketRepository innerJoin CensusRepository).selectAll()

            filters.forEach { (column, value) ->
                (columns + CensusRepository.columns).find { it.name == column }?.let {
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
            CensusRepository.domainMapper(row),
            StreetMarket.Address(
                row[street],
                row[number],
                row[neighborhood],
                row[landmark]
            ),
            StreetMarket.Geolocation(
                BigDecimal.ONE,
                BigDecimal.ONE
            )
        )
    }

    private fun entityMapper(
        builder: UpdateBuilder<*>,
        domain: StreetMarket,
        sectorId: Long
    ) {
        val address = domain.address

        builder[uuid] = UUID.nameUUIDFromBytes(domain.register.toByteArray())
        builder[register] = domain.register
        builder[name] = domain.name
        builder[street] = address.street
        builder[number] = address.number
        builder[neighborhood] = address.neighborhood
        builder[landmark] = address.landmark
        builder[this.sectorId] = sectorId
    }
}
