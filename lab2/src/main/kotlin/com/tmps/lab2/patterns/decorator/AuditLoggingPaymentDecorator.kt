package com.tmps.lab2.patterns.decorator

/**
 * Concrete Decorator:
 * Intercepts payment operations to record audit logs, timing metrics, and transaction traces.
 */
class AuditLoggingPaymentDecorator(
    wrapped: PaymentProcessor,
    private val logSink: (String) -> Unit = { println(it) }
) : PaymentDecorator(wrapped) {

    override fun processPayment(
        orderId: Long,
        customerName: String,
        amount: Double,
        currency: String
    ): PaymentResult {
        logSink("Audit: Starting payment for Order #$orderId | Client: $customerName | Amount: ${"%.2f".format(amount)} $currency")
        val startTime = System.nanoTime()

        val result = super.processPayment(orderId, customerName, amount, currency)

        val elapsedMs = (System.nanoTime() - startTime) / 1_000_000.0
        when (result) {
            is PaymentResult.Success -> {
                logSink("Audit: Success - TxRef=${result.transactionId} settled in ${"%.2f".format(elapsedMs)} ms")
            }
            is PaymentResult.Failure -> {
                logSink("Audit: Failure - ErrorCode=${result.errorCode}: ${result.reason} (in ${"%.2f".format(elapsedMs)} ms)")
            }
        }

        return result
    }
}
