package com.tmps.lab2.patterns.facade

import com.tmps.lab2.domain.Order
import com.tmps.lab2.patterns.decorator.PaymentResult

sealed class CheckoutResponse {
    data class Success(
        val order: Order,
        val originalTotal: Double,
        val originalCurrency: String,
        val chargedAmount: Double,
        val chargedCurrency: String,
        val exchangeRate: Double,
        val paymentResult: PaymentResult.Success,
        val receipt: String
    ) : CheckoutResponse()

    data class Failed(
        val order: Order?,
        val totalAmount: Double?,
        val currency: String?,
        val paymentResult: PaymentResult.Failure
    ) : CheckoutResponse()
}
