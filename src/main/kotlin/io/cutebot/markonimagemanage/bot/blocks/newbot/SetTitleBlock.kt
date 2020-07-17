package io.cutebot.markonimagemanage.bot.blocks.newbot

import io.cutebot.markonimagemanage.botclient.BotClient
import io.cutebot.telegram.bot.block.BotBlock
import io.cutebot.telegram.bot.block.BotTextBlock
import io.cutebot.telegram.bot.model.TextMessage
import io.cutebot.telegram.interaction.model.ChatAnswer

class SetTitleBlock(
        private val bot: BotBuilder,
        private val botClient: BotClient,
        private val returnBlock: BotBlock
): BotTextBlock {
    private var currentMessage = "Insert title of new bot. You well see this name in list of your bots\n" +
            "Or /start to return to main menu"

    override fun getAnswer(): ChatAnswer {
        return ChatAnswer.text(currentMessage)
    }

    override fun handleText(message: TextMessage): BotBlock {
        if (message.text.length > 32) {
            currentMessage = "title too long (max 32 symbols). Please input title"
            return this
        }
        bot.name = message.text
        return if (bot.token == null) {
            SetTokenBlock(bot, botClient, returnBlock)
        } else {
            CommitBlock(bot, botClient, returnBlock)
        }
    }
}
