package com.ajaxproject.financeservice.infrastructure.adapter.output.persistence.mongo

import com.ajaxproject.financeservice.application.port.output.FinanceRepository
import com.ajaxproject.financeservice.domain.Finance
import com.ajaxproject.financeservice.domain.FinanceType
import com.ajaxproject.financeservice.infrastructure.adapter.output.persistence.mongo.entity.MongoFinance
import com.ajaxproject.financeservice.infrastructure.adapter.output.persistence.mongo.mapper.toDomain
import com.ajaxproject.financeservice.infrastructure.adapter.output.persistence.mongo.mapper.toMongo
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class MongoFinanceRepository(
    private val mongoTemplate: ReactiveMongoTemplate,
) : FinanceRepository {

    override fun findByUserIdAndFinanceType(userId: Long, financeType: FinanceType): Flux<Finance> {
        val query = Query(
            Criteria.where("userId").`is`(userId)
                .and("financeType").`is`(financeType)
        )
        return mongoTemplate.find(query, MongoFinance::class.java).map { it.toDomain() }
    }

    override fun save(finance: Finance): Mono<Finance> =
        mongoTemplate.save(finance.toMongo()).map { it.toDomain() }

    override fun deleteByUserId(userId: Long): Mono<Void> {
        val query = Query(Criteria.where("userId").`is`(userId))
        return mongoTemplate.remove(query, MongoFinance::class.java).then()
    }
}
