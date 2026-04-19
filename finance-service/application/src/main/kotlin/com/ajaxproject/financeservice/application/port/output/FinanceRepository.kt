package com.ajaxproject.financeservice.application.port.output

import com.ajaxproject.financeservice.domain.Finance
import com.ajaxproject.financeservice.domain.FinanceType
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface FinanceRepository {

    fun findByUserIdAndFinanceType(userId: Long, financeType: FinanceType): Flux<Finance>

    fun save(finance: Finance): Mono<Finance>

    fun deleteByUserId(userId: Long): Mono<Void>
}
