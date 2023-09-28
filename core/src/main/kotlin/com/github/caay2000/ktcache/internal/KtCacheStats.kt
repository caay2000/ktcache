package com.github.caay2000.ktcache.internal

internal data class KtCacheStats internal constructor(
    var size: Int = 0,
    var accessCount: Int = 0,
    var hitCount: Int = 0,
    var missCount: Int = 0,
) {

    fun access() {
        accessCount++
    }

    fun hit() {
        hitCount++
    }

    fun miss() {
        missCount++
        size++
    }

    fun clean() {
        size = 0
        accessCount = 0
        hitCount = 0
        missCount = 0
    }
}
