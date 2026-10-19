data class OrderItem(val quantity: Int, val unitPrice: Double)

fun interface OrderReader {
    fun readOrder(): OrderItem
}

fun interface PricingStrategy {
    fun calculateTotal(item: OrderItem): Double
}

fun interface ReceiptPrinter {
    fun print(item: OrderItem, finalPrice: Double)
}