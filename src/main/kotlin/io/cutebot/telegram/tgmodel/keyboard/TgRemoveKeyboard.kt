package io.cutebot.telegram.tgmodel.keyboard

import com.fasterxml.jackson.annotation.JsonProperty

data class TgRemoveKeyboard(
        @field: JsonProperty("remove_keyboard")
        private val removeKeyboard: Boolean
) : TgKeyboard()
