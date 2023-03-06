package com.anymind.pos.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "sale")
data class Sale(
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = "",
    @Column(name = "datetime")
    val datetime: Instant,
    @Column(name = "sales")
    val sales: String,
    @Column(name = "points")
    val points: Int
) {
    constructor(): this("", Instant.now(), "", 0)
}