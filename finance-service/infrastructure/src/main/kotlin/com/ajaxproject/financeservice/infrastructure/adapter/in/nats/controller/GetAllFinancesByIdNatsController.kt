package com.ajaxproject.financeservice.infrastructure.adapter.`in`.nats.controller

import com.ajaxproject.financeservice.application.port.`in`.GetFinancesUseCase
import com.ajaxproject.financeservice.infrastructure.adapter.`in`.nats.NatsController
import com.ajaxproject.financeservice.infrastructure.adapter.`in`.nats.mapper.toDomain
import com.ajaxproject.financeservice.infrastructure.adapter.`in`.nats.mapper.toProto
import com.ajaxproject.internalapi.NatsSubject
import com.ajaxproject.internalapi.finance.commonmodels.FinanceMessage
import com.ajaxproject.internalapi.finance.input.reqreply.GetAllFinancesByIdRequest
import com.ajaxproject.internalapi.finance.input.reqreply.GetAllFinancesByIdResponse
import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class GetAllFinancesByIdNatsController(
    private val getFinances: GetFinancesUseCase,
) : NatsController<GetAllFinancesByIdRequest, GetAllFinancesByIdResponse> {

    override val subject: String = NatsSubject.FinanceRequest.GET_ALL_FINANCES_BY_ID

    override val parser: Parser<GetAllFinancesByIdRequest> = GetAllFinancesByIdRequest.parser()

    override fun handle(request: GetAllFinancesByIdRequest): Mono<GetAllFinancesByIdResponse> =
        getFinances.getFinances(request.userId, request.financeType.toDomain())
            .map { it.toProto() }
            .collectList()
            .map { buildSuccessResponse(it) }
            .onErrorResume { buildFailureResponse(it.message ?: "Unknown error").toMono() }

    private fun buildSuccessResponse(financeList: List<FinanceMessage>): GetAllFinancesByIdResponse =
        GetAllFinancesByIdResponse.newBuilder().apply {
            successBuilder.addAllFinance(financeList)
        }.build()

    private fun buildFailureResponse(message: String): GetAllFinancesByIdResponse =
        GetAllFinancesByIdResponse.newBuilder().apply {
            failureBuilder.setMessage("Finances find failed: $message")
        }.build()
}
