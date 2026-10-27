package com.tmps.lab1

fun main() {
    println("TMPS Lab 1: Creational Design Patterns Demonstration\n")

    // 1. Singleton Pattern Demonstration
    println("[1] Singleton Pattern: CheckoutConfig")
    println("Store Name: ${CheckoutConfig.storeName}")
    println("Active Currency: ${CheckoutConfig.defaultCurrency}")
    println("Default VAT Rate: ${CheckoutConfig.standardTaxRate * 100}%")
    println("Next Order ID generated: ${CheckoutConfig.nextOrderId()}\n")

    // 2. Interactive Order Creation via Builder Pattern
    println("[2] Builder Pattern: Constructing Order")
    print("Enter customer name (press Enter for default 'Ion Creangă'): ")
    val customerInput = readlnOrNull()?.trim()
    val customerName = if (customerInput.isNullOrBlank()) "Ion Creangă" else customerInput

    print("Is this a priority order? (y/N): ")
    val priorityInput = readlnOrNull()?.trim()?.lowercase()
    val isPriority = priorityInput == "y" || priorityInput == "yes"

    print("Shipping destination (press Enter for 'Self-pickup'): ")
    val shippingInput = readlnOrNull()?.trim()
    val shippingAddress = if (shippingInput.isNullOrBlank()) null else shippingInput

    val orderBuilder = OrderBuilder()
        .withCustomer(customerName)
        .asPriority(isPriority)
        .withShippingAddress(shippingAddress)

    println("Enter order items (press Enter on item name to finish):")
    var itemCount = 0
    while (true) {
        print("  Item ${itemCount + 1} name: ")
        val itemName = readlnOrNull()?.trim() ?: ""
        if (itemName.isEmpty()) {
            if (itemCount > 0) break
            println("  Adding default item: 'Wireless Ergonomic Mouse' x1 @ 350.00")
            orderBuilder.addItem("Wireless Ergonomic Mouse", quantity = 1, unitPrice = 350.0)
            itemCount++
            break
        }

        print("  Quantity (default 1): ")
        val qty = readlnOrNull()?.trim()?.toIntOrNull()?.takeIf { it > 0 } ?: 1

        print("  Unit price (default 100.0): ")
        val price = readlnOrNull()?.trim()?.toDoubleOrNull()?.takeIf { it >= 0.0 } ?: 100.0

        orderBuilder.addItem(itemName, quantity = qty, unitPrice = price)
        itemCount++
    }

    val order = orderBuilder.build()
    println("\nOrder #${order.id} constructed successfully for ${order.customerName}.\n")

    // 3. Abstract Factory Pattern Demonstration
    println("[3] Abstract Factory Pattern: Checkout Processing")
    print("Select checkout channel [1: Retail, 2: Wholesale B2B] (default 1): ")
    val channelChoice = readlnOrNull()?.trim() ?: "1"

    val factory: CheckoutFactory = if (channelChoice == "2") {
        WholesaleCheckoutFactory()
    } else {
        RetailCheckoutFactory()
    }

    val checkoutService = OrderCheckoutService(factory)
    val total = checkoutService.processCheckout(order)

    println("Execution completed. Total charged: ${"%.2f".format(total)} ${CheckoutConfig.defaultCurrency}")
}
