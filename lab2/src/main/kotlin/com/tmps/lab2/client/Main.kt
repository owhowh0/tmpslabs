package com.tmps.lab2.client

import com.tmps.lab2.domain.CheckoutConfig
import com.tmps.lab2.patterns.adapter.CurrencyConverter
import com.tmps.lab2.patterns.adapter.HttpCurrencyExchangeAdapter
import com.tmps.lab2.patterns.decorator.AuditLoggingPaymentDecorator
import com.tmps.lab2.patterns.decorator.FraudDetectionPaymentDecorator
import com.tmps.lab2.patterns.decorator.PaymentProcessor
import com.tmps.lab2.patterns.decorator.StandardPaymentProcessor
import com.tmps.lab2.patterns.facade.CheckoutFacade
import com.tmps.lab2.patterns.facade.CheckoutItemRequest
import com.tmps.lab2.patterns.facade.CheckoutRequest
import com.tmps.lab2.patterns.facade.CheckoutResponse
import com.tmps.lab2.patterns.facade.StoreCheckoutFacade

fun main() {
    println("TMPS Lab 2: Structural Design Patterns Demonstration (Kotlin)")
    println("Patterns: Adapter (External Currency API), Decorator (Fraud Screening + Audit Logs), Facade (Store Checkout)")
    println()

    // 1. Adapter: Adapts external java.net.http.HttpClient and public exchange API to domain CurrencyConverter
    val currencyAdapter: CurrencyConverter = HttpCurrencyExchangeAdapter()

    // 2. Component & Decorator Pipeline: Base processor wrapped with Audit Logging and Fraud Detection
    val baseProcessor: PaymentProcessor = StandardPaymentProcessor()
    val decoratedPaymentPipeline: PaymentProcessor = FraudDetectionPaymentDecorator(
        wrapped = AuditLoggingPaymentDecorator(
            wrapped = baseProcessor,
            logSink = { log -> println("  $log") }
        ),
        maxTransactionLimit = 5_000.0 // Limit in charged currency
    )

    // 3. Facade: Simplifies client interactions across Builder, Factory, Adapter, and Decorator subsystems
    val checkoutFacade: CheckoutFacade = StoreCheckoutFacade(
        paymentProcessor = decoratedPaymentPipeline,
        currencyConverter = currencyAdapter
    )

    println("Architecture Pipeline:")
    println("  Client -> StoreCheckoutFacade (Facade)")
    println("         -> HttpCurrencyExchangeAdapter (Adapter: calls external API via HttpClient)")
    println("         -> FraudDetectionPaymentDecorator (Decorator: Risk & AML)")
    println("         -> AuditLoggingPaymentDecorator (Decorator: Telemetry & Logs)")
    println("         -> StandardPaymentProcessor (Component: Settlement)")
    println()

    println("Scenario 1: Retail Checkout in Base Currency (MDL)")
    val retailRequest = CheckoutRequest(
        customerName = "Ion Creanga",
        items = listOf(
            CheckoutItemRequest("Ergonomic Keyboard", quantity = 1, unitPrice = 1200.0),
            CheckoutItemRequest("Wireless Gaming Mouse", quantity = 1, unitPrice = 650.0)
        ),
        isWholesale = false,
        shippingAddress = "Str. Stefan cel Mare 168, Chisinau",
        isPriority = true,
        targetCurrency = "MDL"
    )
    handleResponse(checkoutFacade.checkout(retailRequest))

    println("Scenario 2: Retail Checkout with Currency Conversion (EUR)")
    val eurRequest = CheckoutRequest(
        customerName = "Maria Cibotari",
        items = listOf(
            CheckoutItemRequest("Bluetooth Headset", quantity = 1, unitPrice = 1800.0)
        ),
        isWholesale = false,
        targetCurrency = "EUR"
    )
    handleResponse(checkoutFacade.checkout(eurRequest))

    println("Scenario 3: Wholesale B2B Bulk Checkout with Currency Conversion (USD)")
    val wholesaleRequest = CheckoutRequest(
        customerName = "Moldova AgroTech SRL",
        items = listOf(
            CheckoutItemRequest("Industrial Sensor Node", quantity = 15, unitPrice = 450.0),
            CheckoutItemRequest("Mounting Brackets Pack", quantity = 15, unitPrice = 50.0)
        ),
        isWholesale = true,
        shippingAddress = "Parcul Industrial Tracom, Chisinau",
        targetCurrency = "USD"
    )
    handleResponse(checkoutFacade.checkout(wholesaleRequest))

    println("Scenario 4: Security Interception via Decorator (Ceiling Exceeded)")
    val fraudRiskRequest = CheckoutRequest(
        customerName = "Enterprise Corp",
        items = listOf(
            CheckoutItemRequest("High-End Server Rack", quantity = 5, unitPrice = 30_000.0)
        ),
        isWholesale = true,
        targetCurrency = "USD"
    )
    handleResponse(checkoutFacade.checkout(fraudRiskRequest))

    println("Demonstration finished successfully.")
}

private fun handleResponse(response: CheckoutResponse) {
    when (response) {
        is CheckoutResponse.Success -> {
            println("Checkout Status: Completed")
            println("Order Reference: #${response.order.id}")
            println("Base Amount:     ${"%.2f".format(response.originalTotal)} ${response.originalCurrency}")
            if (response.originalCurrency != response.chargedCurrency) {
                println("Exchange Rate:   1 ${response.originalCurrency} = ${"%.4f".format(response.exchangeRate)} ${response.chargedCurrency}")
                println("Charged Amount:  ${"%.2f".format(response.chargedAmount)} ${response.chargedCurrency}")
            }
            println("Payment Tx Ref:  ${response.paymentResult.transactionId}")
            println()
        }
        is CheckoutResponse.Failed -> {
            println("Checkout Status: Aborted / Rejected")
            println("Order Reference: #${response.order?.id ?: "N/A"}")
            println("Error Code:      ${response.paymentResult.errorCode}")
            println("Reason:          ${response.paymentResult.reason}")
            println()
        }
    }
}
