package io.cutebot.markonimagemanage.service.messagehandlers.process

import io.cutebot.markonimagemanage.botclient.BotClient
import io.cutebot.markonimagemanage.service.messagehandlers.handler.CommandHandler
import io.cutebot.markonimagemanage.service.messagehandlers.handler.model.BotBuilder
import io.cutebot.telegram.TelegramService
import io.cutebot.telegram.tgmodel.TgSendTextMessage
import io.cutebot.telegram.tgmodel.TgUpdate
import io.cutebot.telegram.tgmodel.TgUser
import io.cutebot.telegram.tgmodel.keyboard.TgKeyboardButton
import io.cutebot.telegram.tgmodel.keyboard.TgReplyKeyboardMarkup
import java.lang.Exception
import java.lang.RuntimeException
import java.util.Arrays.asList

class NewBotProcess(
        private val telegramService: TelegramService,
        private val botClient: BotClient,
        private val user: TgUser,
        private val chatId: Long,
        private val cancelHandler: CommandHandler,
        private val manageHandler: CommandHandler
) : BotProcess {

    private val newBot = BotBuilder(user.id)
    private var currentStage: Stage = WaitTitleStage()

    override fun init(): BotProcess {
        telegramService.sendMessage(currentStage.message(newBot, chatId))
        return this
    }

    override fun handle(tgUpdate: TgUpdate): BotProcess {

        val text = if (tgUpdate.message!!.text != null) {
            tgUpdate.message.text!!
        } else {
            ""
        }
        try {
            currentStage = currentStage.handle(newBot, text, botClient) ?: return getManageProcess(tgUpdate)
        } catch (e: Exception) {
            return getCancelProcess(tgUpdate)
        }
        telegramService.sendMessage(currentStage.message(newBot, chatId))
        return this
    }

    private fun getManageProcess(tgUpdate: TgUpdate): BotProcess {
        if (newBot.botId == null) {
            return getCancelProcess(tgUpdate)
        }
        val manageProcess = manageHandler.handle(newBot.botId!!.toString(), chatId, user)
        manageProcess.handle(tgUpdate)
        return manageProcess
    }

    private fun getCancelProcess(tgUpdate: TgUpdate): BotProcess {
        val cancelProcess = cancelHandler.handle("", chatId, user)
        cancelProcess.handle(tgUpdate)
        return cancelProcess
    }

    interface Stage {
        fun message(bot: BotBuilder, chatId: Long): TgSendTextMessage
        fun handle(bot: BotBuilder, text: String, botClient: BotClient): Stage?
    }

    class WaitTitleStage: Stage {
        private var message = "Insert title of new bot. You well see this name in list of your bots\n" +
                "Or /cancel to return to main menu"
        override fun message(bot: BotBuilder, chatId: Long): TgSendTextMessage {
            return TgSendTextMessage(chatId, message)
        }
        override fun handle(bot: BotBuilder, text: String, botClient: BotClient): Stage {
            if (text.isEmpty()) {
                return this
            }
            if (text.length > 32) {
                message = "title too long (max 32 symbols). Please input title"
                return this
            }
            bot.name = text
            if (bot.token == null) {
                return WaitTokenStage()
            } else {
                return WaitCommitStage()
            }
        }

    }

    class WaitTokenStage: Stage {
        private var message = "Input token of your bot\n" +
                "You can get bot token from @BotFather"
        override fun message(bot: BotBuilder, chatId: Long): TgSendTextMessage {
            return TgSendTextMessage(chatId, message)
        }
        override fun handle(bot: BotBuilder, text: String, botClient: BotClient): Stage {
            if (text.isEmpty()) {
                return this
            }
            if (text.length > 100) {
                message = "Token too long. Please input correct token"
                return this
            }

            bot.token = text
            return WaitCommitStage()
        }

    }

    class WaitCommitStage: Stage {
        val okReaction = "✅ Ok"
        val cancelReaction = "❌ Cancel"
        val changeNameReaction = "Change name"
        val changeTokenReaction = "Change token"
        override fun message(bot: BotBuilder, chatId: Long): TgSendTextMessage {
            val maskedToken = bot.token!!.replace(Regex("[a-zA-Z]"), "*")
            val message = "Ok, here is your bot: \n" +
                    "name: " + bot.name + "\n" +
                    "token: " + maskedToken
            val confirmButton = TgKeyboardButton(okReaction)
            val cancelButton = TgKeyboardButton(cancelReaction)
            val changeNameButton = TgKeyboardButton(changeNameReaction)
            val changeTokenButton = TgKeyboardButton(changeTokenReaction)

            val firstRow = asList(confirmButton, cancelButton)
            val secondRow = asList(changeNameButton, changeTokenButton)

            val markup = TgReplyKeyboardMarkup(asList(firstRow, secondRow))
            return TgSendTextMessage(chatId = chatId, text = message, replyMarkup = markup)
        }

        override fun handle(bot: BotBuilder, text: String, botClient: BotClient): Stage? {
            when (text) {
                okReaction -> {
                    val created = botClient.addBot(bot.usrId, bot.token!!, bot.name!!)
                    bot.botId = created.botId
                    return null
                }
                cancelReaction -> {
                    throw RuntimeException("cancel")
                }
                changeNameReaction -> {
                    return WaitTitleStage()
                }
                changeTokenReaction -> {
                    return WaitTokenStage()
                }
            }
            return this
        }

    }
}
