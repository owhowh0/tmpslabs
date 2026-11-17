package com.tmps.lab1

/**
 * Concrete Factory for standard consumer retail transactions.
 */
class RetailCheckoutFactory : CheckoutFactory {
    override fun createPricingStrategy(): PricingStrategy {
        return StandardPricingStrategy()
    }

    override fun createReceiptPrinter(): ReceiptPrinter {
        return StandardReceiptPrinter()
    }
}

/**
 * Concrete Factory for B2B wholesale transactions with bulk volume incentives.
 */
class WholesaleCheckoutFactory : CheckoutFactory {
    override fun createPricingStrategy(): PricingStrategy {
        return BulkDiscountPricingStrategy()
    }

    override fun createReceiptPrinter(): ReceiptPrinter {
        return WholesaleReceiptPrinter()
    }
}
