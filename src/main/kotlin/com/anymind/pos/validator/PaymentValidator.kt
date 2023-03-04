package com.anymind.pos.validator

import com.anymind.pos.dto.PaymentRequest
import com.anymind.pos.enums.PaymentMethod
import com.anymind.pos.exception.PosBadRequestException
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object PaymentValidator {
    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT

    fun validatePaymentRequest(paymentRequest: PaymentRequest) {
        validatePaymentRequestFields(paymentRequest)

        val priceModifier = paymentRequest.priceModifier
        val isValid = when (paymentRequest.paymentMethod) {
            PaymentMethod.CASH -> priceModifier >= BigDecimal("0.9") && priceModifier <= BigDecimal("1")
            PaymentMethod.CASH_ON_DELIVERY -> priceModifier >= BigDecimal("1") && priceModifier <= BigDecimal("1.02")
            PaymentMethod.VISA, PaymentMethod.MASTERCARD, PaymentMethod.JCB -> priceModifier >= BigDecimal("0.95") && priceModifier <= BigDecimal("1")
            PaymentMethod.AMEX -> priceModifier >= BigDecimal("0.98") && priceModifier <= BigDecimal("1")
        }
        if (!isValid)
            throw PosBadRequestException("priceModifier value invalid with given paymentMethod")
    }

    fun validateStartDateTimeAndEndDateTime(startDateTime: String, endDateTime: String) {
        val startInstant: Instant?
        val endInstant: Instant?
        try {
            startInstant = Instant.from(dateTimeFormatter.parse(startDateTime))
            endInstant = Instant.from(dateTimeFormatter.parse(endDateTime))
        } catch (ex: DateTimeParseException) {
            throw PosBadRequestException("startDateTime or endDateTime pattern invalid")
        }
        if (startInstant.isAfter(endInstant)) {
            throw PosBadRequestException("startDateTime is after endDateTime")
        }
    }

    private fun validatePaymentRequestFields(paymentRequest: PaymentRequest) {
        try {
            BigDecimal(paymentRequest.price)
        } catch (ex: NumberFormatException) {
            throw PosBadRequestException("price field is not a number")
        }
        try {
            Instant.from(dateTimeFormatter.parse(paymentRequest.datetime))
        } catch (ex: DateTimeParseException) {
            throw PosBadRequestException("datetime field pattern invalid")
        }
    }
}