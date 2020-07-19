package io.cutebot.memegenmanage.bot.blocks.managebot

import io.cutebot.memegenmanage.bot.blocks.managebot.tools.ManageTools
import io.cutebot.memegenmanage.bot.blocks.managebot.tools.MemeBuilder
import io.cutebot.memegenmanage.bot.blocks.managebot.tools.MemeTextArea
import io.cutebot.memegenmanage.bot.blocks.managebot.tools.generateAreas
import io.cutebot.telegram.bot.block.BotBlock
import io.cutebot.telegram.bot.block.BotTextBlock
import io.cutebot.telegram.bot.model.TextMessage
import io.cutebot.telegram.client.model.keyboard.builder.ReplyKeyboardSimpleBuilder
import io.cutebot.telegram.interaction.model.ChatAnswer
import java.io.File
import java.math.BigDecimal
import java.util.Collections.singletonList

class MemeAddTextAreaBlock(
        private val memeBuilder: MemeBuilder,
        private val tools: ManageTools
): BotTextBlock {

    private var currentMode = MODE.INIT
    private var area = MemeTextArea()

    private var validationMessage = ""

    override fun getAnswer(): ChatAnswer {
        area.normalize()
        var currentMessage = defaultMessage

        if (validationMessage.isNotEmpty()) {
            currentMessage = "<b>$validationMessage</b>\n\n$currentMessage"
        }

        currentMessage += "Area's corners:\n"
        currentMessage += "left / top: " + area.left + "% / " + area.top + "%\n"
        currentMessage += "right / bottom: " + area.right + "% / " + area.bottom + "%\n"

        if (currentMode == MODE.INIT) {
            currentMessage += "\nSelect which side you want to edit.\n"
        } else {
            currentMessage += "\nEnter new value for " + currentMode.side +
                    " side or select another side you want to edit.\n"
        }

        val preview = generateAreas(memeBuilder.filePath, singletonList(area), tools.imageDir)

        return if (memeBuilder.areas.isNotEmpty()) {
            ChatAnswer.photo(File(preview), currentMessage, defaultMarkup)
        } else {
            ChatAnswer.photo(File(preview), currentMessage, firstMarkup)
        }
    }

    override fun handleText(message: TextMessage): BotBlock {
        validationMessage = ""
        val text = message.text
        when {
            text == okReaction -> {
                memeBuilder.areas.add(area)
                return MemeConfirmationBlock(memeBuilder, tools)
            }
            text == backReaction && memeBuilder.areas.isNotEmpty() -> {
                return MemeConfirmationBlock(memeBuilder, tools)
            }
            text == leftReaction -> currentMode = MODE.LEFT
            text == rightReaction -> currentMode = MODE.RIGHT
            text == topReaction -> currentMode = MODE.TOP
            text == bottomReaction -> currentMode = MODE.BOTTOM
        }
        if (currentMode == MODE.INIT || sideReactions.contains(text)) {
            return this
        }

        val value = try {
            BigDecimal(message.text)
        } catch (e: NumberFormatException) {
            validationMessage = "Please, input correct number"
            return this
        }

        when (currentMode) {
            MODE.LEFT -> area.left = value
            MODE.RIGHT -> area.right = value
            MODE.TOP -> area.top = value
            MODE.BOTTOM -> area.bottom = value
            else -> return this
        }
        return this
    }

    companion object {
        private const val defaultMessage =
"""
Setting up coordinates for this text area. This area will be used to put your text and place text on meme template.

"""
        private const val backReaction = "❌ Cancel"
        private const val okReaction = "✅ Create area"
        private const val topReaction = "⬆️"
        private const val bottomReaction = "⬇️"
        private const val leftReaction = "⬅️"
        private const val rightReaction = "➡️"
        private val firstMarkup = ReplyKeyboardSimpleBuilder()
                .addRow(topReaction)
                .addRow(leftReaction, rightReaction)
                .addRow(bottomReaction)
                .oneButton(okReaction)
        private val defaultMarkup = ReplyKeyboardSimpleBuilder()
                .addRow(topReaction)
                .addRow(leftReaction, rightReaction)
                .addRow(bottomReaction)
                .addRow(backReaction, okReaction)
                .build()

        private val sideReactions = listOf(topReaction, leftReaction, rightReaction, bottomReaction)
    }

    enum class MODE(
            val side: String
    ) {
        INIT(""),
        LEFT("left"),
        TOP("top"),
        RIGHT("right"),
        BOTTOM("bottom")
    }
}