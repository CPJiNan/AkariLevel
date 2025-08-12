package com.github.cpjinan.plugin.akarilevel.database.lock

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.database.lock
 *
 * @author QwQ-dev
 * @since 2025/8/12 04:43
 */
class CacheCircuitBreaker(private val config: CircuitBreakerConfig) {
    @Volatile
    private var state = CircuitBreakerState.CLOSED

    private val failureCount = AtomicInteger(0)
    private val requestCount = AtomicInteger(0)
    private val lastFailureTime = AtomicLong(0)
    private val lastStateChange = AtomicLong(System.currentTimeMillis())

    private val halfOpenTestCount = AtomicInteger(0)
    private val maxHalfOpenTests = 5

    private val recentRequests = java.util.concurrent.ConcurrentLinkedQueue<RequestRecord>()
    private val windowSizeMs = 60_000L

    fun canExecute(): Boolean {
        cleanupOldRequests()

        return when (state) {
            CircuitBreakerState.CLOSED -> true

            CircuitBreakerState.OPEN -> {
                val timeSinceFailure = System.currentTimeMillis() - lastFailureTime.get()
                if (timeSinceFailure >= config.timeoutMs) {
                    transitionToHalfOpen()
                    true
                } else {
                    false
                }
            }

            CircuitBreakerState.HALF_OPEN -> {
                halfOpenTestCount.get() < maxHalfOpenTests
            }
        }
    }

    fun recordSuccess() {
        val currentTime = System.currentTimeMillis()
        recordRequest(RequestOutcome.SUCCESS, currentTime)

        when (state) {
            CircuitBreakerState.HALF_OPEN -> {
                val successCount = halfOpenTestCount.incrementAndGet()
                if (successCount >= 3) {
                    transitionToClosed()
                }
            }

            CircuitBreakerState.CLOSED -> {
                val requests = requestCount.incrementAndGet()
                if (requests >= config.sampleSize) {
                    resetCounters()
                }
            }

            CircuitBreakerState.OPEN -> {
                // Should not happen, but handle gracefully
            }
        }
    }

    fun recordFailure() {
        val currentTime = System.currentTimeMillis()
        recordRequest(RequestOutcome.FAILURE, currentTime)
        lastFailureTime.set(currentTime)

        when (state) {
            CircuitBreakerState.HALF_OPEN -> {
                transitionToOpen()
            }

            CircuitBreakerState.CLOSED -> {
                failureCount.incrementAndGet()
                val requests = requestCount.incrementAndGet()

                if (requests >= config.sampleSize) {
                    val currentFailureRate = calculateFailureRate()
                    val threshold = config.failureThreshold.toDouble() / 100.0

                    if (currentFailureRate >= threshold) {
                        transitionToOpen()
                    } else {
                        resetCounters()
                    }
                }
            }

            CircuitBreakerState.OPEN -> {
                // Already open, just update timestamp
            }
        }
    }

    private fun transitionToClosed() {
        state = CircuitBreakerState.CLOSED
        lastStateChange.set(System.currentTimeMillis())
        resetCounters()
    }

    private fun transitionToOpen() {
        state = CircuitBreakerState.OPEN
        lastStateChange.set(System.currentTimeMillis())
        halfOpenTestCount.set(0)
    }

    private fun transitionToHalfOpen() {
        state = CircuitBreakerState.HALF_OPEN
        lastStateChange.set(System.currentTimeMillis())
        halfOpenTestCount.set(0)
    }

    private fun resetCounters() {
        failureCount.set(0)
        requestCount.set(0)
    }

    private fun calculateFailureRate(): Double {
        cleanupOldRequests()

        val requests = recentRequests.toList()
        if (requests.isEmpty()) return 0.0

        val failures = requests.count { it.outcome == RequestOutcome.FAILURE }
        return failures.toDouble() / requests.size
    }

    private fun recordRequest(outcome: RequestOutcome, timestamp: Long) {
        recentRequests.offer(RequestRecord(outcome, timestamp))

        while (recentRequests.size > config.sampleSize * 2) {
            recentRequests.poll()
        }
    }

    private fun cleanupOldRequests() {
        val cutoffTime = System.currentTimeMillis() - windowSizeMs

        while (true) {
            val oldest = recentRequests.peek()
            if (oldest != null && oldest.timestamp < cutoffTime) {
                recentRequests.poll()
            } else {
                break
            }
        }
    }

    fun getState(): CircuitBreakerState = state
}

enum class CircuitBreakerState {
    CLOSED,    // Normal operation
    OPEN,      // Blocking all requests
    HALF_OPEN  // Testing with limited requests
}

private enum class RequestOutcome {
    SUCCESS, FAILURE
}

private data class RequestRecord(
    val outcome: RequestOutcome,
    val timestamp: Long
)

data class CircuitBreakerConfig(
    val failureThreshold: Int = 10,  // 失败率百分比阈值
    val timeoutMs: Long = 30_000,    // 熔断超时时间
    val sampleSize: Int = 20         // 采样窗口大小
)