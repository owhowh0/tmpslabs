package com.tmps.lab2.domain

/**
 * Concrete Factory for standard consumer retail transactions.
 */
class RetailCheckoutFactory : CheckoutFactory {
    override fun createPricingStrategy(): PricingStrategy = StandardPricingStrategy()
    override fun createReceiptPrinter(): ReceiptPrinter = StandardReceiptPrinter()
}

/**
 * Concrete Factory for B2B wholesale transactions with bulk volume incentives.
 */
class WholesaleCheckoutFactory : CheckoutFactory {
    override fun createPricingStrategy(): PricingStrategy = BulkDiscountPricingStrategy()
    override fun createReceiptPrinter(): ReceiptPrinter = WholesaleReceiptPrinter()
}
