package com.tmps.lab2.patterns.facade

data class CheckoutItemRequest(
    val name: String,
    val quantity: Int,
    val unitPrice: Double
)

data class CheckoutRequest(
    val customerName: String,
    val items: List<CheckoutItemRequest>,
    val isWholesale: Boolean = false,
    val shippingAddress: String? = null,
    val isPriority: Boolean = false,
    val targetCurrency: String = "MDL"
)
