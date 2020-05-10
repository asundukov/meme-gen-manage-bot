package io.cutebot.telegram.tgmodel.inline

import com.fasterxml.jackson.annotation.JsonProperty

data class TgInputMessageContent(
        @field: JsonProperty("message_text")
        val messageText: String,

        @field: JsonProperty("parse_mode")
        val parseMode: String = "HTML",

        @field: JsonProperty("disable_web_page_preview")
        val disableWebPagePreview: Boolean = true
)
