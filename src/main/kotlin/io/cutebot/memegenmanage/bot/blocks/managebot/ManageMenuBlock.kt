package io.cutebot.memegenmanage.bot.blocks.managebot

import io.cutebot.memegenmanage.bot.blocks.managebot.tools.ManageTools
import io.cutebot.telegram.bot.block.BotBlock
import io.cutebot.telegram.bot.block.BotTextBlock
import io.cutebot.telegram.bot.model.TextMessage
import io.cutebot.telegram.client.model.keyboard.builder.ReplyKeyboardSimpleBuilder
import io.cutebot.telegram.interaction.model.ChatAnswer

class ManageMenuBlock(
        private val tools: ManageTools
): BotTextBlock {

    override fun getAnswer(): ChatAnswer {
        val bot = tools.botClient.getBot(botId = tools.selectedBotId)
        var message = "Selected bot: <b>" + bot.title + "</b> - " + "@" + bot.username + "\n\n"
        val memes = tools.botClient.getMemes(bot.botId)

        if (memes.isEmpty()) {
            message += "\nYou need to add at least one meme to do your bot working\n"
        } else {
            message += "\nBot's memes:\n"
            var i = 1
            memes.forEach {
                message += i.toString() + ". " + it.alias + " /meme_" + it.id.toString() + "\n"
                i++
            }
        }

        val keyboard = ReplyKeyboardSimpleBuilder()
        if (memes.size < 50) {
            keyboard.addRow(addMemeReaction)
        } else {
            message += "\nYou cannot add more than 50 memes to one bot.\n"
        }
        keyboard.addRow(changeNameReaction, changeTokenReaction)
                .addRow(backReaction)

        return ChatAnswer.text(message, keyboard.build())
    }

    override fun handleText(message: TextMessage): BotBlock {
        when (message.text) {
            addMemeReaction -> return MemeDownloadBlock(tools)
            changeNameReaction -> return ChangeTitleBlock(tools)
            changeTokenReaction -> return ChangeTokenBlock(tools)
            backReaction -> return tools.returnBlock
        }
        return this
    }

    companion object {
        private const val addMemeReaction = "Add meme"
        private const val changeTokenReaction = "Change token"
        private const val changeNameReaction = "Change name"
        private const val backReaction = "\uD83D\uDD19 Back"
    }
}
