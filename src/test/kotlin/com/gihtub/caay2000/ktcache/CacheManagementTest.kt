package com.gihtub.caay2000.ktcache

import com.github.caay2000.ktcache.CacheManagement.CacheStatistics
import com.github.caay2000.ktcache.CacheManagement.Companion.cacheStatistics
import com.github.caay2000.ktcache.CacheManagement.Companion.cached
import com.github.caay2000.ktcache.CacheManagement.Companion.closeCacheContext
import com.github.caay2000.ktcache.CacheManagement.Companion.openCacheContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CacheManagementTest {

    @Test
    fun `cache working if context is open`() {

        openCacheContext()
        testCacheInThread("no_context")
        val cacheStatistics = cacheStatistics()
        closeCacheContext()
        cacheStatistics.assertCacheStatistics(size = 1, accessCount = 100, hitCount = 99, missCount = 1)
    }

    @Test
    fun `cache not working if context is not open`() {

        testCacheInThread("no_context")
        val cacheStatistics = cacheStatistics()
        cacheStatistics.assertCacheStatistics(size = 0, accessCount = 0, hitCount = 0, missCount = 0)
    }

    @Test
    fun `cache is cleaned when context is closed`() {

        openCacheContext()
        testCacheInThread("no_context")
        val cacheStatisticsBeforeClean = cacheStatistics()
        closeCacheContext()
        cacheStatisticsBeforeClean.assertCacheStatistics(size = 1, accessCount = 100, hitCount = 99, missCount = 1)

        val cacheStatisticsAfterClean = cacheStatistics()
        cacheStatisticsAfterClean.assertCacheStatistics(size = 0, accessCount = 0, hitCount = 0, missCount = 0)

        openCacheContext()
        testCacheInThread("no_context")
        val secondCacheStatistics = cacheStatistics()
        closeCacheContext()
        secondCacheStatistics.assertCacheStatistics(size = 1, accessCount = 100, hitCount = 99, missCount = 1)
    }

    @Test
    fun `multiple caches in different threads does not conflict with cache`() {

        var statisticsCacheThread1: CacheStatistics? = null
        var statisticsCacheThread2: CacheStatistics? = null

        openCacheContext()
        val thread1 = Thread {
            testCacheInThread("key_1")
            testCacheInThread("key_2")
            testCacheInThread("key_1")
            statisticsCacheThread1 = cacheStatistics()
        }

        val thread2 = Thread {
            testCacheInThread("key_1")
            statisticsCacheThread2 = cacheStatistics()
        }

        thread1.start()
        thread2.start()

        thread1.join()
        thread2.join()

        statisticsCacheThread1!!.assertCacheStatistics(size = 2, accessCount = 300, hitCount = 298, missCount = 2)
        statisticsCacheThread2!!.assertCacheStatistics(size = 1, accessCount = 100, hitCount = 99, missCount = 1)
    }

    private fun CacheStatistics.assertCacheStatistics(size: Int, accessCount: Int, hitCount: Int, missCount: Int) {
        assertThat(this.size).isEqualTo(size)
        assertThat(this.accessCount).isEqualTo(accessCount)
        assertThat(this.hitCount).isEqualTo(hitCount)
        assertThat(this.missCount).isEqualTo(missCount)
    }

    private fun testCacheInThread(cacheKey: String) {

        for (i in 1..100) {
            cached("method+$cacheKey") {
                method(cacheKey)
            }
        }
    }

    private fun method(cacheKey: String): String = cacheKey
}
