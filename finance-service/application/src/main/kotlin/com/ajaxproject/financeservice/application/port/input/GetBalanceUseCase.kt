package com.ajaxproject.financeservice.application.port.input

import reactor.core.publisher.Mono
import java.math.BigDecimal

fun interface GetBalanceUseCase {
    fun getBalance(userId: Long): Mono<BigDecimal>
}
