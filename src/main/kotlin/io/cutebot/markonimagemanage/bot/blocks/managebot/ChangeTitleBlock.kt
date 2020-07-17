package io.cutebot.markonimagemanage.bot.blocks.managebot

import io.cutebot.markonimagemanage.bot.blocks.managebot.tools.ManageTools
import io.cutebot.markonimagemanage.botclient.model.UpdateBotRequest
import io.cutebot.telegram.bot.block.BotBlock
import io.cutebot.telegram.bot.block.BotTextBlock
import io.cutebot.telegram.bot.model.TextMessage
import io.cutebot.telegram.client.model.keyboard.builder.ReplyKeyboardSimpleBuilder
import io.cutebot.telegram.interaction.model.ChatAnswer

class ChangeTitleBlock(
        private val tools: ManageTools
): BotTextBlock {

    private var currentMessage = "Insert new title."

    override fun getAnswer(): ChatAnswer {
        return ChatAnswer.textWithReplyKeyboard(currentMessage, markup)
    }

    override fun handleText(message: TextMessage): BotBlock {
        val text = message.text
        when {
            text == backReaction -> ManageMenuBlock(tools)
            text.isEmpty() -> return this
            text.length > 32 -> {
                currentMessage = "Too long (max 32 symbols). Please input correct title."
                return this
            }
        }

        val bot = tools.botClient.getBot(tools.selectedBotId)
        if (bot.adminUsrId != tools.userId) {
            throw RuntimeException("Not your bot")
        }

        val updateBotRequest = UpdateBotRequest(token = null, title = text)
        tools.botClient.updateBot(tools.selectedBotId, updateBotRequest)

        return ManageMenuBlock(tools)
    }

    companion object {
        private const val backReaction = "\uD83D\uDD19 Back"
        private val markup = ReplyKeyboardSimpleBuilder()
                .oneButton(backReaction)
    }
}
