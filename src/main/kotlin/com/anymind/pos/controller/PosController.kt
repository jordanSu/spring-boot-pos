package com.anymind.pos.controller

import com.anymind.pos.dto.PaymentRequest
import com.anymind.pos.dto.PaymentResponse
import com.anymind.pos.entity.Sale
import com.anymind.pos.service.PosService
import com.anymind.pos.validator.PaymentValidator
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class PosController(
    private val posService: PosService
) {

    @QueryMapping
    fun getSales(@Argument startDateTime: String, @Argument endDateTime: String): List<Sale> {
        PaymentValidator.validateStartDateTimeAndEndDateTime(startDateTime, endDateTime)
        return posService.getSales(startDateTime, endDateTime)
    }

    @MutationMapping
    fun makePayment(@Argument paymentRequest: PaymentRequest): PaymentResponse {
        PaymentValidator.validatePaymentRequest(paymentRequest)
        return posService.makePayment(paymentRequest)
    }
}