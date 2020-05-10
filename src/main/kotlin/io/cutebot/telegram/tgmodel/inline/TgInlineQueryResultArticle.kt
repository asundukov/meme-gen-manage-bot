package io.cutebot.telegram.tgmodel.inline

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import io.cutebot.telegram.tgmodel.keyboard.TgInlineKeyboardMarkup

@JsonInclude(JsonInclude.Include.NON_NULL)
class TgInlineQueryResultArticle(
        @field: JsonProperty
        val type: String = "article",

        @field: JsonProperty
        val id: String,

        @field: JsonProperty
        val title: String,

        @field: JsonProperty("input_message_content")
        val inputMessageContent: TgInputMessageContent,

        @field: JsonProperty
        val description: String? = null,


        @field: JsonProperty("reply_markup")
        val replyKeyboardMarkup: TgInlineKeyboardMarkup? = null

) : TgInlineQueryResult() {

    override fun toString(): String {
        return "TgInlineQueryResultArticle{" +
                "type='" + type + '\'' +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", inputMessageContent=" + inputMessageContent +
                ", replyKeyboardMarkup=" + replyKeyboardMarkup +
                '}'
    }
}