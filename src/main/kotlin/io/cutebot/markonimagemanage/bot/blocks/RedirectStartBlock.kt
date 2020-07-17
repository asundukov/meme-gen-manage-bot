package io.cutebot.markonimagemanage.bot.blocks

import io.cutebot.markonimagemanage.botclient.BotClient
import io.cutebot.telegram.bot.block.BotBlock
import io.cutebot.telegram.bot.block.BotTextBlock
import io.cutebot.telegram.bot.model.TextMessage
import io.cutebot.telegram.interaction.model.ChatAnswer
import org.springframework.stereotype.Service

@Service
class RedirectStartBlock(
        private val botClient: BotClient
): BotTextBlock {
    override fun getAnswer(): ChatAnswer {
        return ChatAnswer.noAnswer()
    }

    override fun handleText(message: TextMessage): BotBlock {
        return StartBlock(botClient, message.user!!.id)
    }
}
