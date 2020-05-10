package io.cutebot.markonimagemanage.service.messagehandlers.process

import io.cutebot.telegram.TelegramService
import io.cutebot.telegram.tgmodel.TgSendTextMessage
import io.cutebot.telegram.tgmodel.TgUpdate

class SimpleSendTextProcess(
        private val message: String,
        private val telegramService: TelegramService,
        private val chatId: Long
): BotProcess {

    override fun init(): BotProcess {
        val sendMessage = TgSendTextMessage(chatId = chatId, text = message)
        telegramService.sendMessage(sendMessage)
        return this
    }

    override fun handle(tgUpdate: TgUpdate): BotProcess {
        init()
        return this
    }
}