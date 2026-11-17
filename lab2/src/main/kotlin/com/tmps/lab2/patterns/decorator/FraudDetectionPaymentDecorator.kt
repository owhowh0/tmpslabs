package com.tmps.lab2.patterns.decorator

/**
 * Concrete Decorator:
 * Enforces risk screening, transaction limits, and anti-fraud checks before delegating.
 * Prevents downstream processing for compromised or high-risk payments.
 */
class FraudDetectionPaymentDecorator(
    wrapped: PaymentProcessor,
    private val maxTransactionLimit: Double = 50_000.0,
    private val blacklistedKeywords: Set<String> = setOf("FRAUD", "BLOCKED", "SUSPICIOUS")
) : PaymentDecorator(wrapped) {

    override fun processPayment(
        orderId: Long,
        customerName: String,
        amount: Double,
        currency: String
    ): PaymentResult {
        // Guard 1: High-risk transaction limit
        if (amount > maxTransactionLimit) {
            return PaymentResult.Failure(
                errorCode = "FRAUD_LIMIT_EXCEEDED",
                reason = "Transaction amount ${"%.2f".format(amount)} $currency exceeds safety ceiling of ${"%.2f".format(maxTransactionLimit)} $currency"
            )
        }

        // Guard 2: AML / identity risk screening
        if (blacklistedKeywords.any { customerName.contains(it, ignoreCase = true) }) {
            return PaymentResult.Failure(
                errorCode = "FRAUD_SUSPICIOUS_IDENTITY",
                reason = "Risk alert: customer name '$customerName' triggered AML security screening"
            )
        }

        // Forward execution down the decorator chain
        return super.processPayment(orderId, customerName, amount, currency)
    }
}
