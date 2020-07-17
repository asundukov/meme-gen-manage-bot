package io.cutebot.markonimagemanage.bot.blocks.managebot

import io.cutebot.markonimagemanage.bot.blocks.managebot.tools.ManageTools
import io.cutebot.markonimagemanage.bot.blocks.managebot.tools.MarkBuilder
import io.cutebot.markonimagemanage.bot.commands.ManageBotCommand
import io.cutebot.markonimagemanage.botclient.model.MarkPosition
import io.cutebot.telegram.bot.block.BotBlock
import io.cutebot.telegram.bot.block.BotTextBlock
import io.cutebot.telegram.bot.model.TextMessage
import io.cutebot.telegram.client.model.keyboard.builder.ReplyKeyboardSimpleBuilder
import io.cutebot.telegram.interaction.model.ChatAnswer

class MarkSetPositionBlock(
        private val markBuilder: MarkBuilder,
        private val tools: ManageTools
): BotTextBlock {
    private var currentMessage = "Select mark description. It is where mark will place on user's images"

    override fun getAnswer(): ChatAnswer {
        return ChatAnswer.textWithReplyKeyboard(currentMessage, markup)
    }

    override fun handleText(message: TextMessage): BotBlock {
        if (message.text == cancelReaction) {
            return MarkConfirmationBlock(markBuilder, tools)
        }

        val selectedPos = posMap[message.text] ?: return this
        markBuilder.position = selectedPos
        return MarkConfirmationBlock(markBuilder, tools)
    }

    companion object {
        private const val cancelReaction = "❌ Cancel"
        private val markup = ReplyKeyboardSimpleBuilder()
                .addRow(MarkPosition.LT.desc, MarkPosition.T.desc, MarkPosition.RT.desc)
                .addRow(MarkPosition.L.desc, MarkPosition.C.desc, MarkPosition.R.desc)
                .addRow(MarkPosition.LB.desc, MarkPosition.B.desc, MarkPosition.RB.desc)
                .addRow(cancelReaction)
                .build()

        private val posMap = MarkPosition.values().map { it.desc to it }.toMap()
    }
}
