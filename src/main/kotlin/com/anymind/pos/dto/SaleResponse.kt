package com.anymind.pos.dto

import java.math.BigDecimal
import java.time.Instant

data class SaleResponse(
    val datetime: Instant,
    var sales: BigDecimal,
    var points: Int
)