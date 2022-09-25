package com.github.caay2000.ktcache

import com.github.caay2000.ktcache.internal.KtCacheContext

object KtCache {

    private val ktCacheContext = KtCacheContext()

    fun openCacheContext() = ktCacheContext.openCacheContext()
    fun closeCacheContext() = ktCacheContext.closeCacheContext()

    val stats: KtCacheStats
        get() = ktCacheContext.stats.toCacheStats()

    fun <T : Any> cached(key: String, block: () -> T): T = ktCacheContext.useCache(key, block)

    private fun com.github.caay2000.ktcache.internal.KtCacheStats.toCacheStats() =
        KtCacheStats(
            cacheName = cacheName,
            size = size,
            accessCount = accessCount,
            hitCount = hitCount,
            missCount = missCount
        )
}
