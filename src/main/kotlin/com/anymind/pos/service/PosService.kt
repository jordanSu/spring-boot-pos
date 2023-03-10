package com.anymind.pos.service

import com.anymind.pos.constant.Point
import com.anymind.pos.dto.PaymentRequest
import com.anymind.pos.dto.PaymentResponse
import com.anymind.pos.dto.SaleResponse
import com.anymind.pos.entity.Sale
import com.anymind.pos.repository.SaleRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

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

    fun getSales(startDateTime: String, endDateTime: String): List<SaleResponse> {
        val resultMap = LinkedHashMap<Instant, SaleResponse>()
        val start = Instant.from(dateTimeFormatter.parse(startDateTime))
        val end = Instant.from(dateTimeFormatter.parse(endDateTime))
        val sales = saleRepository.findAllByDatetimeBetween(start, end)
        sales.forEach {
            val key = it.datetime.truncatedTo(ChronoUnit.HOURS)
            if (resultMap.containsKey(key)) {
                resultMap[key]!!.sales = resultMap[key]!!.sales.add(BigDecimal(it.sales))
                resultMap[key]!!.points += it.points
            } else {
                resultMap[key] = SaleResponse(
                    datetime = key,
                    sales = BigDecimal(it.sales),
                    points = it.points
                )
            }
        }
        return resultMap.values.toList()
    }
}