package com.tmps.lab2

import kotlin.system.exitProcess

fun main() {
    println("TMPS Lab 2: Automated Unit Test Suite")
    println("Verifying Adapter, Decorator, and Facade Patterns")
    println()

    var totalTests = 0
    var passedTests = 0
    var failedTests = 0

    fun runTest(suiteName: String, testName: String, testFn: () -> Unit) {
        totalTests++
        val formattedTitle = "$suiteName: $testName"
        print("Running $formattedTitle ... ")
        try {
            val start = System.nanoTime()
            testFn()
            val elapsedMs = (System.nanoTime() - start) / 1_000_000.0
            println("PASSED (${"%.2f".format(elapsedMs)} ms)")
            passedTests++
        } catch (t: Throwable) {
            println("FAILED")
            println("  Error: ${t.message}")
            t.printStackTrace()
            failedTests++
        }
    }

    println("Adapter Pattern Tests:")
    val adapterSuite = AdapterPatternTest()
    runTest("AdapterPatternTest", "testAdapterReturnsUnitRateForSameCurrency") {
        adapterSuite.testAdapterReturnsUnitRateForSameCurrency()
    }
    runTest("AdapterPatternTest", "testAdapterConvertsCurrencyUsingExternalApiOrFallback") {
        adapterSuite.testAdapterConvertsCurrencyUsingExternalApiOrFallback()
    }
    runTest("AdapterPatternTest", "testAdapterFallsBackGracefullyOnInvalidEndpoint") {
        adapterSuite.testAdapterFallsBackGracefullyOnInvalidEndpoint()
    }
    runTest("AdapterPatternTest", "testAdapterRejectsUnsupportedCurrencyPair") {
        adapterSuite.testAdapterRejectsUnsupportedCurrencyPair()
    }
    println()

    println("Decorator Pattern Tests:")
    val decoratorSuite = DecoratorPatternTest()
    runTest("DecoratorPatternTest", "testDecoratorsCanBeStackedInAnyOrderAndProduceIdenticalValidResults") {
        decoratorSuite.testDecoratorsCanBeStackedInAnyOrderAndProduceIdenticalValidResults()
    }
    runTest("DecoratorPatternTest", "testFraudDetectionDecoratorBlocksHighRiskTransactionsWithoutInvokingInner") {
        decoratorSuite.testFraudDetectionDecoratorBlocksHighRiskTransactionsWithoutInvokingInner()
    }
    runTest("DecoratorPatternTest", "testFraudDetectionDecoratorBlocksSuspiciousEntityNames") {
        decoratorSuite.testFraudDetectionDecoratorBlocksSuspiciousEntityNames()
    }
    runTest("DecoratorPatternTest", "testAuditLoggingDecoratorCapturesTelemetryLogs") {
        decoratorSuite.testAuditLoggingDecoratorCapturesTelemetryLogs()
    }
    println()

    println("Facade Pattern Tests:")
    val facadeSuite = FacadePatternTest()
    runTest("FacadePatternTest", "testFacadeCoordinatesFullRetailCheckoutPipelineSuccessfully") {
        facadeSuite.testFacadeCoordinatesFullRetailCheckoutPipelineSuccessfully()
    }
    runTest("FacadePatternTest", "testFacadeConvertsCurrencyViaAdapterForForeignOrders") {
        facadeSuite.testFacadeConvertsCurrencyViaAdapterForForeignOrders()
    }
    runTest("FacadePatternTest", "testFacadeHandlesDownstreamPaymentRejectionGracefullyWithoutPrintingReceipt") {
        facadeSuite.testFacadeHandlesDownstreamPaymentRejectionGracefullyWithoutPrintingReceipt()
    }
    println()

    println("Test Summary: $totalTests run, $passedTests passed, $failedTests failed.")

    if (failedTests > 0) {
        exitProcess(1)
    }
}
