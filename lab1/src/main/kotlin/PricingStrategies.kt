class PricingStrategies: PricingStrategy {
    override fun calculateTotal(item: OrderItem): Double {
        return item.quantity * item.unitPrice
    }
}

class BulkDiscountPricingStrategy(private val threshold: Int = 10, private val discountRate: Double = 0.10) : PricingStrategy {
    override fun calculateTotal(item: OrderItem): Double {
        val baseTotal = item.quantity * item.unitPrice
        return if (item.quantity >= threshold) {
            baseTotal * (1.0 - discountRate)
        } else {
            baseTotal
        }
    }
}