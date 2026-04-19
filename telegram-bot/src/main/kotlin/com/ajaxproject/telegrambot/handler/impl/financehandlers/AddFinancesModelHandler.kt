package com.ajaxproject.telegrambot.handler.impl.financehandlers

import com.ajaxproject.internalapi.finance.commonmodels.FinanceType
import com.ajaxproject.telegrambot.enums.Buttons.ADD_FINANCE_AGAIN_BUTTON
import com.ajaxproject.telegrambot.enums.Buttons.BACK_TO_MENU_BUTTON
import com.ajaxproject.telegrambot.enums.Commands.ADD_FINANCE
import com.ajaxproject.telegrambot.enums.Commands.MENU
import com.ajaxproject.telegrambot.enums.ConversationState.CONVERSATION_STARTED
import com.ajaxproject.telegrambot.enums.ConversationState.WAITING_FOR_ADD_FINANCE
import com.ajaxproject.telegrambot.enums.TextPropertyName.FAILED_ADD_FINANCE_TEXT
import com.ajaxproject.telegrambot.enums.TextPropertyName.SUCCESSFUL_ADD_FINANCE_TEXT
import com.ajaxproject.telegrambot.handler.UserRequestHandler
import com.ajaxproject.telegrambot.client.FinanceClient
import com.ajaxproject.telegrambot.dto.request.FinanceRequest
import com.ajaxproject.telegrambot.dto.response.FinanceResponse
import com.ajaxproject.telegrambot.service.telegram.TelegramMessageService
import com.ajaxproject.telegrambot.service.TextService
import com.ajaxproject.telegrambot.service.UserSessionService
import com.ajaxproject.telegrambot.service.isTextMessage
import com.ajaxproject.telegrambot.service.updatemodels.UpdateRequest
import com.ajaxproject.telegrambot.util.KeyboardUtils
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.message.Message
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.util.Date

@Component
class AddFinancesModelHandler(
    private val telegramService: TelegramMessageService,
    private val financeClient: FinanceClient,
    private val textService: TextService,
    private val userSessionService: UserSessionService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return WAITING_FOR_ADD_FINANCE == request.updateSession.state && request.update.isTextMessage()
    }

    override fun handle(dispatchRequest: UpdateRequest): Mono<Unit> {
        val financeData = dispatchRequest.update.message.text

        return Mono.just(financeData)
            .filter { it.matches(FINANCE_DATA_REGEX) }
            .switchIfEmpty {
                sendMessageDataIsNotCorrect(dispatchRequest)
                    .then(Mono.empty())
            }
            .flatMap {
                createFinance(dispatchRequest, financeData)
            }
            .flatMap {
                sendMessageCreateFinanceIsSuccessful(dispatchRequest)
            }
            .flatMap {
                userSessionService.updateSession(
                    CONVERSATION_STARTED,
                    dispatchRequest.chatId,
                    dispatchRequest.updateSession.localization
                )
            }
            .thenReturn(Unit)
    }

    private fun sendMessageDataIsNotCorrect(dispatchRequest: UpdateRequest): Mono<Message> {
        return telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = textService.getText(dispatchRequest.updateSession.localization, FAILED_ADD_FINANCE_TEXT.name),
            replyKeyboard = KeyboardUtils.run {
                inlineKeyboardInOneRow(
                    inlineButton(
                        textService.getText(
                            dispatchRequest.updateSession.localization,
                            BACK_TO_MENU_BUTTON.name
                        ),
                        MENU.command
                    )
                )
            }
        )
    }

    private fun sendMessageCreateFinanceIsSuccessful(dispatchRequest: UpdateRequest): Mono<Message> {
        return telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = textService.getText(dispatchRequest.updateSession.localization, SUCCESSFUL_ADD_FINANCE_TEXT.name),
            replyKeyboard = KeyboardUtils.run {
                inlineKeyboardInOneRow(
                    inlineButton(
                        textService.getText(dispatchRequest.updateSession.localization, ADD_FINANCE_AGAIN_BUTTON.name),
                        ADD_FINANCE.command
                    ),
                    inlineButton(
                        textService.getText(dispatchRequest.updateSession.localization, BACK_TO_MENU_BUTTON.name),
                        MENU.command
                    )
                )
            }
        )
    }

    private fun createFinance(dispatchRequest: UpdateRequest, financeData: String): Mono<FinanceResponse> {
        return financeClient.createFinance(
            FinanceRequest(
                userId = dispatchRequest.chatId,
                financeType = checkDataIncomeOrExpense(financeData),
                amount = financeData.substring(1).split(" ")[0].toDouble(),
                description = financeData.split(" ")[1],
                date = Date()
            )
        )
    }

    private fun checkDataIncomeOrExpense(financeData: String): FinanceType {
        return when {
            financeData.contains("+") -> FinanceType.INCOME
            financeData.contains("-") -> FinanceType.EXPENSE
            else -> throw IllegalArgumentException("Unknown finance type")
        }
    }

    companion object {
        val FINANCE_DATA_REGEX = Regex("[+-]\\d+ [^\\n\\r]+\$")
    }
}
