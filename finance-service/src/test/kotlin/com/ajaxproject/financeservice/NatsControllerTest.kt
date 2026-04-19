package com.ajaxproject.financeservice

import com.ajaxproject.financeservice.domain.FinanceType
import com.ajaxproject.financeservice.infrastructure.adapter.input.nats.mapper.toProto
import com.ajaxproject.financeservice.infrastructure.adapter.output.persistence.mongo.MongoFinanceRepository
import com.ajaxproject.financeservice.infrastructure.adapter.output.persistence.mongo.entity.MongoFinance
import com.ajaxproject.financeservice.infrastructure.adapter.output.persistence.mongo.mapper.toDomain
import com.ajaxproject.internalapi.NatsSubject
import com.ajaxproject.internalapi.finance.input.reqreply.CreateFinanceRequest
import com.ajaxproject.internalapi.finance.input.reqreply.CreateFinanceResponse
import com.ajaxproject.internalapi.finance.input.reqreply.DeleteFinanceByIdRequest
import com.ajaxproject.internalapi.finance.input.reqreply.DeleteFinanceByIdResponse
import com.ajaxproject.internalapi.finance.input.reqreply.GetAllFinancesByIdRequest
import com.ajaxproject.internalapi.finance.input.reqreply.GetAllFinancesByIdResponse
import com.ajaxproject.internalapi.finance.input.reqreply.GetCurrentBalanceRequest
import com.ajaxproject.internalapi.finance.input.reqreply.GetCurrentBalanceResponse
import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import io.nats.client.Connection
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import com.ajaxproject.internalapi.finance.commonmodels.FinanceType as ProtoFinanceType
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant

@SpringBootTest
class NatsControllerTest {

    @Autowired
    private lateinit var natsConnection: Connection

    @Autowired
    private lateinit var mongoTemplate: ReactiveMongoTemplate

    @Autowired
    private lateinit var financeRepository: MongoFinanceRepository

    @AfterEach
    fun clean() {
        val query = Query.query(Criteria.where("userId").`is`(TEST_USER_ID))
        mongoTemplate.remove(query, MongoFinance::class.java).block()
    }

    private val testMongoIncomeFinance = MongoFinance(
        id = ObjectId(),
        userId = TEST_USER_ID,
        financeType = FinanceType.INCOME,
        amount = BigDecimal("100"),
        description = "Test Description",
        date = Instant.now(),
    )

    @Test
    fun `should return expected finance when get all finance by request`() {
        //GIVEN
        financeRepository.save(testMongoIncomeFinance.toDomain()).block()

        val request = GetAllFinancesByIdRequest.newBuilder()
            .setUserId(testMongoIncomeFinance.userId)
            .setFinanceType(ProtoFinanceType.INCOME)
            .build()

        val expectedResponse = GetAllFinancesByIdResponse.newBuilder().apply {
            successBuilder.addAllFinance(listOf(testMongoIncomeFinance.toDomain().toProto()))
        }.build()

        //WHEN
        val actualResponse = doRequest(
            NatsSubject.FinanceRequest.GET_ALL_FINANCES_BY_ID,
            request,
            GetAllFinancesByIdResponse.parser(),
        )

        //THEN
        assertThat(actualResponse).isEqualTo(expectedResponse)
    }

    @Test
    fun `should return expected message when delete finance`() {
        //GIVEN
        financeRepository.save(testMongoIncomeFinance.toDomain()).block()

        val request = DeleteFinanceByIdRequest.newBuilder()
            .setUserId(testMongoIncomeFinance.userId)
            .build()

        val expectedResponse = DeleteFinanceByIdResponse.newBuilder().apply {
            successBuilder.setMessage("Finance deleted successfully")
        }.build()

        //WHEN
        val actualResponse = doRequest(
            NatsSubject.FinanceRequest.DELETE_FINANCE,
            request,
            DeleteFinanceByIdResponse.parser(),
        )

        //THEN
        assertThat(actualResponse).isEqualTo(expectedResponse)
    }

    @Test
    fun `should return expected finance when save finance`() {
        //GIVEN
        val request = CreateFinanceRequest.newBuilder()
            .setFinance(testMongoIncomeFinance.toDomain().toProto())
            .build()

        val expectedResponse = CreateFinanceResponse.newBuilder().apply {
            successBuilder.setFinance(testMongoIncomeFinance.toDomain().toProto())
        }.build()

        //WHEN
        val actualResponse = doRequest(
            NatsSubject.FinanceRequest.CREATE_FINANCE,
            request,
            CreateFinanceResponse.parser(),
        )

        //THEN
        assertThat(actualResponse).isEqualTo(expectedResponse)
    }

    @Test
    fun `should return expected current balance when get current balance request`() {
        //GIVEN
        val testExpenseFinance = MongoFinance(
            id = ObjectId(),
            userId = TEST_USER_ID,
            financeType = FinanceType.EXPENSE,
            amount = BigDecimal("50"),
            description = "Test Description",
            date = Instant.now(),
        )
        financeRepository.save(testExpenseFinance.toDomain()).block()
        financeRepository.save(testMongoIncomeFinance.toDomain()).block()

        val request = GetCurrentBalanceRequest.newBuilder()
            .setUserId(testMongoIncomeFinance.userId)
            .build()

        val expectedResponse = GetCurrentBalanceResponse.newBuilder().apply {
            successBuilder.setBalance(50.0)
        }.build()

        //WHEN
        val actualResponse = doRequest(
            NatsSubject.FinanceRequest.GET_CURRENT_BALANCE,
            request,
            GetCurrentBalanceResponse.parser(),
        )

        //THEN
        assertThat(actualResponse).isEqualTo(expectedResponse)
    }

    private fun <RequestT : GeneratedMessageV3, ResponseT : GeneratedMessageV3> doRequest(
        subject: String,
        payload: RequestT,
        parser: Parser<ResponseT>,
    ): ResponseT {
        val response = natsConnection.requestWithTimeout(
            subject,
            payload.toByteArray(),
            Duration.ofSeconds(10L),
        )
        return parser.parseFrom(response.get().data)
    }

    companion object {
        private const val TEST_USER_ID = 4757839801L
    }
}
