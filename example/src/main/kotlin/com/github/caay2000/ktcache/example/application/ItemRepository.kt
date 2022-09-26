package com.github.caay2000.ktcache.example.application

import java.util.UUID

interface ItemRepository {

    fun exists(id: UUID): Boolean
    fun findById(id: UUID): Item?
    fun findAll(): List<Item>
    fun save(item: Item)
}
