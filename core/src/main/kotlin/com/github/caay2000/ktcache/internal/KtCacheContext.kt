package com.github.caay2000.ktcache.internal

import kotlin.concurrent.getOrSet

internal class KtCacheContext {

    private val threadLocal = ThreadLocal<KtCacheObject>()

    val stats: KtCacheStats
        get() = getCacheObject().statistics()

    val totalStats: KtCacheStats
        get() = totalStatistics

    internal companion object {

        private var totalStatistics: KtCacheStats = KtCacheStats()

        private fun KtCacheStats.update(ktCacheStats: KtCacheStats): KtCacheStats =
            KtCacheStats(
                size = this.size + ktCacheStats.size,
                accessCount = this.accessCount + ktCacheStats.accessCount,
                hitCount = this.hitCount + ktCacheStats.hitCount,
                missCount = this.missCount + ktCacheStats.missCount,
            )

        @Synchronized
        fun updateTotalStatistics(stats: KtCacheStats) {
            totalStatistics = totalStatistics.update(stats)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> useCache(key: String, block: () -> T?): T? =
        if (getCacheObject().openCounter > 0) {
            getCacheObject().getItemOrSet(key, block) as T?
        } else {
            block()
        }

    fun <T> cacheContext(block: () -> T): T = getCacheObject().cacheContext(block)

    private fun getCacheObject(): KtCacheObject = threadLocal.getOrSet { KtCacheObject() }
}
