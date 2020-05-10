package io.cutebot.telegram.tgmodel

import com.fasterxml.jackson.annotation.JsonProperty

data class TgCallbackQuery (
        @field: JsonProperty
        val id: Long,

        @field: JsonProperty
        val from: TgUser,

        @field: JsonProperty
        val message: TgMessage?,

        @field: JsonProperty("chat_instance")
        var chatInstance: String?,

        @field: JsonProperty
        var data: String?

)
