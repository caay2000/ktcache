package com.github.caay2000.ktcache

data class KtCacheStats(
    val cacheName: String,
    val size: Int,
    val accessCount: Int,
    val hitCount: Int,
    val missCount: Int,

    val hitRatio: Double = if (accessCount == 0) ZERO else hitCount.toDouble().div(accessCount.toDouble()),
    val missRatio: Double = if (accessCount == 0) ZERO else missCount.toDouble().div(accessCount.toDouble())
) {
    private companion object {
        const val ZERO: Double = 0.0
    }
}
