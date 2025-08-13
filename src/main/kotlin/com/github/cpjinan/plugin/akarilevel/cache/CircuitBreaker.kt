package com.github.cpjinan.plugin.akarilevel.cache

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.cache
 *
 * 熔断器接口。
 *
 * @author QwQ-dev
 * @since 2025/8/12 17:35
 */
interface CircuitBreaker {
    fun canExecute(): Boolean
    fun recordSuccess()
    fun recordFailure()
    fun getState(): CircuitBreakerState
}

/**
 * 熔断器状态。
 */
enum class CircuitBreakerState {
    CLOSED,    // 正常状态
    OPEN,      // 熔断状态
    HALF_OPEN  // 半开状态
}

/**
 * 熔断器配置。
 */
data class CircuitBreakerConfig(
    val failureThreshold: Int = 15,        // 失败率阈值(%)
    val timeoutMs: Long = 60_000,          // 熔断超时时间
    val sampleSize: Int = 30,              // 样本窗口大小
    val halfOpenMaxTests: Int = 5          // 半开状态最大测试数
)

/**
 * [CircuitBreaker] 接口的实现。
 */
class FastCircuitBreaker(private val config: CircuitBreakerConfig) : CircuitBreaker {
    @Volatile
    private var state = CircuitBreakerState.CLOSED

    private val failureCount = AtomicInteger(0)
    private val successCount = AtomicInteger(0)
    private val rejectCount = AtomicLong(0)
    private val lastFailureTime = AtomicLong(0)
    private val lastStateChange = AtomicLong(System.currentTimeMillis())
    private val halfOpenTests = AtomicInteger(0)

    override fun canExecute(): Boolean {
        return when (state) {
            CircuitBreakerState.CLOSED -> true
            CircuitBreakerState.OPEN -> {
                if (shouldAttemptReset()) {
                    transitionToHalfOpen()
                    true
                } else {
                    rejectCount.incrementAndGet()
                    false
                }
            }

            CircuitBreakerState.HALF_OPEN -> {
                if (halfOpenTests.get() < config.halfOpenMaxTests) {
                    halfOpenTests.incrementAndGet()
                    true
                } else {
                    rejectCount.incrementAndGet()
                    false
                }
            }
        }
    }

    override fun recordSuccess() {
        successCount.incrementAndGet()

        when (state) {
            CircuitBreakerState.HALF_OPEN -> {
                if (halfOpenTests.get() >= config.halfOpenMaxTests) {
                    transitionToClosed()
                }
            }

            CircuitBreakerState.CLOSED -> {
                if (shouldResetCounters()) {
                    resetCounters()
                }
            }

            else -> {}
        }
    }

    override fun recordFailure() {
        failureCount.incrementAndGet()
        lastFailureTime.set(System.currentTimeMillis())

        when (state) {
            CircuitBreakerState.CLOSED -> {
                if (shouldTrip()) {
                    transitionToOpen()
                }
            }

            CircuitBreakerState.HALF_OPEN -> {
                transitionToOpen()
            }

            else -> {}
        }
    }

    override fun getState(): CircuitBreakerState = state

    private fun shouldAttemptReset(): Boolean {
        val timeSinceFailure = System.currentTimeMillis() - lastFailureTime.get()
        return timeSinceFailure >= config.timeoutMs
    }

    private fun shouldTrip(): Boolean {
        val total = failureCount.get() + successCount.get()
        if (total < config.sampleSize) return false

        val failureRate = (failureCount.get() * 100) / total
        return failureRate >= config.failureThreshold
    }

    private fun shouldResetCounters(): Boolean {
        val timeSinceChange = System.currentTimeMillis() - lastStateChange.get()
        return timeSinceChange >= config.timeoutMs
    }

    private fun transitionToOpen() {
        state = CircuitBreakerState.OPEN
        lastStateChange.set(System.currentTimeMillis())
        halfOpenTests.set(0)
    }

    private fun transitionToHalfOpen() {
        state = CircuitBreakerState.HALF_OPEN
        lastStateChange.set(System.currentTimeMillis())
        halfOpenTests.set(0)
    }

    private fun transitionToClosed() {
        state = CircuitBreakerState.CLOSED
        lastStateChange.set(System.currentTimeMillis())
        resetCounters()
    }

    private fun resetCounters() {
        failureCount.set(0)
        successCount.set(0)
        halfOpenTests.set(0)
    }
}