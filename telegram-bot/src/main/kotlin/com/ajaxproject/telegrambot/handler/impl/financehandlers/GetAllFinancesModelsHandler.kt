package com.ajaxproject.telegrambot.handler.impl.financehandlers

import com.ajaxproject.internalapi.finance.commonmodels.FinanceType
import com.ajaxproject.telegrambot.enums.Buttons
import com.ajaxproject.telegrambot.enums.Commands.GET_EXPENSES
import com.ajaxproject.telegrambot.enums.Commands.GET_INCOMES
import com.ajaxproject.telegrambot.enums.Commands.MENU
import com.ajaxproject.telegrambot.enums.ConversationState.CONVERSATION_STARTED
import com.ajaxproject.telegrambot.enums.TextPropertyName.BACK_TO_MENU_TEXT
import com.ajaxproject.telegrambot.enums.TextPropertyName.NO_FINANCE_TEXT
import com.ajaxproject.telegrambot.handler.UserRequestHandler
import com.ajaxproject.telegrambot.client.FinanceClient
import com.ajaxproject.telegrambot.service.telegram.TelegramMessageService
import com.ajaxproject.telegrambot.service.TextService
import com.ajaxproject.telegrambot.service.UserSessionService
import com.ajaxproject.telegrambot.service.updatemodels.UpdateRequest
import com.ajaxproject.telegrambot.util.KeyboardUtils
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.message.Message
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmptyDeferred

@Component
class GetAllFinancesModelsHandler(
    private val telegramService: TelegramMessageService,
    private val financeClient: FinanceClient,
    private val userSessionService: UserSessionService,
    private val textService: TextService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, GET_INCOMES.command, GET_EXPENSES.command)
    }

    override fun handle(dispatchRequest: UpdateRequest): Mono<Unit> {
        return financeClient.getAllFinancesByUserId(
            dispatchRequest.chatId,
            dispatchRequest.update.callbackQuery.data.checkCommandIncomeOrExpense()
        )
            .flatMapMany { list ->
                Flux.fromIterable(list)
            }
            .switchIfEmptyDeferred {
                sendNoFinanceMessage(dispatchRequest)
                    .then(Mono.empty())
            }
            .flatMap { financeResponse ->
                telegramService.sendMessage(
                    chatId = dispatchRequest.chatId,
                    text = financeResponse.toString()
                )
            }
            .flatMap {
                userSessionService.updateSession(
                    CONVERSATION_STARTED,
                    dispatchRequest.chatId,
                    dispatchRequest.updateSession.localization
                )
            }
            .then(returnToMainMenu(dispatchRequest))
            .then(Mono.empty())
    }

    fun sendNoFinanceMessage(dispatchRequest: UpdateRequest): Mono<Message> {
        return telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = textService.getText(dispatchRequest.updateSession.localization, NO_FINANCE_TEXT.name),
        )
    }

    fun returnToMainMenu(dispatchRequest: UpdateRequest): Mono<Message> {
        return telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = textService.getText(dispatchRequest.updateSession.localization, BACK_TO_MENU_TEXT.name),
            replyKeyboard = KeyboardUtils.run {
                inlineKeyboardWithManyRows(
                    inlineButton(
                        textService.getText(
                            dispatchRequest.updateSession.localization,
                            Buttons.BACK_TO_MENU_BUTTON.name
                        ),
                        MENU.command
                    )
                )
            }
        )
    }
}

private fun String.checkCommandIncomeOrExpense(): FinanceType {
    return when (this) {
        GET_INCOMES.command -> FinanceType.INCOME
        GET_EXPENSES.command -> FinanceType.EXPENSE
        else -> throw IllegalArgumentException("Unknown finance type")
    }
}
