package com.ajaxproject.financeservice.infrastructure.adapter.input.nats.controller

import com.ajaxproject.financeservice.application.port.input.DeleteUserFinancesUseCase
import com.ajaxproject.financeservice.infrastructure.adapter.input.nats.NatsController
import com.ajaxproject.internalapi.NatsSubject
import com.ajaxproject.internalapi.finance.input.reqreply.DeleteFinanceByIdRequest
import com.ajaxproject.internalapi.finance.input.reqreply.DeleteFinanceByIdResponse
import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class DeleteFinanceByIdNatsController(
    private val deleteUserFinances: DeleteUserFinancesUseCase,
) : NatsController<DeleteFinanceByIdRequest, DeleteFinanceByIdResponse> {

    override val subject: String = NatsSubject.FinanceRequest.DELETE_FINANCE

    override val parser: Parser<DeleteFinanceByIdRequest> = DeleteFinanceByIdRequest.parser()

    override fun handle(request: DeleteFinanceByIdRequest): Mono<DeleteFinanceByIdResponse> =
        deleteUserFinances.deleteUserFinances(request.userId)
            .then(Mono.fromCallable { buildSuccessResponse() })
            .onErrorResume { buildFailureResponse(it.message ?: "Unknown error").toMono() }

    private fun buildSuccessResponse(): DeleteFinanceByIdResponse =
        DeleteFinanceByIdResponse.newBuilder().apply {
            successBuilder.setMessage("Finance deleted successfully")
        }.build()

    private fun buildFailureResponse(message: String): DeleteFinanceByIdResponse =
        DeleteFinanceByIdResponse.newBuilder().apply {
            failureBuilder.setMessage("User deleteById failed: $message")
        }.build()
}
