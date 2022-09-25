package com.gihtub.caay2000.ktcache

import com.github.caay2000.ktcache.KtCache
import com.github.caay2000.ktcache.KtCache.cached
import com.github.caay2000.ktcache.KtCache.closeCacheContext
import com.github.caay2000.ktcache.KtCache.openCacheContext
import com.github.caay2000.ktcache.KtCacheStats
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KLogger
import mu.KotlinLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CacheManagementTest {

    private val logger: KLogger = KotlinLogging.logger {}

    @Test
    fun `cache working if context is open`() {

        openCacheContext()
        testCacheInThread("no_context")
        val cacheStatistics = KtCache.stats
        closeCacheContext()
        cacheStatistics.assertCacheStatistics(size = 1, accessCount = 100, hitCount = 99, missCount = 1)
    }

    @Test
    fun `cache not working if context is not open`() {

        testCacheInThread("no_context")
        val cacheStatistics = KtCache.stats
        cacheStatistics.assertCacheStatistics(size = 0, accessCount = 0, hitCount = 0, missCount = 0)
    }

    @Test
    fun `cache is cleaned when context is closed`() {

        openCacheContext()
        testCacheInThread("no_context")
        val cacheStatisticsBeforeClean = KtCache.stats
        closeCacheContext()
        cacheStatisticsBeforeClean.assertCacheStatistics(size = 1, accessCount = 100, hitCount = 99, missCount = 1)

        val cacheStatisticsAfterClean = KtCache.stats
        cacheStatisticsAfterClean.assertCacheStatistics(size = 0, accessCount = 0, hitCount = 0, missCount = 0)

        openCacheContext()
        testCacheInThread("no_context")
        val secondCacheStatistics = KtCache.stats
        closeCacheContext()
        secondCacheStatistics.assertCacheStatistics(size = 1, accessCount = 100, hitCount = 99, missCount = 1)
    }

    @Test
    fun `multiple caches in different threads does not conflict with cache`() {

        var statisticsCacheThread1: KtCacheStats? = null
        var statisticsCacheThread2: KtCacheStats? = null

        val thread1 = Thread {
            openCacheContext()
            testCacheInThread("key_1")
            println("thread1")
            testCacheInThread("key_2")
            println("thread1")
            testCacheInThread("key_1")
            println("thread1")
            statisticsCacheThread1 = KtCache.stats
            closeCacheContext()
        }

        val thread2 = Thread {
            openCacheContext()
            testCacheInThread("key_1")
            println("thread2")
            testCacheInThread("key_1")
            println("thread2")
            testCacheInThread("key_2")
            println("thread2")
            testCacheInThread("key_3")
            println("thread2")
            statisticsCacheThread2 = KtCache.stats
            closeCacheContext()
        }

        thread1.start()
        thread2.start()

        thread1.join()
        thread2.join()

        statisticsCacheThread1!!.assertCacheStatistics(size = 2, accessCount = 300, hitCount = 298, missCount = 2)
        statisticsCacheThread2!!.assertCacheStatistics(size = 3, accessCount = 400, hitCount = 397, missCount = 3)
    }

    @Test
    fun `multiple caches in different coroutines does not conflict with cache`() {

        var statisticsCacheThread1: KtCacheStats? = null
        var statisticsCacheThread2: KtCacheStats? = null

        runBlocking {
            coroutineScope {
                val job1 = launch {
                    openCacheContext()
                    testCacheInThread("key_1")
                    println("launch1")
                    testCacheInThread("key_2")
                    println("launch1")
                    testCacheInThread("key_1")
                    println("launch1")
                    statisticsCacheThread1 = KtCache.stats
                    closeCacheContext()
                }

                val job2 = launch {
                    openCacheContext()
                    testCacheInThread("key_1")
                    println("launch2")
                    testCacheInThread("key_1")
                    println("launch2")
                    testCacheInThread("key_2")
                    println("launch2")
                    testCacheInThread("key_3")
                    println("launch2")
                    statisticsCacheThread2 = KtCache.stats
                    closeCacheContext()
                }

                job1.join()
                job2.join()
            }
        }

        statisticsCacheThread1!!.assertCacheStatistics(size = 2, accessCount = 300, hitCount = 298, missCount = 2)
        statisticsCacheThread2!!.assertCacheStatistics(size = 3, accessCount = 400, hitCount = 397, missCount = 3)
    }

    @Test
    fun `launch lot of coroutines`() {

        var statisticsCacheThread: KtCacheStats? = null

        runBlocking {
            coroutineScope {
                repeat(300) {
                    launch {
                        openCacheContext()
                        testCacheInThread("key_1")
                        testCacheInThread("key_2")
                        testCacheInThread("key_1")
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
        logger.info { this }
    }

    private fun testCacheInThread(cacheKey: String) {
        for (i in 1..100) {
            cached("method+$cacheKey") {
                method(cacheKey)
            }
        }
    }

    private fun method(parameter: String): String = parameter
}
