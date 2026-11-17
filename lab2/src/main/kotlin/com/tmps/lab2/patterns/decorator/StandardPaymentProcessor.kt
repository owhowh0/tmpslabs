package com.tmps.lab2.patterns.decorator

import java.util.UUID

/**
 * Concrete Component for the Decorator Pattern:
 * Standard internal settlement processor.
 */
class StandardPaymentProcessor : PaymentProcessor {
    override fun processPayment(
        orderId: Long,
        customerName: String,
        amount: Double,
        currency: String
    ): PaymentResult {
        val txId = "INTERNAL-TX-" + UUID.randomUUID().toString().take(8).uppercase()
        return PaymentResult.Success(
            transactionId = txId,
            amount = amount,
            currency = currency,
            details = "Internal payment gateway cleared successfully"
        )
    }
}
