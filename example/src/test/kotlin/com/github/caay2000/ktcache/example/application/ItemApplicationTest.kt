package com.github.caay2000.ktcache.example.application

import com.github.caay2000.ktcache.KtCache
import com.github.caay2000.ktcache.KtCacheStats
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID

internal class ItemApplicationTest {

    private val itemRepository = mock<ItemRepository>()

    private val sut = ItemApplication(itemRepository)

    private val item = Item(
        id = UUID.randomUUID(),
        name = "name",
        value = 1
    )

    @Test
    fun `retrieve Item just calls repository 1 time`() {

        KtCache.cacheContext {
            whenever(itemRepository.findById(item.id)).thenReturn(item)

            val result = sut.retrieveItem(item.id)

            assertThat(result).isEqualTo(item)

            verify(itemRepository, times(1)).findById(item.id)
            KtCache.stats.assertCacheStatistics(1, 2, 1, 1)
        }
    }

    private fun KtCacheStats.assertCacheStatistics(size: Int, accessCount: Int, hitCount: Int, missCount: Int) {
        assertThat(this.size).isEqualTo(size)
        assertThat(this.accessCount).isEqualTo(accessCount)
        assertThat(this.hitCount).isEqualTo(hitCount)
        assertThat(this.missCount).isEqualTo(missCount)
    }
}
