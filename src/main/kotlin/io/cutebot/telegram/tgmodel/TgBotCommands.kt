package io.cutebot.telegram.tgmodel

import com.fasterxml.jackson.annotation.JsonProperty

data class TgBotCommands (
        @field: JsonProperty
        val commands: ArrayList<TgBotCommand> = ArrayList()
)
