package com.tmps.lab1

/**
 * Standard pricing: calculates raw subtotal + standard tax (VAT) from CheckoutConfig.
 */
class StandardPricingStrategy(
    private val taxRate: Double = CheckoutConfig.standardTaxRate
) : PricingStrategy {
    override fun calculateTotal(order: Order): Double {
        val subtotal = order.rawSubtotal
        return subtotal * (1.0 + taxRate)
    }
}

/**
 * Bulk discount pricing: applies percentage discount if total quantity exceeds threshold,
 * then adds tax.
 */
class BulkDiscountPricingStrategy(
    private val threshold: Int = CheckoutConfig.bulkDiscountThreshold,
    private val discountRate: Double = CheckoutConfig.bulkDiscountRate,
    private val taxRate: Double = CheckoutConfig.standardTaxRate
) : PricingStrategy {
    override fun calculateTotal(order: Order): Double {
        val baseTotal = order.rawSubtotal
        val discountedSubtotal = if (order.totalQuantity >= threshold) {
            baseTotal * (1.0 - discountRate)
        } else {
            baseTotal
        }
        return discountedSubtotal * (1.0 + taxRate)
    }
}
