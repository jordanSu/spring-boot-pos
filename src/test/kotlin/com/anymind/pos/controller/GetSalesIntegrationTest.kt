package com.anymind.pos.controller

import com.anymind.pos.dto.SaleResponse
import com.anymind.pos.util.DatabaseUtil
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import java.math.BigDecimal
import java.time.Instant
import java.time.format.DateTimeFormatter


@SpringBootTest
class GetSalesIntegrationTest {
    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    private lateinit var posController: PosController

    @BeforeEach
    fun setUp() {
        val statements = DatabaseUtil.getSqlFileStatements("sql/get-sales.sql")
        statements.forEach { jdbcTemplate.execute(it) }
    }

    @AfterEach
    fun tearDown() {
        jdbcTemplate.execute("delete from sale")
    }

    @Test
    fun fetchAllDataCase() {
        val sales = posController.getSales("2023-03-05T00:00:00Z", "2023-03-05T12:00:00Z")
        assertEquals(2, sales.count())

        // Assert sale with datetime: 2023-03-05T10:00:00Z
        assertEquals(SaleResponse(Instant.from(dateTimeFormatter.parse("2023-03-05T10:00:00Z")), BigDecimal("500.00"), 25), sales[0])

        // Assert sale with datetime: 2023-03-05T11:00:00Z
        assertEquals(SaleResponse(Instant.from(dateTimeFormatter.parse("2023-03-05T11:00:00Z")), BigDecimal("800.00"), 90), sales[1])
    }

    @Test
    fun partialDataFetchCase() {
        val sales = posController.getSales("2023-03-05T00:00:00Z", "2023-03-05T10:59:00Z")
        assertEquals(1, sales.count())

        // Assert sale with datetime: 2023-03-05T10:00:00Z
        assertEquals(SaleResponse(Instant.from(dateTimeFormatter.parse("2023-03-05T10:00:00Z")), BigDecimal("500.00"), 25), sales[0])
    }

    @Test
    fun emptyResponseCase() {
        val sales = posController.getSales("2023-03-05T00:00:00Z", "2023-03-05T09:59:00Z")
        assertEquals(0, sales.count())
    }
}