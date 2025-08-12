package com.github.cpjinan.plugin.akarilevel.database.lock

/**
 * AkariLevel
 * com.github.cpjinan.plugin.akarilevel.database.lock
 *
 * @author QwQ-dev
 * @since 2025/8/12 04:43
 */
interface DistributedLock {
    fun tryLock(lockKey: String, timeoutSeconds: Int = 5): Boolean

    fun unlock(lockKey: String): Boolean

    fun isLocked(lockKey: String): Boolean

    fun <T> withLock(
        lockKey: String,
        timeoutSeconds: Int = 5,
        action: () -> T
    ): LockResult<T>

    fun tryLockBatch(lockKeys: List<String>, timeoutSeconds: Int = 5): BatchLockResult {
        val results = mutableMapOf<String, Boolean>()
        val acquiredLocks = mutableListOf<String>()

        try {
            for (lockKey in lockKeys) {
                val acquired = tryLock(lockKey, timeoutSeconds)
                results[lockKey] = acquired

                if (acquired) {
                    acquiredLocks.add(lockKey)
                } else {
                    // Rollback already acquired locks
                    acquiredLocks.forEach { unlock(it) }
                    return BatchLockResult.PartialFailure(results, "Failed to acquire all locks")
                }
            }
            return BatchLockResult.Success(results)
        } catch (e: Exception) {
            // Cleanup on exception
            acquiredLocks.forEach {
                try {
                    unlock(it)
                } catch (cleanupEx: Exception) { /* ignore cleanup errors */
                }
            }
            return BatchLockResult.Failure(results, e.message ?: "Batch lock operation failed")
        }
    }

    fun unlockBatch(lockKeys: List<String>): Map<String, Boolean> {
        return lockKeys.associateWith { lockKey ->
            try {
                unlock(lockKey)
            } catch (_: Exception) {
                false
            }
        }
    }

    fun tryLockWithRetry(
        lockKey: String,
        timeoutSeconds: Int = 5,
        retryInterval: Long = 100,
        maxRetries: Int = 3
    ): Boolean {
        val startTime = System.currentTimeMillis()
        val totalTimeoutMs = timeoutSeconds * 1000L

        repeat(maxRetries + 1) { attempt ->
            if (System.currentTimeMillis() - startTime >= totalTimeoutMs) {
                return false
            }

            if (tryLock(lockKey, 1)) {
                return true
            }

            if (attempt < maxRetries) {
                try {
                    Thread.sleep(retryInterval * (attempt + 1))
                } catch (_: InterruptedException) {
                    Thread.currentThread().interrupt()
                    return false
                }
            }
        }
        return false
    }
}

sealed class LockResult<T> {
    data class Success<T>(val value: T) : LockResult<T>()

    data class LockFailed<T>(val reason: String) : LockResult<T>()

    data class ExecutionFailed<T>(val exception: Throwable) : LockResult<T>()
}

sealed class BatchLockResult {
    data class Success(val results: Map<String, Boolean>) : BatchLockResult()

    data class PartialFailure(val results: Map<String, Boolean>, val reason: String) : BatchLockResult()

    data class Failure(val results: Map<String, Boolean>, val reason: String) : BatchLockResult()

    fun isSuccessful(): Boolean = this is Success
    fun getSuccessCount(): Int = when (this) {
        is Success -> results.values.count { it }
        is PartialFailure -> results.values.count { it }
        is Failure -> results.values.count { it }
    }
}