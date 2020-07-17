package io.cutebot.markonimagemanage.bot.blocks.newbot

import io.cutebot.markonimagemanage.botclient.BotClient
import io.cutebot.telegram.bot.block.BotBlock
import io.cutebot.telegram.bot.block.BotTextBlock
import io.cutebot.telegram.bot.model.TextMessage
import io.cutebot.telegram.interaction.model.ChatAnswer

class SetTokenBlock(
        private val bot: BotBuilder,
        private val botClient: BotClient,
        private val returnBlock: BotBlock

): BotTextBlock {
    private var currentMessage = "Input token of your bot\n" +
            "You can get bot token from @BotFather"

    override fun getAnswer(): ChatAnswer {
        return ChatAnswer.text(currentMessage)
    }

    override fun handleText(message: TextMessage): BotBlock {
        if (message.text.length > 100) {
            currentMessage = "Token too long. Please input correct token"
            return this
        }
        bot.token = message.text

        return CommitBlock(bot, botClient, returnBlock)
    }
}
