package com.ajaxproject.telegrambot.util

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow

object KeyboardUtils {

    fun inlineKeyboardInOneRow(vararg buttons: InlineKeyboardButton): InlineKeyboardMarkup {
        return InlineKeyboardMarkup.builder()
            .keyboardRow(InlineKeyboardRow(buttons.toList()))
            .build()
    }

    fun inlineKeyboardWithManyRows(vararg buttons: InlineKeyboardButton): InlineKeyboardMarkup {
        val keyboardBuilder = InlineKeyboardMarkup.builder()
        buttons.forEach { button ->
            keyboardBuilder.keyboardRow(InlineKeyboardRow(listOf(button)))
        }
        return keyboardBuilder.build()
    }

    fun inlineButton(name: String, callbackData: String): InlineKeyboardButton {
        return InlineKeyboardButton.builder()
            .text(name)
            .callbackData(callbackData)
            .build()
    }
}
