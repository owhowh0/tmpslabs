package com.tmps.lab2.patterns.decorator

/**
 * Decorator Pattern (Base Decorator):
 * Conforms to the [PaymentProcessor] interface and maintains a reference to a wrapped component.
 * Concrete decorators inherit from this class to add runtime capabilities while preserving delegation.
 */
abstract class PaymentDecorator(
    protected val wrapped: PaymentProcessor
) : PaymentProcessor {

    override fun processPayment(
        orderId: Long,
        customerName: String,
        amount: Double,
        currency: String
    ): PaymentResult {
        return wrapped.processPayment(orderId, customerName, amount, currency)
    }
}
