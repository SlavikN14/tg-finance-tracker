package com.ajaxproject.financeservice.application.service

import com.ajaxproject.financeservice.application.port.input.CreateFinanceUseCase
import com.ajaxproject.financeservice.application.port.input.DeleteUserFinancesUseCase
import com.ajaxproject.financeservice.application.port.input.GetBalanceUseCase
import com.ajaxproject.financeservice.application.port.input.GetFinancesUseCase
import com.ajaxproject.financeservice.application.port.output.FinanceRepository
import com.ajaxproject.financeservice.domain.Finance
import com.ajaxproject.financeservice.domain.FinanceType
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
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
            .map { it.amount }
            .reduce(BigDecimal.ZERO, BigDecimal::add)
}
