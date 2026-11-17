package com.tmps.lab2.domain

import java.util.concurrent.atomic.AtomicLong

/**
 * Singleton Pattern (Reused from Lab 1):
 * Thread-safe global configuration and sequential transaction identifier tracker.
 */
object CheckoutConfig {
    var storeName: String = "TechStore Chisinau"
    var defaultCurrency: String = "MDL"
    var standardTaxRate: Double = 0.20 // 20% VAT
    var bulkDiscountThreshold: Int = 10
    var bulkDiscountRate: Double = 0.15 // 15% discount for bulk orders

    private val sequenceCounter = AtomicLong(2000L)

    fun nextOrderId(): Long = sequenceCounter.incrementAndGet()

    fun resetSequence(initialValue: Long = 2000L) {
        sequenceCounter.set(initialValue)
    }
}
