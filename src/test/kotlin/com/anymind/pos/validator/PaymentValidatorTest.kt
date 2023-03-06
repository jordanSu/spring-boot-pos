package com.anymind.pos.validator

import com.anymind.pos.dto.PaymentRequest
import com.anymind.pos.enums.PaymentMethod
import com.anymind.pos.exception.PosBadRequestException
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.math.BigDecimal

class PaymentValidatorTest {
    val paymentRequest = PaymentRequest("200.00", BigDecimal(0.95), PaymentMethod.CASH, "2023-03-05T13:00:00Z")

    @Nested
    inner class ValidatePaymentRequest {
        @Nested
        inner class ValidatePrice {
            @Test
            fun normalCase() {
                assertDoesNotThrow { PaymentValidator.validatePaymentRequest(paymentRequest) }
            }

            @Test
            fun invalidPriceCaseShouldReturnBadRequest() {
                val invalidPriceRequest = paymentRequest.copy(price = "2ac.01")
                assertThrows<PosBadRequestException> { PaymentValidator.validatePaymentRequest(invalidPriceRequest) }
            }
        }

        @Nested
        inner class ValidateDatetime {
            @Test
            fun normalCase() {
                assertDoesNotThrow { PaymentValidator.validatePaymentRequest(paymentRequest) }
            }

            @Test
            fun invalidDatetimeShouldReturnBadRequest() {
                val invalidDatetimeRequest = paymentRequest.copy(datetime = "2023-02-29T10:00:00Z")
                assertThrows<PosBadRequestException> { PaymentValidator.validatePaymentRequest(invalidDatetimeRequest) }
            }

            @Test
            fun unsupportedDatetimePatternShouldReturnBadRequest() {
                val unsupportedDatetimeRequest = paymentRequest.copy(datetime = "2022-09-03 00:00:00.000000")
                assertThrows<PosBadRequestException> { PaymentValidator.validatePaymentRequest(unsupportedDatetimeRequest) }
            }
        }

        @Nested
        inner class ValidatePriceModifier {
            @ParameterizedTest
            @CsvSource(value = [
                "CASH,0.9",
                "CASH_ON_DELIVERY,1.02",
                "VISA,0.95",
                "MASTERCARD,1",
                "AMEX,1.01",
                "JCB,0.95"
            ])
            fun normalCase(paymentMethod: String, priceModifier: String) {
                val normalRequest = paymentRequest.copy(
                    priceModifier = BigDecimal(priceModifier),
                    paymentMethod = PaymentMethod.valueOf(paymentMethod)
                )
                assertDoesNotThrow { PaymentValidator.validatePaymentRequest(normalRequest) }
            }

            @ParameterizedTest
            @CsvSource(value = [
                "CASH,1.05",
                "CASH_ON_DELIVERY,1.03",
                "VISA,0.93",
                "MASTERCARD,0.94",
                "AMEX,0.97",
                "JCB,0.94"
            ])
            fun invalidCaseShouldReturnBadRequest(paymentMethod: PaymentMethod, priceModifier: BigDecimal) {
                val invalidRequest = paymentRequest.copy(
                    priceModifier = priceModifier,
                    paymentMethod = paymentMethod
                )
                assertThrows<PosBadRequestException> { PaymentValidator.validatePaymentRequest(invalidRequest) }
            }
        }
    }


    @Nested
    inner class ValidateStartDateTimeAndEndDateTime {
        @Test
        fun normalCase() {
            assertDoesNotThrow {
                PaymentValidator.validateStartDateTimeAndEndDateTime("2023-03-05T06:00:00Z", "2023-03-05T07:00:00Z")
            }
        }

        @ParameterizedTest
        @CsvSource(value = [
            "2023-03-00T06:00:00Z,2023-03-05T06:00:00Z",
            "2023-03-05T00:00:00Z,2023-03-05T25:00:00Z",
            "2023-03-05T06:00:00.000000,2023-03-05T06:00:00.000100"
        ])
        fun invalidDatetimeShouldReturnBadRequest(startDateTime: String, endDateTime: String) {
            assertThrows<PosBadRequestException> { PaymentValidator.validateStartDateTimeAndEndDateTime(startDateTime, endDateTime) }
        }

        @ParameterizedTest
        @CsvSource(value = [
            "2023-03-05T06:00:00Z,2023-03-01T06:00:00Z",
            "2023-03-05T06:00:00Z,2023-02-26T06:00:00Z",
            "2023-03-05T08:00:00Z,2023-03-05T06:00:00Z"
        ])
        fun startDateTimeAfterEndDateTimeShouldReturnBadRequest(startDateTime: String, endDateTime: String) {
            assertThrows<PosBadRequestException> { PaymentValidator.validateStartDateTimeAndEndDateTime(startDateTime, endDateTime) }
        }
    }
}