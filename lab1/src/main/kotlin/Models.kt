package com.tmps.lab1

data class OrderItem(
    val name: String,
    val quantity: Int,
    val unitPrice: Double
) {
    val subtotal: Double
        get() = quantity * unitPrice
}

data class Order(
    val id: Long,
    val customerName: String,
    val items: List<OrderItem>,
    val shippingAddress: String?,
    val isPriority: Boolean
) {
    val totalQuantity: Int
        get() = items.sumOf { it.quantity }

    val rawSubtotal: Double
        get() = items.sumOf { it.subtotal }
}
