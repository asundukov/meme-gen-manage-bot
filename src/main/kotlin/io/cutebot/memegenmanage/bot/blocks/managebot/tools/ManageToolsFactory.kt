package io.cutebot.memegenmanage.bot.blocks.managebot.tools

import io.cutebot.memegenmanage.bot.blocks.StartBlock
import io.cutebot.memegenmanage.botclient.BotClient
import io.cutebot.telegram.bot.block.BotBlock
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ManageToolsFactory(
        private val botClient: BotClient,

        @Value("\${image.dir}")
        private val imageDir: String

) {
    fun getTools(userId: Long, botId: Int, returnBlock: BotBlock): ManageTools {
        return ManageTools(
                userId = userId,
                botClient = botClient,
                selectedBotId = botId,
                imageDir = imageDir,
                returnBlock = returnBlock
        )
    }
}