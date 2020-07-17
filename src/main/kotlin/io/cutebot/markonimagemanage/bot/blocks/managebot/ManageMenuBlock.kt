package io.cutebot.markonimagemanage.bot.blocks.managebot

import io.cutebot.markonimagemanage.bot.blocks.managebot.tools.ManageTools
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
        val marks = tools.botClient.getMarks(bot.botId)

        if (marks.isEmpty()) {
            message += "\nYou need to add at least one mark to do your bot working\n"
        } else {
            message += "\nBot's marks:\n"
            var i = 1
            marks.forEach {
                message += i.toString() + ". " + it.title + " /mmanage_" + it.id.toString() + "\n"
                i++
            }
        }

        val keyboard = ReplyKeyboardSimpleBuilder()
        if (marks.size < 9) {
            keyboard.addRow(addMarkReaction)
        } else {
            message += "\nYou cannot add more than 9 marks to one bot.\n"
        }
        keyboard.addRow(changeNameReaction, changeTokenReaction)
                .addRow(backReaction)

        return ChatAnswer.textWithReplyKeyboard(message, keyboard.build())
    }

    override fun handleText(message: TextMessage): BotBlock {
        when (message.text) {
            addMarkReaction -> return MarkDownloadBlock(tools)
            changeNameReaction -> return ChangeTitleBlock(tools)
            changeTokenReaction -> return ChangeTokenBlock(tools)
            backReaction -> return tools.returnBlock
        }
        return this
    }

    companion object {
        private const val addMarkReaction = "Add mark"
        private const val changeTokenReaction = "Change token"
        private const val changeNameReaction = "Change name"
        private const val backReaction = "\uD83D\uDD19 Back"
    }
}
