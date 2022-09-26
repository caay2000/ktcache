package com.github.caay2000.ktcache.example.application

import com.github.caay2000.ktcache.KtCache.cacheContext
import mu.KLogger
import mu.KotlinLogging
import java.util.UUID

class ItemApplication(private val repository: ItemRepository) {

    private val logger: KLogger = KotlinLogging.logger {}

    private val itemPrinter = ItemPrinter(repository)
    private val itemFinder = ItemFinder(repository)

    fun create(id: UUID, name: String, value: Int) {
        if (repository.exists(id).not()) {
            val item = Item(id = id, name = name, value = value)
            repository.save(item)
            logger.info { "Created $item" }
        } else {
            throw RuntimeException("item $id already exists")
        }
    }

    fun update(id: UUID, newValue: Int) {
        val item = itemFinder.invoke(id)
        repository.save(item.update(newValue))
        logger.info { "Updated $item" }
    }

    fun retrieveItem(id: UUID): Item =
        cacheContext {
            itemPrinter.invoke(id)
            itemFinder.invoke(id)
        }

    fun getItemOrNull(id: UUID): Item? =
        cacheContext {
            try {
                itemFinder.invoke(id)
            } catch (e: Throwable) {
                null
            }
        }

    fun doublePrinter(id: UUID) =
        cacheContext {
            itemPrinter.invoke(id)
            itemPrinter.invoke(id)
        }
}
