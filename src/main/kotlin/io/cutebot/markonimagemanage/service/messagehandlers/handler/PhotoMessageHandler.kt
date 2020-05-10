package io.cutebot.markonimagemanage.service.messagehandlers.handler

import io.cutebot.markonimagemanage.botclient.BotClient
import io.cutebot.telegram.TelegramService
import io.cutebot.telegram.tgmodel.TgDocument
import io.cutebot.telegram.tgmodel.TgUser
import io.cutebot.telegram.tgmodel.photo.TgPhotoSize
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


@Service
class PhotoMessageHandler(
       private val telegramService: TelegramService,
       private val botClient: BotClient
) {

    fun handlePhoto(photo: List<TgPhotoSize>, chatId: Long, usr: TgUser) {
        handle(photo[photo.size - 1].fileId, chatId, usr)
    }

    fun handleDocument(photo: TgDocument, chatId: Long, usr: TgUser) {
        handle(photo.fileId, chatId, usr)
    }

    private fun handle(fileId: String, chatId: Long, usr: TgUser) {

    }

    companion object {
        private val logger = LoggerFactory.getLogger(PhotoMessageHandler::class.java)
    }
}