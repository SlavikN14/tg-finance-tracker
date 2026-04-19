package com.ajaxproject.financeservice.infrastructure.adapter.output.persistence.mongo.mapper

import com.ajaxproject.financeservice.domain.Finance
import com.ajaxproject.financeservice.infrastructure.adapter.output.persistence.mongo.entity.MongoFinance
import org.bson.types.ObjectId

fun Finance.toMongo(): MongoFinance = MongoFinance(
    id = id?.let { ObjectId(it) },
    userId = userId,
    financeType = financeType,
    amount = amount,
    description = description,
    date = date,
)

fun MongoFinance.toDomain(): Finance = Finance(
    id = id?.toHexString(),
    userId = userId,
    financeType = financeType,
    amount = amount,
    description = description,
    date = date,
)
