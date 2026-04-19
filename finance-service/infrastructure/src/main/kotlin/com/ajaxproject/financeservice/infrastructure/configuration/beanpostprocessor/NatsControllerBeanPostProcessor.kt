package com.ajaxproject.financeservice.infrastructure.configuration.beanpostprocessor

import com.ajaxproject.financeservice.infrastructure.adapter.`in`.nats.NatsController
import com.google.protobuf.GeneratedMessageV3
import io.nats.client.Connection
import io.nats.client.Dispatcher
import io.nats.client.Message
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import reactor.core.scheduler.Schedulers

@Component
class NatsControllerBeanPostProcessor(private val connection: Connection) : BeanPostProcessor {

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        if (bean is NatsController<*, *>) {
            bean.initializeNatsController(connection)
        }
        return bean
    }
}

private fun <RequestT : GeneratedMessageV3, ResponseT : GeneratedMessageV3>
    NatsController<RequestT, ResponseT>.initializeNatsController(
    connection: Connection,
) {
    createDispatcher(connection).apply {
        subscribe(subject)
    }
}

private fun <RequestT : GeneratedMessageV3, ResponseT : GeneratedMessageV3>
    NatsController<RequestT, ResponseT>.createDispatcher(
    connection: Connection,
): Dispatcher {
    return connection.createDispatcher { message: Message ->
        val parsedData = parser.parseFrom(message.data)
        handle(parsedData)
            .map { it.toByteArray() }
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe { connection.publish(message.replyTo, it) }
    }
}
