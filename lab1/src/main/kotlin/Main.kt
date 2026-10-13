fun main() {
    val reader = ConsoleOrderReader()
    val printer = ConsoleReceiptPrinter()

    val pricing = BulkDiscountPricingStrategy(threshold = 5, discountRate = 0.10)

    val checkout = OrderCheckoutService(reader, pricing, printer)
    checkout.processCheckout()
}