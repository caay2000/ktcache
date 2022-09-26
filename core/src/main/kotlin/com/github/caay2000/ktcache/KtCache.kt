package com.github.caay2000.ktcache

import com.github.caay2000.ktcache.internal.KtCacheContext

object KtCache {

    private val ktCacheContext = KtCacheContext()

    fun <T> cacheContext(block: () -> T): T = ktCacheContext.cacheContext(block)

    fun clean() = ktCacheContext.clean()

    val stats: KtCacheStats
        get() = ktCacheContext.stats.toCacheStats()

    fun <T> cached(key: String, block: () -> T?): T? {
        return ktCacheContext.useCache(key, block)
    }

    private fun com.github.caay2000.ktcache.internal.KtCacheStats.toCacheStats() =
        KtCacheStats(
            cacheName = this.cacheName,
            size = this.size,
            accessCount = this.accessCount,
            hitCount = this.hitCount,
            missCount = this.missCount
        )
}
