package io.cutebot.markonimagemanage.service.messagehandlers.handler

import io.cutebot.markonimagemanage.botclient.BotClient
import io.cutebot.markonimagemanage.service.messagehandlers.process.BotProcess
import io.cutebot.markonimagemanage.service.messagehandlers.process.SimpleSendTextProcess
import io.cutebot.telegram.TelegramService
import io.cutebot.telegram.tgmodel.TgUser
import org.springframework.stereotype.Service

@Service
class StartCommandHandler(
        private val botClient: BotClient,
        private val telegramService: TelegramService
) : CommandHandler {

    override fun handle(params: String, chatId: Long, user: TgUser): BotProcess {
        var message = "Welcome!\n"

        message += "I can help create your own bot like:\n" +
                "@StoriesTellerBot - russian 2020 election no campaign\n" +
                "@RickAndMortyPicBot - Rick&Morty fun bot\n" +
                "@watermarkTestBot - place watermark example\n" +
                "You can invite your team to make avatars or other labeled media for your campaigns\n\n"

        val bots = botClient.getBots(user.id)

        if (bots.isEmpty()) {
            message += "Currently you dont have any bots.\n"
        } else {
            message += "\nYour bots:\n"
            var i = 1
            for (bot in bots) {
                message += i.toString() + ". <b>" + bot.title + "</b> " +
                        "@" + bot.username + " /manage_" + bot.botId + "\n"
                i++
            }
        }

        if (bots.size >= 9) {
            message += "\nYou already have 9 bots. You cannot add more to this account"
        } else {
            message += "\nYou can add new mark-on-image-bot /newbot"
        }

        return SimpleSendTextProcess(message = message, chatId = chatId, telegramService = telegramService).init()
    }
}