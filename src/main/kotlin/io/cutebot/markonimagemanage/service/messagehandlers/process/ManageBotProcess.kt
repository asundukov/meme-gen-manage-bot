package io.cutebot.markonimagemanage.service.messagehandlers.process

import io.cutebot.markonimagemanage.botclient.BotClient
import io.cutebot.markonimagemanage.botclient.model.CreateMarkRequest
import io.cutebot.markonimagemanage.botclient.model.MarkPosition
import io.cutebot.markonimagemanage.botclient.model.MarkPosition.*
import io.cutebot.markonimagemanage.botclient.model.UpdateBotRequest
import io.cutebot.markonimagemanage.service.messagehandlers.handler.CommandHandler
import io.cutebot.telegram.TelegramService
import io.cutebot.telegram.tgmodel.*
import io.cutebot.telegram.tgmodel.keyboard.TgKeyboardButton
import io.cutebot.telegram.tgmodel.keyboard.TgReplyKeyboardMarkup
import org.apache.commons.io.FileUtils
import java.io.File
import java.lang.NumberFormatException
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
                    message += i.toString() + ". " + mark.title + " /mmanage_" + mark.id.toString() + "\n"
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

            return MarkConfirmationStage(MarkBuilder(filePath))
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
                return MarkConfirmationStage(markBuilder)
            }
            if (text.isEmpty()) {
                return this
            }
            if (text.length > 32) {
                message = "Too long (max 32 symbols). Please input correct title."
                return this
            }

            markBuilder.title = text

            return MarkConfirmationStage(markBuilder)
        }
    }

    class SetMarkDescriptionStage(private val markBuilder: MarkBuilder): Stage {
        private val cancelReaction = "❌ Cancel"
        private val cancelButton = TgKeyboardButton(cancelReaction)
        private val markup = TgReplyKeyboardMarkup(listOf(listOf(cancelButton)))
        private var message = "Set mark description. It will be displayed for your bot's users"

        override fun message(fieldSet: FieldSet, text: String): TgSendTextMessage {
            return TgSendTextMessage(chatId = fieldSet.chatId, text = message, replyMarkup = markup)
        }

        override fun handle(fieldSet: FieldSet, text: String): Stage? {
            if (text == cancelReaction) {
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

    class SetMarkPositionStage(private val markBuilder: MarkBuilder): Stage {
        private val cancelReaction = "❌ Cancel"

        private val lbButton = TgKeyboardButton(LB.desc)
        private val rbButton = TgKeyboardButton(RB.desc)
        private val ltButton = TgKeyboardButton(LT.desc)
        private val rtButton = TgKeyboardButton(RT.desc)

        private val cancelButton = TgKeyboardButton(cancelReaction)

        private val row1 = listOf(ltButton, rtButton)
        private val row2 = listOf(lbButton, rbButton)
        private val row3 = listOf(cancelButton)

        private val markup = TgReplyKeyboardMarkup(listOf(row1, row2, row3))
        private var message = "Select mark description. It is where mark will place on user's images"

        override fun message(fieldSet: FieldSet, text: String): TgSendTextMessage {
            return TgSendTextMessage(chatId = fieldSet.chatId, text = message, replyMarkup = markup)
        }

        override fun handle(fieldSet: FieldSet, text: String): Stage? {
            when (text) {
                cancelReaction -> return MarkConfirmationStage(markBuilder)
                LB.desc -> markBuilder.position = LB
                RB.desc -> markBuilder.position = RB
                LT.desc -> markBuilder.position = LT
                RT.desc -> markBuilder.position = RT
                else -> {
                    return this
                }
            }

            return MarkConfirmationStage(markBuilder)
        }
    }

    class SetSizeStage(private val markBuilder: MarkBuilder): Stage {
        private val cancelReaction = "❌ Cancel"

        private val cancelButton = TgKeyboardButton(cancelReaction)

        private val markup = TgReplyKeyboardMarkup(listOf(listOf(cancelButton)))
        private var message = "Set size in percent (from 1 to 500). It is max ratio of mark side to image side"

        override fun message(fieldSet: FieldSet, text: String): TgSendTextMessage {
            return TgSendTextMessage(chatId = fieldSet.chatId, text = message, replyMarkup = markup)
        }

        override fun handle(fieldSet: FieldSet, text: String): Stage? {
            if (text == cancelReaction) {
                return MarkConfirmationStage(markBuilder)
            }
            try {
                val size = text.trim().toInt()
                if (size < 1) {
                    fieldSet.telegramService.sendMessage(TgSendTextMessage(fieldSet.chatId, "Too small"))
                    return this
                }
                if (size > 500) {
                    fieldSet.telegramService.sendMessage(TgSendTextMessage(fieldSet.chatId, "Too large"))
                    return this
                }
                markBuilder.sizePercent = size
                return MarkConfirmationStage(markBuilder)
            } catch (e: NumberFormatException) {
                fieldSet.telegramService.sendMessage(TgSendTextMessage(fieldSet.chatId, "It isn't int number"))
                return this
            }
        }
    }

    class SetOpacityStage(private val markBuilder: MarkBuilder): Stage {
        private val cancelReaction = "❌ Cancel"

        private val cancelButton = TgKeyboardButton(cancelReaction)

        private val markup = TgReplyKeyboardMarkup(listOf(listOf(cancelButton)))
        private var message = "Set opacity in percent (from 1 to 100)"

        override fun message(fieldSet: FieldSet, text: String): TgSendTextMessage {
            return TgSendTextMessage(chatId = fieldSet.chatId, text = message, replyMarkup = markup)
        }

        override fun handle(fieldSet: FieldSet, text: String): Stage? {
            if (text == cancelReaction) {
                return MarkConfirmationStage(markBuilder)
            }
            try {
                val opacity = text.trim().toInt()
                if (opacity < 1) {
                    fieldSet.telegramService.sendMessage(TgSendTextMessage(fieldSet.chatId, "Too small"))
                    return this
                }
                if (opacity > 100) {
                    fieldSet.telegramService.sendMessage(TgSendTextMessage(fieldSet.chatId, "Too large"))
                    return this
                }
                markBuilder.opacity = opacity
                return MarkConfirmationStage(markBuilder)
            } catch (e: NumberFormatException) {
                fieldSet.telegramService.sendMessage(TgSendTextMessage(fieldSet.chatId, "It isn't int number"))
                return this
            }
        }
    }

    class MarkConfirmationStage(private val markBuilder: MarkBuilder): Stage {
        private val cancelReaction = "❌ Cancel"
        private val confirmReaction = "✅ Confirm"
        private val titleReaction = "⚙️ title"
        private val descriptionReaction = "⚙️ description"
        private val positionReaction = "⚙️ position"
        private val sizeReaction = "⚙️ size"
        private val opacityReaction = "⚙️ opacity"

        private val cancelButton = TgKeyboardButton(cancelReaction)
        private val confirmButton = TgKeyboardButton(confirmReaction)
        private val titleButton = TgKeyboardButton(titleReaction)
        private val descriptionButton = TgKeyboardButton(descriptionReaction)
        private val positionButton = TgKeyboardButton(positionReaction)
        private val sizeButton = TgKeyboardButton(sizeReaction)
        private val opacityButton = TgKeyboardButton(opacityReaction)

        private val row1 = listOf(titleButton, descriptionButton)
        private val row2 = listOf(positionButton, sizeButton, opacityButton)
        private val row3 = listOf(cancelButton, confirmButton)
        private val markup = TgReplyKeyboardMarkup(listOf(row1, row2, row3))
        private var message = "Confirm creation mark\n" +
                "title: <b>" + markBuilder.title + "</b>\n" +
                "description: " + markBuilder.description + "\n" +
                "position: " + markBuilder.position.desc + "\n" +
                "size: " + markBuilder.sizePercent + "%\n" +
                "opacity: " + markBuilder.opacity + "%"

        override fun message(fieldSet: FieldSet, text: String): TgSendTextMessage {
            fieldSet.telegramService.sendPhoto(TgSendPhoto(fieldSet.chatId, File(markBuilder.filePath)))
            return TgSendTextMessage(chatId = fieldSet.chatId, text = message, replyMarkup = markup)
        }

        override fun handle(fieldSet: FieldSet, text: String): Stage? {
            when (text) {
                confirmReaction -> {
                    fieldSet.botClient.addMark(CreateMarkRequest(
                            botId = fieldSet.botId,
                            imagePath = markBuilder.filePath,
                            description = markBuilder.description,
                            title = markBuilder.title,
                            sizeValue = markBuilder.sizePercent.div(100.0).toBigDecimal(),
                            position = markBuilder.position,
                            opacity = markBuilder.opacity.div(100.0).toBigDecimal()
                    ))
                    return MenuStage()
                }
                cancelReaction -> return MenuStage()
                titleReaction -> return SetMarkTitleStage(markBuilder)
                descriptionReaction -> return SetMarkDescriptionStage(markBuilder)
                positionReaction -> return SetMarkPositionStage(markBuilder)
                sizeReaction -> return SetSizeStage(markBuilder)
                opacityReaction -> return SetOpacityStage(markBuilder)
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
            var filePath: String,
            var title: String = "",
            var description: String = "",
            var sizePercent: Int = 50,
            var opacity: Int = 100,
            var position: MarkPosition = LB
    )
}
