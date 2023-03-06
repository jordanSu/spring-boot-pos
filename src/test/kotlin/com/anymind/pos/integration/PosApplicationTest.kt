package com.anymind.pos.integration

import com.anymind.pos.dto.SaleResponse
import com.anymind.pos.util.DatabaseUtil
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.graphql.test.tester.HttpGraphQlTester
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.web.reactive.server.WebTestClient
import java.math.BigDecimal
import java.time.Instant
import java.time.format.DateTimeFormatter

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class PosApplicationTest {

    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT

    private lateinit var graphQlTester: HttpGraphQlTester

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @BeforeEach
    fun setUp() {
        val url = "http://localhost:8080/graphql"

        graphQlTester = HttpGraphQlTester.create(WebTestClient.bindToServer().baseUrl(url).build())
        val statements = DatabaseUtil.getSqlFileStatements("sql/get-sales.sql")
        statements.forEach { jdbcTemplate.execute(it) }
    }

    @AfterEach
    fun tearDown() {
        jdbcTemplate.execute("delete from sale")
    }

    @Test
    fun fetchAllDataCase() {
        graphQlTester.documentName("sales")
            .variable("startDateTime", "2023-03-05T00:00:00Z")
            .variable("endDateTime", "2023-03-05T12:00:00Z")
            .execute()
            .path("sales")
            .entityList(SaleResponse::class.java)
            .hasSize(2)
            .contains(SaleResponse(Instant.from(dateTimeFormatter.parse("2023-03-05T10:00:00Z")), BigDecimal("500.00"), 25))
            .contains(SaleResponse(Instant.from(dateTimeFormatter.parse("2023-03-05T11:00:00Z")), BigDecimal("800.00"), 90))
    }

    @Test
    fun fetchPartialDataCase() {
        graphQlTester.documentName("sales")
            .variable("startDateTime", "2023-03-05T00:00:00Z")
            .variable("endDateTime", "2023-03-05T10:59:00Z")
            .execute()
            .path("sales")
            .entityList(SaleResponse::class.java)
            .hasSize(1)
            .contains(SaleResponse(Instant.from(dateTimeFormatter.parse("2023-03-05T10:00:00Z")), BigDecimal("500.00"), 25))
    }

    @Test
    fun fetchNoDataCase() {
        graphQlTester.documentName("sales")
            .variable("startDateTime", "2023-03-05T00:00:00Z")
            .variable("endDateTime", "2023-03-05T09:00:00Z")
            .execute()
            .path("sales")
            .entityList(SaleResponse::class.java)
            .hasSize(0)
    }

}
