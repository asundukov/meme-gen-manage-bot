package io.cutebot.markonimagemanage.bot.blocks.managebot

import io.cutebot.markonimagemanage.bot.blocks.managebot.tools.ManageTools
import io.cutebot.markonimagemanage.bot.blocks.managebot.tools.MarkBuilder
import io.cutebot.telegram.bot.block.BotBlock
import io.cutebot.telegram.bot.block.BotTextBlock
import io.cutebot.telegram.bot.model.TextMessage
import io.cutebot.telegram.client.model.keyboard.builder.ReplyKeyboardSimpleBuilder
import io.cutebot.telegram.interaction.model.ChatAnswer

class MarkSetTitleBlock(
        private val markBuilder: MarkBuilder,
        private val tools: ManageTools
): BotTextBlock {
    private var currentMessage = "Set mark's title. You will see it in mark list"

    override fun getAnswer(): ChatAnswer {
        return ChatAnswer.textWithReplyKeyboard(currentMessage, markup)
    }

    override fun handleText(message: TextMessage): BotBlock {
        val text = message.text
        when {
            text == backReaction -> return MarkConfirmationBlock(markBuilder, tools)
            text.isEmpty() -> return this
            text.length > 32 -> {
                currentMessage = "Too long (max 32 symbols). Please input correct title."
                return this
            }
        }

        markBuilder.title = text
        return MarkConfirmationBlock(markBuilder, tools)
    }

    companion object {
        private const val backReaction = "❌ Cancel"
        private val markup = ReplyKeyboardSimpleBuilder()
                .oneButton(backReaction)
    }
}
