package io.cutebot.markonimagemanage.service.messagehandlers.handler

import io.cutebot.markonimagemanage.botclient.BotClient
import io.cutebot.markonimagemanage.service.messagehandlers.process.BotProcess
import io.cutebot.markonimagemanage.service.messagehandlers.process.ManageBotProcess
import io.cutebot.telegram.TelegramService
import io.cutebot.telegram.tgmodel.TgSendTextMessage
import io.cutebot.telegram.tgmodel.TgUser
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ManageBotCommandHandler(
        private val telegramService: TelegramService,
        private val botClient: BotClient,
        private val startCommandHandler: StartCommandHandler,
        @Value("\${image.dir}")
        private val imageDir: String
): CommandHandler {
    override fun handle(params: String, chatId: Long, user: TgUser): BotProcess {
        return try {
            val botId = params.toInt()
            val bot = botClient.getBot(botId)
            if (bot.adminUsrId != user.id) {
                throw RuntimeException("Not your bot")
            }
            ManageBotProcess(telegramService, botClient, user, chatId, botId, startCommandHandler, imageDir).init()
        } catch (e: Exception) {
            telegramService.sendMessage(TgSendTextMessage(chatId, "Sorry, dont understand you (" + e.message + ")"))
            startCommandHandler.handle("", chatId, user)
        }
    }
}
