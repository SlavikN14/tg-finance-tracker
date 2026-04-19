package com.ajaxproject.financeservice.infrastructure.adapter.input.nats.controller

import com.ajaxproject.financeservice.application.port.input.GetBalanceUseCase
import com.ajaxproject.financeservice.infrastructure.adapter.input.nats.NatsController
import com.ajaxproject.internalapi.NatsSubject
import com.ajaxproject.internalapi.finance.input.reqreply.GetCurrentBalanceRequest
import com.ajaxproject.internalapi.finance.input.reqreply.GetCurrentBalanceResponse
import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.math.BigDecimal

@Component
class GetCurrentBalanceNatsController(
    private val getBalance: GetBalanceUseCase,
) : NatsController<GetCurrentBalanceRequest, GetCurrentBalanceResponse> {

    override val subject: String = NatsSubject.FinanceRequest.GET_CURRENT_BALANCE

    override val parser: Parser<GetCurrentBalanceRequest> = GetCurrentBalanceRequest.parser()

    override fun handle(request: GetCurrentBalanceRequest): Mono<GetCurrentBalanceResponse> =
        getBalance.getBalance(request.userId)
            .map { buildSuccessResponse(it) }
            .onErrorResume { buildFailureResponse(it.message ?: "Unknown error").toMono() }

    private fun buildSuccessResponse(balance: BigDecimal): GetCurrentBalanceResponse =
        GetCurrentBalanceResponse.newBuilder().apply {
            successBuilder.setBalance(balance.toDouble())
        }.build()

    private fun buildFailureResponse(message: String): GetCurrentBalanceResponse =
        GetCurrentBalanceResponse.newBuilder().apply {
            failureBuilder.setMessage("Finances find failed: $message")
        }.build()
}
