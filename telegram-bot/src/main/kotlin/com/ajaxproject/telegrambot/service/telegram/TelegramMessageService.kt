package com.ajaxproject.telegrambot.service.telegram

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.generics.TelegramClient
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Service
class TelegramMessageService(
    private val telegramClient: TelegramClient,
) {

    fun sendMessage(chatId: Long, text: String, replyKeyboard: ReplyKeyboard? = null): Mono<Message> {
        val sendMessage = SendMessage.builder().also { msg ->
            msg.text(text)
            msg.chatId(chatId)
            msg.replyMarkup(replyKeyboard)
        }.build()
        return execute(sendMessage)
            .subscribeOn(Schedulers.boundedElastic())
    }

    private fun execute(botApiMethod: BotApiMethod<Message>): Mono<Message> {
        return Mono.fromCallable { telegramClient.execute(botApiMethod) }
            .onErrorResume { error ->
                log.error("Failed to execute bot method {}", botApiMethod, error)
                Mono.empty()
            }
    }

    companion object {
        private val log = LoggerFactory.getLogger(TelegramMessageService::class.java)
    }
}
