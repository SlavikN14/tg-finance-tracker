package com.ajaxproject.financeservice.infrastructure.adapter.`in`.nats.controller

import com.ajaxproject.financeservice.application.port.`in`.CreateFinanceUseCase
import com.ajaxproject.financeservice.infrastructure.adapter.`in`.nats.NatsController
import com.ajaxproject.financeservice.infrastructure.adapter.`in`.nats.mapper.toDomain
import com.ajaxproject.financeservice.infrastructure.adapter.`in`.nats.mapper.toProto
import com.ajaxproject.internalapi.NatsSubject
import com.ajaxproject.internalapi.finance.commonmodels.FinanceMessage
import com.ajaxproject.internalapi.finance.input.reqreply.CreateFinanceRequest
import com.ajaxproject.internalapi.finance.input.reqreply.CreateFinanceResponse
import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class CreateFinanceNatsController(
    private val createFinance: CreateFinanceUseCase,
) : NatsController<CreateFinanceRequest, CreateFinanceResponse> {

    override val subject: String = NatsSubject.FinanceRequest.CREATE_FINANCE

    override val parser: Parser<CreateFinanceRequest> = CreateFinanceRequest.parser()

    override fun handle(request: CreateFinanceRequest): Mono<CreateFinanceResponse> =
        createFinance.createFinance(request.finance.toDomain())
            .map { buildSuccessResponse(it.toProto()) }
            .onErrorResume { buildFailureResponse(it.message ?: "Unknown error").toMono() }

    private fun buildSuccessResponse(finance: FinanceMessage): CreateFinanceResponse =
        CreateFinanceResponse.newBuilder().apply {
            successBuilder.setFinance(finance)
        }.build()

    private fun buildFailureResponse(message: String): CreateFinanceResponse =
        CreateFinanceResponse.newBuilder().apply {
            failureBuilder.setMessage("Create Finance failed: $message")
        }.build()
}
