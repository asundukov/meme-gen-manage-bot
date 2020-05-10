package io.cutebot.markonimagemanage.service.messagehandlers.process

import io.cutebot.markonimagemanage.botclient.BotClient
import io.cutebot.markonimagemanage.botclient.model.CreateMarkRequest
import io.cutebot.markonimagemanage.botclient.model.MarkPosition
import io.cutebot.markonimagemanage.botclient.model.UpdateBotRequest
import io.cutebot.markonimagemanage.service.messagehandlers.handler.CommandHandler
import io.cutebot.telegram.TelegramService
import io.cutebot.telegram.tgmodel.*
import io.cutebot.telegram.tgmodel.keyboard.TgKeyboardButton
import io.cutebot.telegram.tgmodel.keyboard.TgReplyKeyboardMarkup
import org.apache.commons.io.FileUtils
import java.io.File
import java.math.BigDecimal
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.imageio.ImageIO

class ManageBotProcess(
        private val telegramService: TelegramService,
        botClient: BotClient,
        private val user: TgUser,
        private val chatId: Long,
        botId: Int,
        private val startCommandHandler: CommandHandler,
        imageDir: String
) : BotProcess {

    private var currentStage: Stage = MenuStage()
    private val fieldSet: FieldSet = FieldSet(
            user = user,
            botClient = botClient,
            telegramService = telegramService,
            chatId = chatId,
            botId = botId,
            imageDir = imageDir
    )

    override fun init(): BotProcess {
        telegramService.sendMessage(currentStage.message(fieldSet, ""))
        return this
    }

    override fun handle(tgUpdate: TgUpdate): BotProcess {

        if (tgUpdate.message!!.document != null && currentStage is DocumentHandler) {
            val docHandler = currentStage as DocumentHandler
            currentStage = docHandler.handleDocument(fieldSet, tgUpdate.message.document!!)
            telegramService.sendMessage(currentStage.message(fieldSet, ""))
            return this
        }

        val text = if (tgUpdate.message!!.text != null) {
            tgUpdate.message.text!!
        } else {
            ""
        }
        try {
            currentStage = currentStage.handle(fieldSet, text)
                    ?: return startCommandHandler.handle("", chatId, user)
        } catch (e: Exception) {
            return this //todo
        }
        telegramService.sendMessage(currentStage.message(fieldSet, text))
        return this
    }

    interface Stage {
        fun message(fieldSet: FieldSet, text: String): TgSendTextMessage
        fun handle(fieldSet: FieldSet, text: String): Stage?
    }

    interface DocumentHandler {
        fun handleDocument(fieldSet: FieldSet, document: TgDocument): Stage
    }

    class MenuStage: Stage {

        private val addMarkReaction = "Add mark"
        private val changeTokenReaction = "Change token"
        private val changeNameReaction = "Change name"
        private val backReaction = "\uD83D\uDD19 Back"

        private val addMarkButton = TgKeyboardButton(addMarkReaction)
        private val changeNameButton = TgKeyboardButton(changeNameReaction)
        private val changeTokenButton = TgKeyboardButton(changeTokenReaction)
        private val backButton = TgKeyboardButton(backReaction)

        private val firstRow = listOf(addMarkButton)
        private val secondRow = listOf(changeNameButton, changeTokenButton)
        private val thirdRow = listOf(backButton)

        override fun message(fieldSet: FieldSet, text: String): TgSendTextMessage {

            val bot = fieldSet.botClient.getBot(botId = fieldSet.botId)

            var message = "Your current selected bot: <b>" + bot.title + "</b>\n"

            val marks = fieldSet.botClient.getMarks(bot.botId)

            if (marks.isEmpty()) {
                message += "\nYou need to add at least one mark to do your bot working\n"
            } else {
                message += "\nBot's marks:\n"
                var i = 1
                for (mark in marks) {
                    message += i.toString() + " " + mark.title + " /mmanage_" + mark.id.toString()
                    i++
                }
            }


            val markup =  if (marks.size < 9) {
                TgReplyKeyboardMarkup(listOf(firstRow, secondRow, thirdRow))
            } else {
                message += "\nYou cannot add more than 9 marks to one bot.\n"
                TgReplyKeyboardMarkup(listOf(secondRow, thirdRow))
            }


            return TgSendTextMessage(chatId = fieldSet.chatId, text = message, replyMarkup = markup)
        }

        override fun handle(fieldSet: FieldSet, text: String): Stage? {
            when (text) {
                addMarkReaction -> {
                    return DownloadMarkStage()
                }
                changeNameReaction -> {
                    return ChangeTitleStage()
                }
                changeTokenReaction -> {
                    return ChangeTokenStage()
                }
                backReaction -> {
                    return null
                }
            }
            return this
        }
    }

    class ChangeTokenStage: Stage {
        private val backReaction = "\uD83D\uDD19 Back"
        private val markup = TgReplyKeyboardMarkup(listOf(listOf(TgKeyboardButton(backReaction))))
        private var message = "Insert new token. You can get it at @BotFather"

        override fun message(fieldSet: FieldSet, text: String): TgSendTextMessage {
            return TgSendTextMessage(chatId = fieldSet.chatId, text = message, replyMarkup = markup)
        }

        override fun handle(fieldSet: FieldSet, text: String): Stage? {
            if (text == backReaction) {
                return MenuStage()
            }
            if (text.isEmpty()) {
                return this
            }
            if (text.length > 100) {
                message = "Too long. Please input correct token."
                return this
            }

            val bot = fieldSet.botClient.getBot(fieldSet.botId)
            if (bot.adminUsrId != fieldSet.user.id) {
                throw RuntimeException("Not your bot")
            }

            val updateBotRequest = UpdateBotRequest(token = text, title = bot.title)
            fieldSet.botClient.updateBot(fieldSet.botId, updateBotRequest)

            fieldSet.telegramService.sendMessage(TgSendTextMessage(fieldSet.user.id, "Ok. Thank you."))

            return MenuStage()
        }
    }

    class ChangeTitleStage: Stage {
        private val backReaction = "\uD83D\uDD19 Back"
        private val markup = TgReplyKeyboardMarkup(listOf(listOf(TgKeyboardButton(backReaction))))
        private var message = "Insert new title."

        override fun message(fieldSet: FieldSet, text: String): TgSendTextMessage {
            return TgSendTextMessage(chatId = fieldSet.chatId, text = message, replyMarkup = markup)
        }

        override fun handle(fieldSet: FieldSet, text: String): Stage? {
            if (text == backReaction) {
                return MenuStage()
            }
            if (text.isEmpty()) {
                return this
            }
            if (text.length > 32) {
                message = "Too long (max 32 symbols). Please input correct title."
                return this
            }

            val bot = fieldSet.botClient.getBot(fieldSet.botId)
            if (bot.adminUsrId != fieldSet.user.id) {
                throw RuntimeException("Not your bot")
            }

            val updateBotRequest = UpdateBotRequest(token = null, title = text)
            fieldSet.botClient.updateBot(fieldSet.botId, updateBotRequest)

            fieldSet.telegramService.sendMessage(TgSendTextMessage(fieldSet.user.id, "Ok. Thank you."))

            return MenuStage()
        }
    }

    class DownloadMarkStage: Stage, DocumentHandler {
        private val backReaction = "\uD83D\uDD19 Back"
        private var message = "Send me mark as document.\n" +
                "Supported formats: png, jpg, gif (no animation), bmp, wbmp\n" +
                "Preferred format: png - no quality looses and transparency benefits"
        private val markup = TgReplyKeyboardMarkup(listOf(listOf(TgKeyboardButton(backReaction))))

        override fun message(fieldSet: FieldSet, text: String): TgSendTextMessage {
            return TgSendTextMessage(chatId = fieldSet.chatId, text = message, replyMarkup = markup)
        }

        override fun handle(fieldSet: FieldSet, text: String): Stage? {
            if (text == backReaction) {
                return MenuStage()
            }
            return this
        }

        override fun handleDocument(fieldSet: FieldSet, document: TgDocument): Stage {
            val f = fieldSet.telegramService.getFile(document.fileId)

            val remotePath = fieldSet.telegramService.getDownloadUrl(f.filePath)

            val extension = remotePath.substring(remotePath.lastIndexOf('.') + 1, remotePath.length)

            val filePath = fieldSet.imageDir + "/" + UUID.randomUUID().toString() + "." + extension

            val localFile = File(filePath)

            Files.createDirectories(Paths.get(fieldSet.imageDir))
            FileUtils.copyURLToFile(URL(remotePath), localFile);

            val image = ImageIO.read(localFile)

            if (image == null) {
                fieldSet.telegramService.sendMessage(TgSendTextMessage(fieldSet.chatId, "Can't recognize image"))
                return this
            }

            return SetMarkTitleStage(MarkBuilder(filePath))
        }
    }

    class SetMarkTitleStage(private val markBuilder: MarkBuilder): Stage {
        private val backReaction = "❌ Cancel"
        private val markup = TgReplyKeyboardMarkup(listOf(listOf(TgKeyboardButton(backReaction))))
        private var message = "Set mark title. You will see it in mark list"

        override fun message(fieldSet: FieldSet, text: String): TgSendTextMessage {
            return TgSendTextMessage(chatId = fieldSet.chatId, text = message, replyMarkup = markup)
        }

        override fun handle(fieldSet: FieldSet, text: String): Stage? {
            if (text == backReaction) {
                return MenuStage()
            }
            if (text.isEmpty()) {
                return this
            }
            if (text.length > 32) {
                message = "Too long (max 32 symbols). Please input correct title."
                return this
            }

            markBuilder.title = text

            return SetMarkDescriptionStage(markBuilder)
        }
    }

    class SetMarkDescriptionStage(private val markBuilder: MarkBuilder): Stage {
        private val cancelReaction = "❌ Cancel"
        private val skipReaction = "Skip"
        private val cancelButton = TgKeyboardButton(cancelReaction)
        private val skipButton = TgKeyboardButton(skipReaction)
        private val markup = TgReplyKeyboardMarkup(listOf(listOf(cancelButton, skipButton)))
        private var message = "Set mark description. It will be displayed for your bot's users"

        override fun message(fieldSet: FieldSet, text: String): TgSendTextMessage {
            return TgSendTextMessage(chatId = fieldSet.chatId, text = message, replyMarkup = markup)
        }

        override fun handle(fieldSet: FieldSet, text: String): Stage? {
            if (text == cancelReaction) {
                return MenuStage()
            }
            if (text == skipReaction) {
                return MarkConfirmationStage(markBuilder)
            }
            if (text.isEmpty()) {
                return this
            }
            if (text.length > 512) {
                message = "Too long (max 512 symbols). Please input correct description."
                return this
            }

            markBuilder.description = text

            return  MarkConfirmationStage(markBuilder)
        }
    }

    class MarkConfirmationStage(private val markBuilder: MarkBuilder): Stage {
        private val cancelReaction = "❌ Cancel"
        private val confirmReaction = "✅ Confirm"
        private val cancelButton = TgKeyboardButton(cancelReaction)
        private val confirmButton = TgKeyboardButton(confirmReaction)
        private val markup = TgReplyKeyboardMarkup(listOf(listOf(cancelButton, confirmButton)))
        private var message = "Confirm creation mark\n" +
                "title: <b>" + markBuilder.title + "</b>\n" +
                "description: " + markBuilder.description

        override fun message(fieldSet: FieldSet, text: String): TgSendTextMessage {
            fieldSet.telegramService.sendPhoto(TgSendPhoto(fieldSet.chatId, File(markBuilder.filePath!!)))
            return TgSendTextMessage(chatId = fieldSet.chatId, text = message, replyMarkup = markup)
        }

        override fun handle(fieldSet: FieldSet, text: String): Stage? {
            if (text == cancelReaction) {
                return MenuStage()
            }
            if (text == confirmReaction) {
                fieldSet.botClient.addMark(CreateMarkRequest(
                        botId = fieldSet.botId,
                        imagePath = markBuilder.filePath,
                        description = markBuilder.description,
                        title = markBuilder.title,
                        sizeValue = BigDecimal.valueOf(0.5),
                        position = MarkPosition.LB
                ))
                return MenuStage()
            }

            return this
        }
    }

    data class FieldSet(
            val user: TgUser,
            val botClient: BotClient,
            val telegramService: TelegramService,
            val chatId: Long,
            val botId: Int,
            val imageDir: String
    )

    data class MarkBuilder(
            var filePath: String = "",
            var title: String = "",
            var description: String = ""
    )
}
