package io.cutebot.telegram.tgmodel.keyboard

import com.fasterxml.jackson.annotation.JsonProperty

class TgInlineKeyboardMarkup(
        @JsonProperty("inline_keyboard")
        val inlineKeyboard: List<List<TgInlineKeyboardButton>>
) : TgKeyboard() {

    override fun toString(): String {
        return "TgInlineKeyboardMarkup{" +
                "inlineKeyboard=" + inlineKeyboard +
                '}'
    }

}
