package com.ajaxproject.financeservice.infrastructure.adapter.`in`.nats

import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import reactor.core.publisher.Mono

interface NatsController<ReqT : GeneratedMessageV3, RespT : GeneratedMessageV3> {

    val subject: String

    val parser: Parser<ReqT>

    fun handle(request: ReqT): Mono<RespT>
}
