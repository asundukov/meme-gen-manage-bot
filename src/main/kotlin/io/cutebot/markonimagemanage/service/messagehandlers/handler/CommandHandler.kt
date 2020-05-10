package io.cutebot.markonimagemanage.service.messagehandlers.handler

import io.cutebot.markonimagemanage.service.messagehandlers.process.BotProcess
import io.cutebot.telegram.tgmodel.TgUser

interface CommandHandler {
    fun handle(params: String, chatId: Long, user: TgUser): BotProcess
}