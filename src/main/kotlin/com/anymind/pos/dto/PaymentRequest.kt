package com.anymind.pos.dto

import com.anymind.pos.enums.PaymentMethod

data class PaymentRequest(
    val price: String,
    val priceModifier: Number,
    val paymentMethod: PaymentMethod,
    val datetime: String
)
