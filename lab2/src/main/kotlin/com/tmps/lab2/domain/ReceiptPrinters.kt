package com.tmps.lab2.domain

/**
 * Standard consumer receipt printer.
 */
class StandardReceiptPrinter(
    private val currency: String = CheckoutConfig.defaultCurrency
) : ReceiptPrinter {
    override fun print(order: Order, finalPrice: Double): String {
        val sb = StringBuilder()
        sb.appendLine("RETAIL RECEIPT")
        sb.appendLine("Store: ${CheckoutConfig.storeName}")
        sb.appendLine("Order ID: #${order.id}")
        sb.appendLine("Customer: ${order.customerName}")
        sb.appendLine("Priority: ${if (order.isPriority) "Yes" else "No"}")
        sb.appendLine("Items:")
        for (item in order.items) {
            sb.appendLine("  ${item.name} x${item.quantity} @ ${"%.2f".format(item.unitPrice)} $currency = ${"%.2f".format(item.subtotal)} $currency")
        }
        sb.appendLine("Total (incl. ${(CheckoutConfig.standardTaxRate * 100).toInt()}% VAT): ${"%.2f".format(finalPrice)} $currency")
        val text = sb.toString()
        print(text)
        return text
    }
}

/**
 * Wholesale B2B invoice printer with breakdown of volume discount and shipping destination.
 */
class WholesaleReceiptPrinter(
    private val currency: String = CheckoutConfig.defaultCurrency
) : ReceiptPrinter {
    override fun print(order: Order, finalPrice: Double): String {
        val gross = order.rawSubtotal
        val qualifiesForDiscount = order.totalQuantity >= CheckoutConfig.bulkDiscountThreshold
        val discountAmount = if (qualifiesForDiscount) gross * CheckoutConfig.bulkDiscountRate else 0.0

        val sb = StringBuilder()
        sb.appendLine("COMMERCIAL INVOICE (B2B)")
        sb.appendLine("Issuer: ${CheckoutConfig.storeName}")
        sb.appendLine("Invoice Ref: INV-${order.id}")
        sb.appendLine("Client: ${order.customerName}")
        sb.appendLine("Shipping Destination: ${order.shippingAddress ?: "Self-pickup"}")
        sb.appendLine("Total Units: ${order.totalQuantity}")
        sb.appendLine("Line Items:")
        for (item in order.items) {
            sb.appendLine("  [ITEM] ${item.name.padEnd(25)} Qty: ${item.quantity.toString().padEnd(4)} Unit: ${"%.2f".format(item.unitPrice)} $currency")
        }
        sb.appendLine("Accounting Summary:")
        sb.appendLine("  Gross Subtotal:     ${"%.2f".format(gross)} $currency")
        if (qualifiesForDiscount) {
            sb.appendLine("  Bulk Discount (${(CheckoutConfig.bulkDiscountRate * 100).toInt()}%): -${"%.2f".format(discountAmount)} $currency")
        }
        sb.appendLine("  Tax (VAT ${(CheckoutConfig.standardTaxRate * 100).toInt()}%):       +${"%.2f".format(finalPrice - (gross - discountAmount))} $currency")
        sb.appendLine("  Final Invoice Due:  ${"%.2f".format(finalPrice)} $currency")
        val text = sb.toString()
        print(text)
        return text
    }
}
