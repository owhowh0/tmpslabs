package com.tmps.lab1

/**
 * High-level checkout orchestrator.
 * Collaborates with [CheckoutFactory] (Abstract Factory) to produce cohesive
 * pricing and printing strategies for the given order.
 */
class OrderCheckoutService(
    private val factory: CheckoutFactory
) {
    fun processCheckout(order: Order): Double {
        val pricingStrategy = factory.createPricingStrategy()
        val printer = factory.createReceiptPrinter()

        val total = pricingStrategy.calculateTotal(order)
        printer.print(order, total)
        return total
    }
}
