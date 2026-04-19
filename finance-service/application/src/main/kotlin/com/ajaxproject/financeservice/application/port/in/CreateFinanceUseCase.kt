package com.ajaxproject.financeservice.application.port.`in`

import com.ajaxproject.financeservice.domain.Finance
import reactor.core.publisher.Mono

fun interface CreateFinanceUseCase {
    fun createFinance(finance: Finance): Mono<Finance>
}
