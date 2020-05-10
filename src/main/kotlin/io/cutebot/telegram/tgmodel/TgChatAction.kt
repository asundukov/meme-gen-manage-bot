package io.cutebot.telegram.tgmodel

import com.fasterxml.jackson.annotation.JsonProperty

class TgChatAction(
        chatId: Long,
        @field: JsonProperty
        private val action: String
): TgSendMessage(chatId)
