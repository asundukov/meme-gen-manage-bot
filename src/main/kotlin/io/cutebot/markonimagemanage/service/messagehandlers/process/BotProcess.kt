package io.cutebot.markonimagemanage.service.messagehandlers.process

import io.cutebot.telegram.tgmodel.TgUpdate

interface BotProcess {
    fun init(): BotProcess
    fun handle(tgUpdate: TgUpdate): BotProcess
}