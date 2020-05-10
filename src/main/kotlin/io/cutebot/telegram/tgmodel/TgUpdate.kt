package io.cutebot.telegram.tgmodel

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.cutebot.telegram.tgmodel.inline.TgInlineQuery

@JsonIgnoreProperties(ignoreUnknown = true)
data class TgUpdate (

        @field: JsonProperty("update_id")
        val updateId: Int,
    
        @field: JsonProperty
        val message: TgMessage?
    
//        @field: JsonProperty("edited_message")
//        val editedMessage: TgMessage?,
//
//        @field: JsonProperty("channel_post")
//        val channelPost: TgMessage?,
//
//        @field: JsonProperty("inline_query")
//        val inlineQuery: TgInlineQuery?,
//
//        @field: JsonProperty("callback_query")
//        val callbackQuery: TgCallbackQuery?,
//
//        @field: JsonProperty("chosen_inline_result")
//        val chosenInlineResult: TgChosenInlineResult?
        
)
