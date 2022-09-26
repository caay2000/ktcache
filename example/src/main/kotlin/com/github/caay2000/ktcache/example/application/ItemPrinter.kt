package com.github.caay2000.ktcache.example.application

import mu.KLogger
import mu.KotlinLogging
import java.util.UUID

class ItemPrinter(repository: ItemRepository) {

    private val logger: KLogger = KotlinLogging.logger {}

    private val retriever = ItemFinder(repository)

    fun invoke(id: UUID) {
        val item = retriever.invoke(id)
        logger.info { item }
    }
}
