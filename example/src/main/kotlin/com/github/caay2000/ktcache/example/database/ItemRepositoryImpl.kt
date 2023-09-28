package com.github.caay2000.ktcache.example.database

import com.github.caay2000.ktcache.example.application.Item
import com.github.caay2000.ktcache.example.application.ItemRepository
import java.util.UUID

class ItemRepositoryImpl : ItemRepository {

    private val database: MutableMap<UUID, Item> = mutableMapOf()

    override fun exists(id: UUID): Boolean = database[id] != null

    override fun findById(id: UUID): Item? = database[id]

    override fun findAll(): List<Item> = database.values.toList()

    override fun save(item: Item) {
        database[item.id] = item
    }

    fun clean() = database.clear()
}
