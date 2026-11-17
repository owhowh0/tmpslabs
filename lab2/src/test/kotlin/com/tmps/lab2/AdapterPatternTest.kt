package com.tmps.lab2

import com.tmps.lab2.patterns.adapter.HttpCurrencyExchangeAdapter
import java.net.http.HttpClient

class AdapterPatternTest {

    fun testAdapterReturnsUnitRateForSameCurrency() {
        val adapter = HttpCurrencyExchangeAdapter()
        val rate = adapter.getExchangeRate("MDL", "MDL")
        val converted = adapter.convert(250.0, "MDL", "MDL")

        check(rate == 1.0) { "Exchange rate for identical currency must be 1.0, got $rate" }
        check(converted == 250.0) { "Converted amount must match original amount, got $converted" }
    }

    fun testAdapterConvertsCurrencyUsingExternalApiOrFallback() {
        val adapter = HttpCurrencyExchangeAdapter()
        val rate = adapter.getExchangeRate("MDL", "EUR")
        val converted = adapter.convert(1000.0, "MDL", "EUR")

        check(rate > 0.0) { "Exchange rate must be positive, got $rate" }
        check(converted > 0.0 && converted < 1000.0) { "1000 MDL in EUR should be a positive fraction of 1000, got $converted" }
        // Verify math: converted == round(1000 * rate * 100) / 100
        val expected = kotlin.math.round(1000.0 * rate * 100.0) / 100.0
        check(kotlin.math.abs(converted - expected) < 0.01) { "Converted calculation mismatch: expected $expected, got $converted" }
    }

    fun testAdapterFallsBackGracefullyOnInvalidEndpoint() {
        // Points to an unreachable host to simulate offline or network partition
        val adapterWithBrokenUrl = HttpCurrencyExchangeAdapter(
            apiBaseUrl = "http://127.0.0.1:65530/unreachable"
        )

        // Must not crash; should fall back to internal cached rate
        val rate = adapterWithBrokenUrl.getExchangeRate("MDL", "USD")
        val converted = adapterWithBrokenUrl.convert(100.0, "MDL", "USD")

        check(rate > 0.0) { "Fallback rate must be valid, got $rate" }
        check(converted > 0.0) { "Converted value from fallback must be valid, got $converted" }
    }

    fun testAdapterRejectsUnsupportedCurrencyPair() {
        val adapter = HttpCurrencyExchangeAdapter()
        var caughtException = false
        try {
            adapter.getExchangeRate("MDL", "NON_EXISTING_XYZ")
        } catch (_: IllegalArgumentException) {
            caughtException = true
        }

        check(caughtException) { "Expected IllegalArgumentException for unsupported currency" }
    }
}
