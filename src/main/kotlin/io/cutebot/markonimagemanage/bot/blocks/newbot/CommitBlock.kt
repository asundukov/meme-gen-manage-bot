package io.cutebot.markonimagemanage.bot.blocks.newbot

import io.cutebot.markonimagemanage.botclient.BotClient
import io.cutebot.telegram.bot.block.BotBlock
import io.cutebot.telegram.bot.block.BotTextBlock
import io.cutebot.telegram.bot.model.TextMessage
import io.cutebot.telegram.client.model.keyboard.builder.ReplyKeyboardSimpleBuilder
import io.cutebot.telegram.interaction.model.ChatAnswer

class CommitBlock(
        private val bot: BotBuilder,
        private val botClient: BotClient,
        private val returnBlock: BotBlock
): BotTextBlock {

    override fun getAnswer(): ChatAnswer {
        val maskedToken = bot.token!!.replace(Regex("[a-zA-Z]"), "*")
        val message = "Ok, here is your bot: \n" +
                "name: " + bot.name + "\n" +
                "token: " + maskedToken

        val keyboard = ReplyKeyboardSimpleBuilder()
                .addRow(okReaction, cancelReaction)
                .addRow(changeNameReaction, changeTokenReaction)
                .build()

        return ChatAnswer.textWithReplyKeyboard(message, keyboard)
    }

    override fun handleText(message: TextMessage): BotBlock {
        when (message.text) {
            okReaction -> {
                val created = botClient.addBot(bot.usrId, bot.token!!, bot.name!!)
                bot.botId = created.botId
                return returnBlock
            }
            cancelReaction -> {
                return returnBlock
            }
            changeNameReaction -> {
                return SetTitleBlock(bot, botClient, returnBlock)
            }
            changeTokenReaction -> {
                return SetTokenBlock(bot, botClient, returnBlock)
            }
        }
        return this
    }

    companion object {
        private const val okReaction = "✅ Ok"
        private const val cancelReaction = "❌ Cancel"
        private const val changeNameReaction = "Change name"
        private const val changeTokenReaction = "Change token"
    }
}
