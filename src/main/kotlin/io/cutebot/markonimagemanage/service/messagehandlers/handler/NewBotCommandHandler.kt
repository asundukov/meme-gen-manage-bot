package io.cutebot.markonimagemanage.service.messagehandlers.handler

import io.cutebot.markonimagemanage.botclient.BotClient
import io.cutebot.markonimagemanage.service.messagehandlers.process.BotProcess
import io.cutebot.markonimagemanage.service.messagehandlers.process.NewBotProcess
import io.cutebot.telegram.TelegramService
import io.cutebot.telegram.tgmodel.TgUser
import org.springframework.stereotype.Service

@Service
class NewBotCommandHandler(
        private val telegramService: TelegramService,
        private val botClient: BotClient,
        private val startCommandHandler: StartCommandHandler,
        private val manageBotCommandHandler: ManageBotCommandHandler
): CommandHandler {

    override fun handle(params: String, chatId: Long, user: TgUser): BotProcess {
        return NewBotProcess(telegramService, botClient, user, chatId, startCommandHandler, manageBotCommandHandler).init()
    }
}