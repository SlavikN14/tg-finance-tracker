package com.ajaxproject.financeservice.infrastructure.adapter.output.persistence.mongo.entity

import com.ajaxproject.financeservice.domain.FinanceType
import com.ajaxproject.financeservice.infrastructure.adapter.output.persistence.mongo.entity.MongoFinance.Companion.COLLECTION_NAME
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.Instant

@TypeAlias("Finances")
@Document(value = COLLECTION_NAME)
data class MongoFinance(
    @Id val id: ObjectId? = null,
    val userId: Long,
    val financeType: FinanceType,
    val amount: BigDecimal,
    val description: String,
    val date: Instant,
) {
    companion object {
        const val COLLECTION_NAME = "finances"
    }
}
