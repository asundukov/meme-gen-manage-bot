package io.cutebot.markonimagemanage.service.messagehandlers.handler

import io.cutebot.markonimagemanage.service.messagehandlers.process.BotProcess
import io.cutebot.markonimagemanage.service.messagehandlers.process.SimpleSendTextProcess
import io.cutebot.telegram.TelegramService
import io.cutebot.telegram.tgmodel.TgUser
import org.springframework.stereotype.Service

@Service
class UnknownCommandHandler(
        private val telegramService: TelegramService
) : CommandHandler {

    private val defaultMessage = "Type /help to see possible bot commands"

    override fun handle(params: String, chatId: Long, user: TgUser): BotProcess {
        return SimpleSendTextProcess(message = defaultMessage, chatId = chatId, telegramService = telegramService).init()
    }
}