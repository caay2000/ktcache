package com.github.caay2000.ktcache.internal

import java.util.UUID

internal class KtCacheObject(cacheName: String = "ThreadId-${Thread.currentThread().id}--${UUID.randomUUID()}") {

    private val cacheMap: MutableMap<String, Any?> = mutableMapOf()
    private val statistics: KtCacheStats = KtCacheStats(cacheName)
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
        if (openCounter == 0) clean()
        openCounter++
    }

    private fun closeCacheContext() {
        openCounter--
        if (openCounter <= 0) {
            openCounter = 0
        }
    }

    fun clean() {
        cacheMap.clear()
        statistics.clean()
    }

    fun statistics() = statistics
}
