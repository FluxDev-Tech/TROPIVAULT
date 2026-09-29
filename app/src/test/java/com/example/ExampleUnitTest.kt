package com.example

import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testOrderSubtotalCalculation() {
        val mangoPrice = 260.0
        val mangoQty = 2
        val pineapplePrice = 180.0
        val pineappleQty = 1
        val deliveryFee = 75.0

        val subtotal = (mangoPrice * mangoQty) + (pineapplePrice * pineappleQty)
        val grandTotal = subtotal + deliveryFee

        assertEquals(700.0, subtotal, 0.001)
        assertEquals(775.0, grandTotal, 0.001)
    }

    @Test
    fun testFreePickupDeliveryFee() {
        val subtotal = 500.0
        val isPickup = true
        val fee = if (isPickup) 0.0 else 75.0
        assertEquals(500.0, subtotal + fee, 0.001)
    }
}
