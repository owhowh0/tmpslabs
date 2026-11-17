package com.tmps.lab2.patterns.adapter

/**
 * Target Interface for currency conversion.
 * Expected by the domain checkout system when customers pay in currencies
 * other than the store's primary currency (MDL).
 */
interface CurrencyConverter {
    fun convert(amount: Double, fromCurrency: String, toCurrency: String): Double
    fun getExchangeRate(fromCurrency: String, toCurrency: String): Double
}
