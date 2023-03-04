package com.anymind.pos.service

import com.anymind.pos.constant.Point
import com.anymind.pos.dto.PaymentRequest
import com.anymind.pos.dto.PaymentResponse
import com.anymind.pos.entity.Sale
import com.anymind.pos.repository.SaleRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.format.DateTimeFormatter

@Service
class PosService(
    private val saleRepository: SaleRepository
) {
    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT

    fun makePayment(paymentRequest: PaymentRequest): PaymentResponse {
        val points = BigDecimal(paymentRequest.price).multiply(Point.getPointMultiplier(paymentRequest.paymentMethod)).setScale(0, RoundingMode.DOWN).toInt()
        val sale = Sale(
            datetime = Instant.parse(paymentRequest.datetime),
            sales = BigDecimal(paymentRequest.price).setScale(2, RoundingMode.DOWN).toPlainString(),
            points = points
        )
        saleRepository.save(sale)

        return PaymentResponse(
            finalPrice = BigDecimal(paymentRequest.price).multiply(paymentRequest.priceModifier).setScale(2, RoundingMode.DOWN).toPlainString(),
            points = points
        )
    }

    fun getSales(startDateTime: String, endDateTime: String): List<Sale> {
        return saleRepository.findAllByDatetimeBetween(
            Instant.from(dateTimeFormatter.parse(startDateTime)),
            Instant.from(dateTimeFormatter.parse(endDateTime))
        )
    }
}