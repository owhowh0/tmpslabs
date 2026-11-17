package com.tmps.lab2.patterns.adapter

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import kotlin.math.roundToLong

/**
 * Adapter Pattern:
 * Adapts an external, unchangeable HTTP client library ([HttpClient]) and a public
 * REST exchange rate API into our domain's [CurrencyConverter] target interface.
 *
 * Incompatibilities resolved:
 * 1. Protocol: Translates domain function calls into HTTP GET requests with timeouts.
 * 2. Data representation: Parses external raw JSON responses to extract numeric currency exchange ratios.
 * 3. Network resilience: Implements fallback local exchange rate matrix if network is unreachable or offline.
 */
class HttpCurrencyExchangeAdapter(
    private val httpClient: HttpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(3))
        .build(),
    private val apiBaseUrl: String = "https://open.er-api.com/v6/latest"
) : CurrencyConverter {

    // Offline fallback rates when external network is unavailable
    private val fallbackRates: Map<String, Map<String, Double>> = mapOf(
        "MDL" to mapOf("MDL" to 1.0, "EUR" to 0.051, "USD" to 0.055, "RON" to 0.25),
        "EUR" to mapOf("EUR" to 1.0, "MDL" to 19.60, "USD" to 1.08, "RON" to 4.97),
        "USD" to mapOf("USD" to 1.0, "MDL" to 18.15, "EUR" to 0.92, "RON" to 4.60)
    )

    override fun getExchangeRate(fromCurrency: String, toCurrency: String): Double {
        val base = fromCurrency.uppercase().trim()
        val target = toCurrency.uppercase().trim()

        if (base == target) return 1.0

        // Attempt live external HTTP request
        try {
            val request = HttpRequest.newBuilder()
                .uri(URI.create("$apiBaseUrl/$base"))
                .timeout(Duration.ofSeconds(3))
                .header("Accept", "application/json")
                .GET()
                .build()

            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() == 200) {
                val parsedRate = parseRateFromJson(response.body(), target)
                if (parsedRate != null && parsedRate > 0.0) {
                    return parsedRate
                }
            }
        } catch (_: Exception) {
            // Network failure or offline environment: seamlessly fallback to offline rates
        }

        return fallbackRates[base]?.get(target)
            ?: throw IllegalArgumentException("Unsupported currency conversion pair: $base to $target")
    }

    override fun convert(amount: Double, fromCurrency: String, toCurrency: String): Double {
        val rate = getExchangeRate(fromCurrency, toCurrency)
        val converted = amount * rate
        // Round to 2 decimal places for financial accuracy
        return (converted * 100.0).roundToLong() / 100.0
    }

    private fun parseRateFromJson(json: String, targetCurrency: String): Double? {
        val regex = Regex("\"$targetCurrency\"\\s*:\\s*([0-9.]+)")
        val match = regex.find(json)
        return match?.groupValues?.get(1)?.toDoubleOrNull()
    }
}
