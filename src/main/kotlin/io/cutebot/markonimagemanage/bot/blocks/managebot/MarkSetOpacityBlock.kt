package io.cutebot.markonimagemanage.bot.blocks.managebot

import io.cutebot.markonimagemanage.bot.blocks.managebot.tools.ManageTools
import io.cutebot.markonimagemanage.bot.blocks.managebot.tools.MarkBuilder
import io.cutebot.telegram.bot.block.BotBlock
import io.cutebot.telegram.bot.block.BotTextBlock
import io.cutebot.telegram.bot.model.TextMessage
import io.cutebot.telegram.client.model.keyboard.builder.ReplyKeyboardSimpleBuilder
import io.cutebot.telegram.interaction.model.ChatAnswer

class MarkSetOpacityBlock(
        private val markBuilder: MarkBuilder,
        private val tools: ManageTools
): BotTextBlock {
    private var currentMessage = "Send opacity in percent (from 1 to 100)"

    override fun getAnswer(): ChatAnswer {
        return ChatAnswer.textWithReplyKeyboard(currentMessage, markup)
    }

    override fun handleText(message: TextMessage): BotBlock {
        if (message.text == cancelReaction) {
            return MarkConfirmationBlock(markBuilder, tools)
        }

        try {
            val opacity = message.text.trim().toInt()
            if (opacity < 1) {
                currentMessage = "Too small (min = 1)"
                return this
            }
            if (opacity > 100) {
                currentMessage = "Too large (max = 100)"
                return this
            }
            markBuilder.opacity = opacity
            return MarkConfirmationBlock(markBuilder, tools)
        } catch (e: NumberFormatException) {
            currentMessage = "It isn't a number"
            return this
        }
    }

    companion object {
        private const val cancelReaction = "❌ Cancel"
        private val markup = ReplyKeyboardSimpleBuilder()
                .oneButton(cancelReaction)
    }
}
