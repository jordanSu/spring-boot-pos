package com.anymind.pos.dto

import com.anymind.pos.enums.PaymentMethod
import java.math.BigDecimal

data class PaymentRequest(
    val price: String,
    val priceModifier: BigDecimal,
    val paymentMethod: PaymentMethod,
    val datetime: String
)
