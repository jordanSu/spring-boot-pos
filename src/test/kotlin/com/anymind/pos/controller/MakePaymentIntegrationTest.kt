package com.anymind.pos.controller

import com.anymind.pos.dto.PaymentRequest
import com.anymind.pos.enums.PaymentMethod
import com.anymind.pos.exception.PosBadRequestException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@SpringBootTest
class MakePaymentIntegrationTest {
    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    private lateinit var posController: PosController

    private val basePaymentRequest = PaymentRequest(
        datetime = "2023-03-05T10:00:00Z",
        priceModifier = BigDecimal.ONE,
        price = "100.00",
        paymentMethod = PaymentMethod.CASH
    )

    @AfterEach
    fun tearDown() {
        jdbcTemplate.execute("delete from sale")
    }

    @Nested
    inner class NormalCase {

        @Test
        fun payByCashTest() {
            val request = basePaymentRequest
            val result = posController.makePayment(request)
            assertEquals("100.00", result.finalPrice)
            assertEquals(5, result.points)

            val saleMap = jdbcTemplate.queryForMap("select * from sale")
            assertEquals(request.datetime, dateTimeFormatter.format(saleMap["datetime"] as OffsetDateTime))
            assertEquals(request.price, saleMap["sales"])
            assertEquals(5, saleMap["points"])
        }

        @Test
        fun payByCashOnDeliveryTest() {
            val request = basePaymentRequest.copy(paymentMethod = PaymentMethod.CASH_ON_DELIVERY, priceModifier = BigDecimal("1.02"))
            val result = posController.makePayment(request)
            assertEquals("102.00", result.finalPrice)
            assertEquals(5, result.points)

            val saleMap = jdbcTemplate.queryForMap("select * from sale")
            assertEquals(request.datetime, dateTimeFormatter.format(saleMap["datetime"] as OffsetDateTime))
            assertEquals(request.price, saleMap["sales"])
            assertEquals(5, saleMap["points"])
        }

        @Test
        fun payByVisaTest() {
            val request = basePaymentRequest.copy(paymentMethod = PaymentMethod.VISA, priceModifier = BigDecimal("0.95"))
            val result = posController.makePayment(request)
            assertEquals("95.00", result.finalPrice)
            assertEquals(3, result.points)

            val saleMap = jdbcTemplate.queryForMap("select * from sale")
            assertEquals(request.datetime, dateTimeFormatter.format(saleMap["datetime"] as OffsetDateTime))
            assertEquals(request.price, saleMap["sales"])
            assertEquals(3, saleMap["points"])
        }

        @Test
        fun payByMastercardTest() {
            val request = basePaymentRequest.copy(paymentMethod = PaymentMethod.MASTERCARD, priceModifier = BigDecimal("0.95"))
            val result = posController.makePayment(request)
            assertEquals("95.00", result.finalPrice)
            assertEquals(3, result.points)

            val saleMap = jdbcTemplate.queryForMap("select * from sale")
            assertEquals(request.datetime, dateTimeFormatter.format(saleMap["datetime"] as OffsetDateTime))
            assertEquals(request.price, saleMap["sales"])
            assertEquals(3, saleMap["points"])
        }

        @Test
        fun payByAmexTest() {
            val request = basePaymentRequest.copy(paymentMethod = PaymentMethod.AMEX, priceModifier = BigDecimal("1.01"))
            val result = posController.makePayment(request)
            assertEquals("101.00", result.finalPrice)
            assertEquals(2, result.points)

            val saleMap = jdbcTemplate.queryForMap("select * from sale")
            assertEquals(request.datetime, dateTimeFormatter.format(saleMap["datetime"] as OffsetDateTime))
            assertEquals(request.price, saleMap["sales"])
            assertEquals(2, saleMap["points"])
        }

        @Test
        fun payByJCBTest() {
            val request = basePaymentRequest.copy(paymentMethod = PaymentMethod.JCB, priceModifier = BigDecimal("0.95"))
            val result = posController.makePayment(request)
            assertEquals("95.00", result.finalPrice)
            assertEquals(5, result.points)

            val saleMap = jdbcTemplate.queryForMap("select * from sale")
            assertEquals(request.datetime, dateTimeFormatter.format(saleMap["datetime"] as OffsetDateTime))
            assertEquals(request.price, saleMap["sales"])
            assertEquals(5, saleMap["points"])
        }
    }

    @Nested
    inner class ErrorCase {
        @ParameterizedTest
        @CsvSource(value = [
            "CASH,0.8",
            "CASH_ON_DELIVERY,1.03",
            "VISA,0.9",
            "MASTERCARD,0.9",
            "AMEX,1.02",
            "JCB,1.01",
        ])
        fun paymentMethodAndPriceModifierCombinationInvalid(paymentMethod: PaymentMethod, priceModifier: BigDecimal) {
            val request = basePaymentRequest.copy(paymentMethod = paymentMethod, priceModifier = priceModifier)
            assertThrows<PosBadRequestException> { posController.makePayment(request) }
        }

        @Test
        fun priceFormatInvalid() {
            val request = basePaymentRequest.copy(price = "123.ab")
            assertThrows<PosBadRequestException> { posController.makePayment(request) }
        }

        @Test
        fun datetimeFormatInvalid() {
            val request = basePaymentRequest.copy(datetime = "2023-01-32T00:00:00Z")
            assertThrows<PosBadRequestException> { posController.makePayment(request) }
        }
    }


}