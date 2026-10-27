class ConsoleOrderReader: OrderReader {
    override fun readOrder(): OrderItem {
        val quantity = readlnOrNull()!!.toInt()
        val unitPrice = readlnOrNull()!!.toDouble()
        return OrderItem(quantity, unitPrice)
    }
}

class ConsoleReceiptPrinter: ReceiptPrinter {
    override fun print(item: OrderItem, finalPrice: Double) {
        println("Quantity : ${item.quantity}, UnitPrice : ${item.unitPrice}")
        println("Total: $finalPrice")
    }
}

class OrderCheckoutService(private val orderReader: OrderReader, private val pricingStrategy: PricingStrategy, private val receiptPrinter: ReceiptPrinter) {
    fun processCheckout() {
        val order = orderReader.readOrder()
        val total = pricingStrategy.calculateTotal(order)
        receiptPrinter.print(order, total)
    }
}