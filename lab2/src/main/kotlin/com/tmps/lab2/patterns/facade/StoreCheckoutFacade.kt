package com.tmps.lab2.patterns.facade

import com.tmps.lab2.domain.CheckoutConfig
import com.tmps.lab2.domain.CheckoutFactory
import com.tmps.lab2.domain.OrderBuilder
import com.tmps.lab2.domain.RetailCheckoutFactory
import com.tmps.lab2.domain.WholesaleCheckoutFactory
import com.tmps.lab2.patterns.adapter.CurrencyConverter
import com.tmps.lab2.patterns.decorator.PaymentProcessor
import com.tmps.lab2.patterns.decorator.PaymentResult

/**
 * Facade Pattern Implementation:
 * Orchestrates multi-phase checkout transactions across independent subsystems:
 * 1. [OrderBuilder] (Creational Builder)
 * 2. [CheckoutFactory] (Creational Abstract Factory)
 * 3. [CurrencyConverter] (Structural Adapter for live/external FX conversion)
 * 4. [PaymentProcessor] (Structural Decorator pipeline for fraud checks and audit logs)
 * 5. [CheckoutConfig] (Creational Singleton)
 *
 * Avoids the God-Class anti-pattern by strictly delegating each task to its specialized subsystem.
 */
class StoreCheckoutFacade(
    private val paymentProcessor: PaymentProcessor,
    private val currencyConverter: CurrencyConverter,
    private val retailFactory: CheckoutFactory = RetailCheckoutFactory(),
    private val wholesaleFactory: CheckoutFactory = WholesaleCheckoutFactory()
) : CheckoutFacade {

    override fun checkout(request: CheckoutRequest): CheckoutResponse {
        // Phase 1: Construct domain Order via Builder Pattern
        val builder = OrderBuilder()
            .withCustomer(request.customerName)
            .withShippingAddress(request.shippingAddress)
            .asPriority(request.isPriority)

        for (item in request.items) {
            builder.addItem(item.name, item.quantity, item.unitPrice)
        }
        val order = builder.build()

        // Phase 2: Select business channel family via Abstract Factory Pattern
        val factory = if (request.isWholesale) wholesaleFactory else retailFactory
        val pricingStrategy = factory.createPricingStrategy()
        val baseTotal = pricingStrategy.calculateTotal(order)
        val baseCurrency = CheckoutConfig.defaultCurrency
        val targetCurrency = request.targetCurrency.uppercase().trim()

        // Phase 3: Currency conversion via Adapter Pattern
        val exchangeRate = if (targetCurrency == baseCurrency) 1.0 else currencyConverter.getExchangeRate(baseCurrency, targetCurrency)
        val finalAmountToCharge = if (targetCurrency == baseCurrency) baseTotal else currencyConverter.convert(baseTotal, baseCurrency, targetCurrency)

        // Phase 4: Execute payment through Decorator pipeline
        val paymentResult = paymentProcessor.processPayment(
            orderId = order.id,
            customerName = order.customerName,
            amount = finalAmountToCharge,
            currency = targetCurrency
        )

        // Phase 5: Handle completion or failure
        return when (paymentResult) {
            is PaymentResult.Success -> {
                val printer = factory.createReceiptPrinter()
                val receipt = printer.print(order, baseTotal)
                CheckoutResponse.Success(
                    order = order,
                    originalTotal = baseTotal,
                    originalCurrency = baseCurrency,
                    chargedAmount = finalAmountToCharge,
                    chargedCurrency = targetCurrency,
                    exchangeRate = exchangeRate,
                    paymentResult = paymentResult,
                    receipt = receipt
                )
            }
            is PaymentResult.Failure -> {
                CheckoutResponse.Failed(
                    order = order,
                    totalAmount = finalAmountToCharge,
                    currency = targetCurrency,
                    paymentResult = paymentResult
                )
            }
        }
    }
}
