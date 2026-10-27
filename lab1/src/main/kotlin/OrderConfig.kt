package com.tmps.lab1

import java.util.concurrent.atomic.AtomicLong

/**
 * Singleton Pattern:
 * Thread-safe global configuration and sequence tracker for the checkout system.
 * Implemented using Kotlin's idiomatic `object` declaration.
 */
object CheckoutConfig {
    var storeName: String = "TechStore Chisinau"
    var defaultCurrency: String = "MDL"
    var standardTaxRate: Double = 0.20 // 20% VAT
    var bulkDiscountThreshold: Int = 10
    var bulkDiscountRate: Double = 0.15 // 15% discount for bulk

    private val sequenceCounter = AtomicLong(1000L)

    fun nextOrderId(): Long = sequenceCounter.incrementAndGet()
}
