package com.tmps.lab2.patterns.decorator

sealed class PaymentResult {
    data class Success(
        val transactionId: String,
        val amount: Double,
        val currency: String,
        val details: String = "Payment processed successfully"
    ) : PaymentResult()

    data class Failure(
        val errorCode: String,
        val reason: String
    ) : PaymentResult()
}

/**
 * Common abstraction for payment processing.
 * Serves as the Component interface for the Decorator pattern
 * and the Target interface for the Adapter pattern.
 */
interface PaymentProcessor {
    fun processPayment(
        orderId: Long,
        customerName: String,
        amount: Double,
        currency: String
    ): PaymentResult
}
