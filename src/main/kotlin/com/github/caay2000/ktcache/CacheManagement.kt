package com.github.caay2000.ktcache

import kotlin.concurrent.getOrSet

class CacheManagement {

    companion object {

        private val threadLocal = ThreadLocal<CacheObject>()
        private var openCounter = 0

        @Suppress("UNCHECKED_CAST")
        fun <T : Any> cached(key: String, block: () -> T): T {
            return if (openCounter > 0) {
                getCacheObject().getItemOrSet(key, block)
            } else {
                block()
            } as T
        }

        fun cacheStatistics() = getCacheObject().statistics()

        fun openCacheContext() {
            openCounter++
        }

        fun closeCacheContext() {
            openCounter--
            if (openCounter <= 0) {
                openCounter = 0
                getCacheObject().clean()
            }
        }

        private fun getCacheObject(): CacheObject = threadLocal.getOrSet { CacheObject() }
    }

    private class CacheObject(private val cacheName: String = "ThreadId-${Thread.currentThread().id}") {

        private val cacheMap: MutableMap<String, Any> = mutableMapOf()

        private var size: Int = 0
        private var accessCount: Int = 0
        private var hitCount: Int = 0
        private var missCount: Int = 0

        fun getItemOrSet(key: String, block: () -> Any): Any {
            accessCount++
            if (cacheMap.containsKey(key)) {
                hitCount++
            } else {
                size++
                missCount++
                cacheMap[key] = block
            }
            return this.cacheMap[key]!!
        }

        fun clean() {
            this.cacheMap.clear()
            size = 0
            accessCount = 0
            hitCount = 0
            missCount = 0
        }

        fun statistics() = CacheStatistics(cacheName, size, accessCount, hitCount, missCount)
    }

    data class CacheStatistics(
        val cacheName: String,
        val size: Int,
        val accessCount: Int,
        val hitCount: Int,
        val missCount: Int,

        val hitRatio: Double? = if (accessCount == 0) null else hitCount.toDouble().div(accessCount.toDouble()),
        val missRatio: Double? = if (accessCount == 0) null else missCount.toDouble().div(accessCount.toDouble())
    )
}
