package com.tmps.lab2

import com.tmps.lab2.patterns.decorator.AuditLoggingPaymentDecorator
import com.tmps.lab2.patterns.decorator.FraudDetectionPaymentDecorator
import com.tmps.lab2.patterns.decorator.PaymentProcessor
import com.tmps.lab2.patterns.decorator.PaymentResult
import com.tmps.lab2.patterns.decorator.StandardPaymentProcessor

class DecoratorPatternTest {

    private class CountingPaymentProcessor : PaymentProcessor {
        var callCount: Int = 0
        override fun processPayment(orderId: Long, customerName: String, amount: Double, currency: String): PaymentResult {
            callCount++
            return PaymentResult.Success(
                transactionId = "MOCK-TX-$orderId",
                amount = amount,
                currency = currency
            )
        }
    }

    fun testDecoratorsCanBeStackedInAnyOrderAndProduceIdenticalValidResults() {
        val counterA = CountingPaymentProcessor()
        val pipelineA: PaymentProcessor = AuditLoggingPaymentDecorator(
            wrapped = FraudDetectionPaymentDecorator(wrapped = counterA, maxTransactionLimit = 20_000.0),
            logSink = { /* silent sink */ }
        )

        val counterB = CountingPaymentProcessor()
        val pipelineB: PaymentProcessor = FraudDetectionPaymentDecorator(
            wrapped = AuditLoggingPaymentDecorator(wrapped = counterB, logSink = { /* silent sink */ }),
            maxTransactionLimit = 20_000.0
        )

        val resultA = pipelineA.processPayment(501L, "Customer Alpha", 1500.0, "MDL")
        val resultB = pipelineB.processPayment(501L, "Customer Beta", 1500.0, "MDL")

        check(resultA is PaymentResult.Success && resultB is PaymentResult.Success) {
            "Both decorator stacks must return PaymentResult.Success"
        }
        check(counterA.callCount == 1 && counterB.callCount == 1) {
            "Both pipelines must successfully delegate to base processor once"
        }
        check(resultA.amount == resultB.amount) {
            "Amounts must match across stack orders"
        }
    }

    fun testFraudDetectionDecoratorBlocksHighRiskTransactionsWithoutInvokingInner() {
        val counter = CountingPaymentProcessor()
        val securityCeiling = 10_000.0
        val fraudDecorator = FraudDetectionPaymentDecorator(
            wrapped = counter,
            maxTransactionLimit = securityCeiling
        )

        val result = fraudDecorator.processPayment(502L, "Wholesale Buyer", 15_000.0, "MDL")

        check(result is PaymentResult.Failure) { "Expected transaction to be rejected by fraud check" }
        check(result.errorCode == "FRAUD_LIMIT_EXCEEDED") { "Expected errorCode FRAUD_LIMIT_EXCEEDED, got ${result.errorCode}" }
        check(counter.callCount == 0) { "Wrapped payment processor MUST NOT be called when fraud is detected" }
    }

    fun testFraudDetectionDecoratorBlocksSuspiciousEntityNames() {
        val counter = CountingPaymentProcessor()
        val fraudDecorator = FraudDetectionPaymentDecorator(wrapped = counter)

        val result = fraudDecorator.processPayment(503L, "FRAUD_SUSPECT_ACCOUNT", 200.0, "MDL")

        check(result is PaymentResult.Failure) { "Expected transaction with blacklisted name to be rejected" }
        check(result.errorCode == "FRAUD_SUSPICIOUS_IDENTITY") { "Expected errorCode FRAUD_SUSPICIOUS_IDENTITY, got ${result.errorCode}" }
        check(counter.callCount == 0) { "Wrapped payment processor MUST NOT be called for blacklisted entity" }
    }

    fun testAuditLoggingDecoratorCapturesTelemetryLogs() {
        val logs = mutableListOf<String>()
        val base = StandardPaymentProcessor()
        val loggingDecorator = AuditLoggingPaymentDecorator(
            wrapped = base,
            logSink = { logs.add(it) }
        )

        val result = loggingDecorator.processPayment(504L, "Audit Test User", 800.0, "MDL")

        check(result is PaymentResult.Success) { "Expected standard processor to succeed" }
        check(logs.size == 2) { "Expected 2 log entries (start and outcome), got ${logs.size}" }
        check(logs[0].contains("Starting payment for Order #504")) { "First log should record initiation" }
        check(logs[1].contains("Success - TxRef=")) { "Second log should record success and latency" }
    }
}
