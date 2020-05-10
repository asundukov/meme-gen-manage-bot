package io.cutebot.telegram.tgmodel

import com.fasterxml.jackson.annotation.JsonProperty

open class TgSendMessage(
        @field: JsonProperty("chat_id")
        val chatId: Long
)
