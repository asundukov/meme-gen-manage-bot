package io.cutebot.markonimagemanage.service.messagehandlers.handler

import io.cutebot.markonimagemanage.botclient.BotClient
import io.cutebot.markonimagemanage.service.messagehandlers.process.BotProcess
import io.cutebot.markonimagemanage.service.messagehandlers.process.SimpleSendTextProcess
import io.cutebot.telegram.TelegramService
import io.cutebot.telegram.tgmodel.TgUser
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class AboutCommandHandler(
        private val telegramService: TelegramService,

        @Value("\${bot.contact}")
        private val tgContact: String,
        @Value("\${bot.twitter}")
        private val twitterContact: String
) : CommandHandler {

    override fun handle(params: String, chatId: Long, user: TgUser): BotProcess {
        val message = "About\n\n" +
                "This bot is a bot-client for open-source project " +
                "<a href=\"https://github.com/asundukov/mark-on-image-bot\">mark-on-image-bot</a>\n\n" +
                "Contact: @" + tgContact + "\n" +
                "Twitter: <a href=\"https://twitter.com/" + twitterContact + "\">@" + twitterContact + "</a>"

        return SimpleSendTextProcess(message = message, chatId = chatId, telegramService = telegramService).init()
    }
}