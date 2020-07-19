package io.cutebot.memegenmanage.bot.blocks.managebot

import io.cutebot.memegenmanage.bot.blocks.managebot.tools.ManageTools
import io.cutebot.memegenmanage.bot.blocks.managebot.tools.MemeBuilder
import io.cutebot.memegenmanage.bot.blocks.managebot.tools.generateAreas
import io.cutebot.memegenmanage.botclient.model.CreateMemeRequest
import io.cutebot.memegenmanage.botclient.model.UpdateMemeRequest
import io.cutebot.telegram.bot.block.BotBlock
import io.cutebot.telegram.bot.block.BotTextBlock
import io.cutebot.telegram.bot.model.TextMessage
import io.cutebot.telegram.client.model.keyboard.builder.ReplyKeyboardSimpleBuilder
import io.cutebot.telegram.interaction.model.ChatAnswer
import java.io.File

class MemeConfirmationBlock(
        private val memeBuilder: MemeBuilder,
        private val tools: ManageTools
): BotTextBlock {

    private val currentMessage = "Confirm creation meme\n" +
            "alias: " + memeBuilder.alias + " \n" +
            "<b>" + memeBuilder.areas.size + "</b> text areas"

    override fun getAnswer(): ChatAnswer {
        val thumb = generateAreas(memeBuilder.filePath, memeBuilder.areas, tools.imageDir)
        val markup = if (memeBuilder.memeId == null) createMarkup else editMarkup
        return ChatAnswer.photo(File(thumb), currentMessage, markup)
    }

    override fun handleText(message: TextMessage): BotBlock {
        when (message.text) {
            confirmReaction -> {
                if (memeBuilder.memeId == null) {
                    tools.botClient.addMeme(CreateMemeRequest(
                            botId = tools.selectedBotId,
                            imagePath = memeBuilder.filePath,
                            alias = memeBuilder.alias,
                            textAreaCoords = memeBuilder.getAreasAsString()
                    ))
                } else {
                    val request = UpdateMemeRequest(
                            alias = memeBuilder.alias,
                            textAreaCoords = memeBuilder.getAreasAsString());
                    tools.botClient.updateMeme(request, memeBuilder.memeId!!)
                }
                return tools.returnBlock
            }
            deleteReaction -> {
                if (memeBuilder.memeId != null) {
                    tools.botClient.deleteMeme(memeBuilder.memeId!!)
                    return tools.returnBlock
                }
                return this
            }
            createAreaReaction -> return MemeAddTextAreaBlock(memeBuilder, tools)
            cancelReaction -> return tools.returnBlock
            aliasReaction -> return MemeSetAliasBlock(memeBuilder, tools)
        }

        return this
    }

    companion object {
        private const val cancelReaction = "❌ Cancel"
        private const val deleteReaction = "\uD83D\uDDD1 Delete"
        private const val confirmReaction = "✅ Confirm"
        private const val aliasReaction = "Edit alias"
        private const val createAreaReaction = "\uD83D\uDCAC Create area"

        private val createMarkup = ReplyKeyboardSimpleBuilder()
                .addRow(createAreaReaction, aliasReaction)
                .addRow(cancelReaction, confirmReaction)
                .build()

        private val editMarkup = ReplyKeyboardSimpleBuilder()
                .addRow(createAreaReaction, aliasReaction)
                .addRow(cancelReaction, deleteReaction, confirmReaction)
                .build()

    }
}
