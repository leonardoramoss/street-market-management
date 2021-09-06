package com.streetmarket.core.spi

interface Repository<T, ID> {
    fun save(domain: T): T

    fun findById(id: ID): T?

    fun delete(id: ID)

    fun replace(id: ID, domain: T): T
}
