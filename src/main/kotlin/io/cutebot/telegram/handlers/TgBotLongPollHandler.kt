package io.cutebot.telegram.handlers

import io.cutebot.telegram.tgmodel.TgResponseUpdate
import io.cutebot.telegram.tgmodel.TgUpdate

interface TgBotLongPollHandler {
    fun handle(update: TgUpdate)
    fun getMessages(botToken: String, offset: Int, timeout: Int, limit: Int): TgResponseUpdate
    fun stopWithWebHook(botToken: String)
}