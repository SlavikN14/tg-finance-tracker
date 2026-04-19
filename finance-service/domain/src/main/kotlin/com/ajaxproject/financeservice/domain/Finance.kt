package com.ajaxproject.financeservice.domain

import java.math.BigDecimal
import java.time.Instant

data class Finance(
    val id: String? = null,
    val userId: Long,
    val financeType: FinanceType,
    val amount: BigDecimal,
    val description: String,
    val date: Instant,
)
