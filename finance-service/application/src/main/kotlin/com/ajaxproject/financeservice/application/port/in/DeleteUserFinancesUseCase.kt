package com.ajaxproject.financeservice.application.port.`in`

import reactor.core.publisher.Mono

fun interface DeleteUserFinancesUseCase {
    fun deleteUserFinances(userId: Long): Mono<Void>
}
