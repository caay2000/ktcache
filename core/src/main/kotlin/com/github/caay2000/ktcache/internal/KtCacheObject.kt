package com.github.caay2000.ktcache.internal

import com.github.caay2000.ktcache.internal.KtCacheContext.Companion.updateTotalStatistics

internal class KtCacheObject {

    private val cacheMap: MutableMap<String, Any?> = mutableMapOf()
    private val statistics: KtCacheStats = KtCacheStats()

    var openCounter = 0

    fun getItemOrSet(key: String, block: () -> Any?): Any? {
        statistics.access()
        if (cacheMap.containsKey(key)) {
            statistics.hit()
        } else {
            statistics.miss()
            cacheMap[key] = block()
        }
        return this.cacheMap[key]
    }

    fun <T> cacheContext(block: () -> T): T =
        try {
            openCacheContext()
            block()
        } finally {
            closeCacheContext()
        }

    private fun openCacheContext() {
        openCounter++
    }

    private fun closeCacheContext() {
        openCounter--
        if (openCounter <= 0) {
            openCounter = 0
            clean()
        }
    }

    fun clean() {
        cacheMap.clear()
        updateTotalStatistics(statistics)
        statistics.clean()
    }

    fun statistics() = statistics
}
