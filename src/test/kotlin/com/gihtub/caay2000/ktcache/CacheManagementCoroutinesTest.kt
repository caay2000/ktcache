package com.gihtub.caay2000.ktcache

import com.github.caay2000.ktcache.KtCache
import com.github.caay2000.ktcache.KtCache.cached
import com.github.caay2000.ktcache.KtCache.closeCacheContext
import com.github.caay2000.ktcache.KtCache.openCacheContext
import com.github.caay2000.ktcache.KtCacheStats
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled
class CacheManagementCoroutinesTest {

    @Test

    fun `launch coroutines inside other coroutines`() {

        var statisticsCacheThread: KtCacheStats? = null

        runBlocking {
            coroutineScope {
                repeat(300) {
                    launch {
                        openCacheContext()
                        testCacheInCoroutineWithContext("key_1")
                        launch { testCacheInCoroutineWithContext("key_2") }
                        launch { testCacheInCoroutineWithContext("key_1") }
                        statisticsCacheThread = KtCache.stats
                        closeCacheContext()
                    }
                }
            }
        }

        statisticsCacheThread!!.assertCacheStatistics(size = 2, accessCount = 300, hitCount = 298, missCount = 2)
    }

    private fun KtCacheStats.assertCacheStatistics(size: Int, accessCount: Int, hitCount: Int, missCount: Int) {
        assertThat(this.size).isEqualTo(size)
        assertThat(this.accessCount).isEqualTo(accessCount)
        assertThat(this.hitCount).isEqualTo(hitCount)
        assertThat(this.missCount).isEqualTo(missCount)
    }

    private suspend fun testCacheInCoroutineWithContext(cacheKey: String) {
        for (i in 1..100) {
//            withContext(KtCacheContext()) {
            cached("method+$cacheKey") {
                method(cacheKey)
            }
//            }
        }
    }

    private fun method(parameter: String): String = parameter
}
