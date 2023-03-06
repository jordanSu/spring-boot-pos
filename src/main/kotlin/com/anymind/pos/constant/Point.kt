package com.anymind.pos.constant

import com.anymind.pos.enums.PaymentMethod
import java.math.BigDecimal

object Point {
    private val paymentPointMap = mapOf(
        PaymentMethod.CASH to BigDecimal("0.05"),
        PaymentMethod.CASH_ON_DELIVERY to BigDecimal("0.05"),
        PaymentMethod.VISA to BigDecimal("0.03"),
        PaymentMethod.MASTERCARD to BigDecimal("0.03"),
        PaymentMethod.AMEX to BigDecimal("0.02"),
        PaymentMethod.JCB to BigDecimal("0.05")
    )

    fun getPointMultiplier(paymentMethod: PaymentMethod) = paymentPointMap[paymentMethod]
}