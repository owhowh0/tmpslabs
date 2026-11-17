package com.tmps.lab2.domain

/**
 * Builder Pattern (Reused from Lab 1):
 * Fluent builder enforcing domain validation prior to [Order] instantiation.
 */
class OrderBuilder {
    private var id: Long = CheckoutConfig.nextOrderId()
    private var customerName: String = ""
    private val items: MutableList<OrderItem> = mutableListOf()
    private var shippingAddress: String? = null
    private var isPriority: Boolean = false

    fun withId(id: Long): OrderBuilder = apply {
        this.id = id
    }

    fun withCustomer(name: String): OrderBuilder = apply {
        this.customerName = name.trim()
    }

    fun addItem(name: String, quantity: Int, unitPrice: Double): OrderBuilder = apply {
        require(quantity > 0) { "Item quantity must be greater than zero: $quantity" }
        require(unitPrice >= 0.0) { "Item unit price cannot be negative: $unitPrice" }
        items.add(OrderItem(name.trim(), quantity, unitPrice))
    }

    fun withShippingAddress(address: String?): OrderBuilder = apply {
        this.shippingAddress = address?.trim()
    }

    fun asPriority(priority: Boolean = true): OrderBuilder = apply {
        this.isPriority = priority
    }

    fun build(): Order {
        require(customerName.isNotBlank()) { "Customer name is required to build an Order" }
        require(items.isNotEmpty()) { "Order must contain at least one OrderItem" }

        return Order(
            id = id,
            customerName = customerName,
            items = items.toList(),
            shippingAddress = shippingAddress,
            isPriority = isPriority
        )
    }
}
