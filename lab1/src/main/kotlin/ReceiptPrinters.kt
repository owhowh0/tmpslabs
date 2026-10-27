package com.tmps.lab1

/**
 * Standard consumer receipt printer.
 */
class StandardReceiptPrinter(
    private val currency: String = CheckoutConfig.defaultCurrency
) : ReceiptPrinter {
    override fun print(order: Order, finalPrice: Double) {
        println("RETAIL RECEIPT")
        println("Store: ${CheckoutConfig.storeName}")
        println("Order ID: #${order.id} | Customer: ${order.customerName}")
        println("Priority: ${if (order.isPriority) "YES" else "NO"}")
        println("Items:")
        for (item in order.items) {
            println("  ${item.name} x${item.quantity} @ ${"%.2f".format(item.unitPrice)} $currency = ${"%.2f".format(item.subtotal)} $currency")
        }
        println("Total (incl. tax): ${"%.2f".format(finalPrice)} $currency\n")
    }
}

/**
 * Wholesale B2B invoice printer with breakdown of volume discount and shipping destination.
 */
class WholesaleReceiptPrinter(
    private val currency: String = CheckoutConfig.defaultCurrency
) : ReceiptPrinter {
    override fun print(order: Order, finalPrice: Double) {
        val gross = order.rawSubtotal
        val qualifiesForDiscount = order.totalQuantity >= CheckoutConfig.bulkDiscountThreshold
        val discountAmount = if (qualifiesForDiscount) gross * CheckoutConfig.bulkDiscountRate else 0.0

        println("COMMERCIAL INVOICE (B2B)")
        println("Issuer: ${CheckoutConfig.storeName}")
        println("Invoice Ref: INV-${order.id}")
        println("Client: ${order.customerName}")
        println("Shipping Destination: ${order.shippingAddress ?: "Self-pickup"}")
        println("Total Units: ${order.totalQuantity}")
        println("Line Items:")
        for (item in order.items) {
            println("  [ITEM] ${item.name.padEnd(25)} Qty: ${item.quantity.toString().padEnd(4)} Unit: ${"%.2f".format(item.unitPrice)} $currency")
        }
        println("Accounting Summary:")
        println("  Gross Subtotal:     ${"%.2f".format(gross)} $currency")
        if (qualifiesForDiscount) {
            println("  Bulk Discount (${(CheckoutConfig.bulkDiscountRate * 100).toInt()}%): -${"%.2f".format(discountAmount)} $currency")
        }
        println("  Tax (VAT ${(CheckoutConfig.standardTaxRate * 100).toInt()}%):       +${"%.2f".format(finalPrice - (gross - discountAmount))} $currency")
        println("  Final Invoice Due:  ${"%.2f".format(finalPrice)} $currency\n")
    }
}
