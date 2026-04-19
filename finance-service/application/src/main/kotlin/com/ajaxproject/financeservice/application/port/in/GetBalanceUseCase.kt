package com.ajaxproject.financeservice.application.port.`in`

import reactor.core.publisher.Mono
import java.math.BigDecimal

fun interface GetBalanceUseCase {
    fun getBalance(userId: Long): Mono<BigDecimal>
}
