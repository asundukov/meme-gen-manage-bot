package io.cutebot.markonimagemanage.bot.blocks.managebot

import io.cutebot.markonimagemanage.bot.blocks.managebot.tools.ManageTools
import io.cutebot.markonimagemanage.bot.blocks.managebot.tools.MarkBuilder
import io.cutebot.markonimagemanage.bot.commands.ManageBotCommand
import io.cutebot.telegram.bot.block.BotBlock
import io.cutebot.telegram.bot.block.BotTextBlock
import io.cutebot.telegram.bot.model.TextMessage
import io.cutebot.telegram.client.model.keyboard.builder.ReplyKeyboardSimpleBuilder
import io.cutebot.telegram.interaction.model.ChatAnswer

class MarkSetSizeBlock(
        private val markBuilder: MarkBuilder,
        private val tools: ManageTools
): BotTextBlock {
    private var currentMessage = "Send size in percent (from 1 to 500). It is max ratio of mark side to image side"

    override fun getAnswer(): ChatAnswer {
        return ChatAnswer.textWithReplyKeyboard(currentMessage, markup)
    }

    override fun handleText(message: TextMessage): BotBlock {
        if (message.text == cancelReaction) {
            return MarkConfirmationBlock(markBuilder, tools)
        }

        val size = try {
            message.text.trim().toInt()
        } catch (e: NumberFormatException) {
            currentMessage = "It isn't a number"
            return this
        }

        if (size < 1) {
            currentMessage = "Too small (min = 1)"
            return this
        }
        if (size > 500) {
            currentMessage = "Too large (max = 500)"
            return this
        }
        markBuilder.sizePercent = size
        return MarkConfirmationBlock(markBuilder, tools)
    }

    companion object {
        private const val cancelReaction = "❌ Cancel"
        private val markup = ReplyKeyboardSimpleBuilder()
                .oneButton(cancelReaction)
    }
}
