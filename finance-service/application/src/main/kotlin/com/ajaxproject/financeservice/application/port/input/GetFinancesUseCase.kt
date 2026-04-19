package com.ajaxproject.financeservice.application.port.input

import com.ajaxproject.financeservice.domain.Finance
import com.ajaxproject.financeservice.domain.FinanceType
import reactor.core.publisher.Flux

fun interface GetFinancesUseCase {
    fun getFinances(userId: Long, financeType: FinanceType): Flux<Finance>
}
