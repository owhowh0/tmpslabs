package com.tmps.lab1

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
    fun print(order: Order, finalPrice: Double)
}

/**
 * Abstract Factory Pattern:
 * Declares factory methods for creating families of related checkout components
 * (pricing strategies and receipt printers) without coupling clients to concrete classes.
 */
interface CheckoutFactory {
    fun createPricingStrategy(): PricingStrategy
    fun createReceiptPrinter(): ReceiptPrinter
}
