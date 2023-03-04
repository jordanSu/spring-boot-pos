package com.anymind.pos.repository

import com.anymind.pos.entity.Sale
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface SaleRepository : JpaRepository<Sale, Int> {

    fun findAllByDatetimeBetween(startDateTime: Instant, endDateTime: Instant): List<Sale>
}