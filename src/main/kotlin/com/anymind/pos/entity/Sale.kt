package com.anymind.pos.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "sale")
class Sale(
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    val id: String?=null,
    val datetime: Instant,
    val sales: String,
    val points: Int
) {
    constructor(): this("", Instant.now(), "", 0)
}