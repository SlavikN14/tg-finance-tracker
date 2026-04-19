package com.ajaxproject.financeservice.infrastructure.adapter.`in`.nats.mapper

import com.ajaxproject.financeservice.domain.Finance
import com.ajaxproject.financeservice.domain.FinanceType
import com.ajaxproject.internalapi.finance.commonmodels.FinanceMessage
import java.math.BigDecimal
import java.time.Instant
import com.ajaxproject.internalapi.finance.commonmodels.FinanceType as ProtoFinanceType

fun Finance.toProto(): FinanceMessage = FinanceMessage.newBuilder()
    .setUserId(userId)
    .setFinanceType(financeType.toProto())
    .setAmount(amount.toDouble())
    .setDescription(description)
    .build()

fun FinanceMessage.toDomain(): Finance = Finance(
    userId = userId,
    financeType = financeType.toDomain(),
    amount = BigDecimal.valueOf(amount),
    description = description,
    date = Instant.now(),
)

fun FinanceType.toProto(): ProtoFinanceType = when (this) {
    FinanceType.INCOME -> ProtoFinanceType.INCOME
    FinanceType.EXPENSE -> ProtoFinanceType.EXPENSE
}

fun ProtoFinanceType.toDomain(): FinanceType = when (this) {
    ProtoFinanceType.INCOME -> FinanceType.INCOME
    ProtoFinanceType.EXPENSE -> FinanceType.EXPENSE
    ProtoFinanceType.UNRECOGNIZED -> throw IllegalArgumentException("Unrecognized FinanceType: $this")
}
