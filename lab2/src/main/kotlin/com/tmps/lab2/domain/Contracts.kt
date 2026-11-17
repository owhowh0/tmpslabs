package com.tmps.lab2.domain

/**
 * Strategy interface for pricing calculation.
 */
fun interface PricingStrategy {
    fun calculateTotal(order: Order): Double
}

/**
 * Strategy interface for receipt/invoice printing.
 */
fun interface ReceiptPrinter {
    fun print(order: Order, finalPrice: Double): String
}

/**
 * Abstract Factory Pattern (Reused from Lab 1):
 * Provides coordinated families of pricing strategies and receipt formatters.
 */
interface CheckoutFactory {
    fun createPricingStrategy(): PricingStrategy
    fun createReceiptPrinter(): ReceiptPrinter
}
