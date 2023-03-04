package com.anymind.pos.controller

import com.anymind.pos.service.PosService
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller

@Controller
class PosController(
    posService: PosService
) {

    @MutationMapping
    fun makePayment(): Any {
        return Any()
    }
}