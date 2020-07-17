package io.cutebot.markonimagemanage.bot.blocks.managebot

import io.cutebot.markonimagemanage.bot.blocks.managebot.tools.ManageTools
import io.cutebot.markonimagemanage.bot.blocks.managebot.tools.MarkBuilder
import io.cutebot.telegram.bot.block.BotBlock
import io.cutebot.telegram.bot.block.BotTextBlock
import io.cutebot.telegram.bot.model.TextMessage
import io.cutebot.telegram.client.model.keyboard.builder.ReplyKeyboardSimpleBuilder
import io.cutebot.telegram.interaction.model.ChatAnswer

class MarkSetDescriptionBlock(
        private val markBuilder: MarkBuilder,
        private val tools: ManageTools
): BotTextBlock {
    private var currentMessage = "Set mark's description. It will be displayed for your bot's users"

    override fun getAnswer(): ChatAnswer {
        return ChatAnswer.textWithReplyKeyboard(currentMessage, markup)
    }

    override fun handleText(message: TextMessage): BotBlock {
        val text = message.text
        when {
            text == backReaction -> return MarkConfirmationBlock(markBuilder, tools)
            text.isEmpty() -> return this
            text.length > 512 -> {
                currentMessage = "Too long (max 512 symbols). Please input correct description."
                return this
            }
        }

        markBuilder.description = text
        return MarkConfirmationBlock(markBuilder, tools)
    }

    companion object {
        private const val backReaction = "❌ Cancel"
        private val markup = ReplyKeyboardSimpleBuilder()
                .oneButton(backReaction)
    }
}
