package com.github.caay2000.ktcache.example.database

import com.github.caay2000.ktcache.KtCache
import com.github.caay2000.ktcache.KtCacheStats
import com.github.caay2000.ktcache.example.application.Item
import com.github.caay2000.ktcache.example.application.ItemApplication
import mu.KLogger
import mu.KotlinLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class IntegrationTest {

    private val logger: KLogger = KotlinLogging.logger {}

    private val itemRepository = ItemRepositoryImpl()

    private val sut = ItemApplication(itemRepository)

    private val item = Item(id = UUID.randomUUID(), name = "name", value = 1)
    private val updatedItem = item.copy(value = 2)

    @BeforeEach
    fun cleanUp() {
        itemRepository.clean()
    }

    @Test
    fun `retrieve Item just calls repository 1 time`() {

        sut.create(item.id, item.name, item.value)

        val result = sut.retrieveItem(item.id)

        assertThat(result).isEqualTo(item)
        KtCache.stats.assertCacheStatistics(1, 2, 1, 1)
    }

    @Test
    fun `works when returning null`() {

        val result = sut.getItemOrNull(UUID.randomUUID())

        assertThat(result).isEqualTo(null)
        KtCache.stats.assertCacheStatistics(1, 1, 0, 1)
    }

    @Test
    fun `works when returning Unit`() {
        sut.create(item.id, item.name, item.value)

        sut.doublePrinter(item.id)

        KtCache.stats.assertCacheStatistics(1, 2, 1, 1)
    }

    @Test
    fun `cache is cleaned for each request`() {

        sut.create(item.id, item.name, item.value)
        repeat(10) {
            val result = sut.retrieveItem(item.id)
            assertThat(result).isEqualTo(item)
        }
        KtCache.stats.assertCacheStatistics(1, 2, 1, 1)
    }

    @Test
    fun `cache is cleaned for each request with update in between`() {

        sut.create(item.id, item.name, item.value)

        val result = sut.retrieveItem(item.id)
        assertThat(result).isEqualTo(item)
        KtCache.stats.assertCacheStatistics(1, 2, 1, 1)

        sut.update(item.id, updatedItem.value)
        val updatedResult = sut.retrieveItem(item.id)

        assertThat(updatedResult).isEqualTo(updatedItem)
        KtCache.stats.assertCacheStatistics(1, 2, 1, 1)
    }

    private fun KtCacheStats.assertCacheStatistics(size: Int, accessCount: Int, hitCount: Int, missCount: Int) {
        assertThat(this.size).isEqualTo(size)
        assertThat(this.accessCount).isEqualTo(accessCount)
        assertThat(this.hitCount).isEqualTo(hitCount)
        assertThat(this.missCount).isEqualTo(missCount)
        logger.info { this }
    }
}
