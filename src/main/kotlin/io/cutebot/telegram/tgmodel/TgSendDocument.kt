package io.cutebot.telegram.tgmodel

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.File

class TgSendDocument(
        chatId: Long,

        @field:JsonProperty("document")
        val document: File
): TgSendMessage(chatId)
