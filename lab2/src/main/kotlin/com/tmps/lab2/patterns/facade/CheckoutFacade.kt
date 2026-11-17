package com.tmps.lab2.patterns.facade

/**
 * Facade Pattern Interface:
 * Exposes a clean, high-level interface that completely hides the complexity
 * of order building, channel factory selection, pricing calculation, decorated payment execution,
 * and receipt formatting from client code.
 */
interface CheckoutFacade {
    fun checkout(request: CheckoutRequest): CheckoutResponse
}
