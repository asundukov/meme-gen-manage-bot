package io.cutebot.telegram.handlers

import io.cutebot.telegram.tgmodel.TgUpdate

interface TgBotWebHookHandler {
    fun handle(token: String, update: TgUpdate)
}
