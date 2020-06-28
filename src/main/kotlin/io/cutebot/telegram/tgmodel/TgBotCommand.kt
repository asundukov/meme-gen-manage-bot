package io.cutebot.telegram.tgmodel

import com.fasterxml.jackson.annotation.JsonProperty

data class TgBotCommand(

        @field: JsonProperty
        val command: String,

        @field: JsonProperty
        val description: String
)
