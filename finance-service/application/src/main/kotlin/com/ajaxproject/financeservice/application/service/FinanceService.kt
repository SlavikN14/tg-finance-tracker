package com.ajaxproject.financeservice.application.service

import com.ajaxproject.financeservice.application.port.`in`.CreateFinanceUseCase
import com.ajaxproject.financeservice.application.port.`in`.DeleteUserFinancesUseCase
import com.ajaxproject.financeservice.application.port.`in`.GetBalanceUseCase
import com.ajaxproject.financeservice.application.port.`in`.GetFinancesUseCase
import com.ajaxproject.financeservice.application.port.out.FinanceRepository
import com.ajaxproject.financeservice.domain.Finance
import com.ajaxproject.financeservice.domain.FinanceType
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal

@Service
class FinanceService(
    private val financeRepository: FinanceRepository,
) : CreateFinanceUseCase,
    GetFinancesUseCase,
    GetBalanceUseCase,
    DeleteUserFinancesUseCase {

    override fun createFinance(finance: Finance): Mono<Finance> =
        financeRepository.save(finance)

    override fun getFinances(userId: Long, financeType: FinanceType): Flux<Finance> =
        financeRepository.findByUserIdAndFinanceType(userId, financeType)

    override fun deleteUserFinances(userId: Long): Mono<Void> =
        financeRepository.deleteByUserId(userId)

    override fun getBalance(userId: Long): Mono<BigDecimal> =
        Mono.zip(sumByType(userId, FinanceType.INCOME), sumByType(userId, FinanceType.EXPENSE))
            .map { (incomes, expenses) -> incomes.subtract(expenses) }

    private fun sumByType(userId: Long, type: FinanceType): Mono<BigDecimal> =
        financeRepository.findByUserIdAndFinanceType(userId, type)
            .reduce(BigDecimal.ZERO) { acc, finance -> acc.add(finance.amount) }
}
