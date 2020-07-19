package io.cutebot.memegenmanage.bot.blocks.managebot.tools

import io.cutebot.memegenmanage.botclient.BotClient
import io.cutebot.telegram.bot.block.BotBlock

data class ManageTools (
        val userId: Long,
        val botClient: BotClient,
        val selectedBotId: Int,
        val imageDir: String,
        val returnBlock: BotBlock
)
