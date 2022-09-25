package com.github.caay2000.ktcache.internal

import kotlin.concurrent.getOrSet

internal class KtCacheContext {

    private val threadLocal = ThreadLocal<KtCacheObject>()

    val stats: KtCacheStats
        get() = getCacheObject().statistics()

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> useCache(key: String, block: () -> T): T =
        if (getCacheObject().openCounter > 0) {
            getCacheObject().getItemOrSet(key, block) as T
        } else {
            block()
        }

    fun openCacheContext() = getCacheObject().openCacheContext()
    fun closeCacheContext() = getCacheObject().closeCacheContext()

    private fun getCacheObject(): KtCacheObject = threadLocal.getOrSet { KtCacheObject() }
}
