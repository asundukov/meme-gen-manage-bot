package io.cutebot.memegenmanage.bot.blocks.managebot

import io.cutebot.memegenmanage.bot.blocks.managebot.tools.ManageTools
import io.cutebot.memegenmanage.bot.blocks.managebot.tools.MemeBuilder
import io.cutebot.telegram.bot.block.BotBlock
import io.cutebot.telegram.bot.block.BotTextBlock
import io.cutebot.telegram.bot.model.TextMessage
import io.cutebot.telegram.client.model.keyboard.builder.ReplyKeyboardSimpleBuilder
import io.cutebot.telegram.interaction.model.ChatAnswer

class MemeSetAliasBlock(
        private val memeBuilder: MemeBuilder,
        private val tools: ManageTools
): BotTextBlock {
    private var currentMessage = "Set meme's alias. It could be used for fast filtering in inline mode"

    override fun getAnswer(): ChatAnswer {
        return if (memeBuilder.alias.isNotEmpty()) {
            ChatAnswer.text(currentMessage, markup)
        } else {
            ChatAnswer.text(currentMessage)
        }
    }

    override fun handleText(message: TextMessage): BotBlock {
        val text = message.text
        when {
            text == backReaction && memeBuilder.alias.isNotEmpty() -> return MemeConfirmationBlock(memeBuilder, tools)
            text.isEmpty() -> return this
            text.length > 32 -> {
                currentMessage = "Too long (max 32 symbols). Please input correct title."
                return this
            }
        }

        memeBuilder.alias = text

        return if (memeBuilder.areas.isEmpty()) {
            MemeAddTextAreaBlock(memeBuilder, tools)
        } else {
            MemeConfirmationBlock(memeBuilder, tools)
        }
    }

    companion object {
        private const val backReaction = "❌ Cancel"
        private val markup = ReplyKeyboardSimpleBuilder()
                .oneButton(backReaction)
    }
}
