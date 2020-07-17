package io.cutebot.markonimagemanage.bot.blocks.managebot

import io.cutebot.markonimagemanage.bot.blocks.managebot.tools.ManageTools
import io.cutebot.markonimagemanage.bot.blocks.managebot.tools.MarkBuilder
import io.cutebot.markonimagemanage.botclient.model.CreateMarkRequest
import io.cutebot.telegram.bot.block.BotBlock
import io.cutebot.telegram.bot.block.BotTextBlock
import io.cutebot.telegram.bot.model.TextMessage
import io.cutebot.telegram.client.model.keyboard.builder.ReplyKeyboardSimpleBuilder
import io.cutebot.telegram.interaction.model.ChatAnswer

class MarkConfirmationBlock(
        private val markBuilder: MarkBuilder,
        private val tools: ManageTools
): BotTextBlock {

    private var currentMessage = "Confirm creation mark\n" +
            "title: <b>" + markBuilder.title + "</b>\n" +
            "description: " + markBuilder.description + "\n" +
            "position: " + markBuilder.position.desc + "\n" +
            "size: " + markBuilder.sizePercent + "%\n" +
            "opacity: " + markBuilder.opacity + "%"

    override fun getAnswer(): ChatAnswer {
        return ChatAnswer.textWithReplyKeyboard(currentMessage, markup)
    }

    override fun handleText(message: TextMessage): BotBlock {
        when (message.text) {
            confirmReaction -> {
                tools.botClient.addMark(CreateMarkRequest(
                        botId = tools.selectedBotId,
                        imagePath = markBuilder.filePath,
                        description = markBuilder.description,
                        title = markBuilder.title,
                        sizeValue = markBuilder.sizePercent.div(100.0).toBigDecimal(),
                        position = markBuilder.position,
                        opacity = markBuilder.opacity.div(100.0).toBigDecimal()
                ))
                return ManageMenuBlock(tools)
            }
            cancelReaction -> return tools.returnBlock
            titleReaction -> return MarkSetTitleBlock(markBuilder, tools)
            descriptionReaction -> return MarkSetDescriptionBlock(markBuilder, tools)
            positionReaction -> return MarkSetPositionBlock(markBuilder, tools)
            sizeReaction -> return MarkSetSizeBlock(markBuilder, tools)
            opacityReaction -> return MarkSetOpacityBlock(markBuilder, tools)
        }

        return this
    }

    companion object {
        private const val cancelReaction = "❌ Cancel"
        private const val confirmReaction = "✅ Confirm"
        private const val titleReaction = "⚙️ title"
        private const val descriptionReaction = "⚙️ description"
        private const val positionReaction = "⚙️ position"
        private const val sizeReaction = "⚙️ size"
        private const val opacityReaction = "⚙️ opacity"

        private val markup = ReplyKeyboardSimpleBuilder()
                .addRow(titleReaction, descriptionReaction)
                .addRow(positionReaction, sizeReaction, opacityReaction)
                .addRow(cancelReaction, confirmReaction)
                .build()
    }
}
