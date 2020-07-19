package io.cutebot.memegenmanage.bot.blocks.managebot

import io.cutebot.memegenmanage.bot.blocks.managebot.tools.ManageTools
import io.cutebot.memegenmanage.bot.blocks.managebot.tools.MemeBuilder
import io.cutebot.telegram.bot.block.BotBlock
import io.cutebot.telegram.bot.model.DocumentMessage
import io.cutebot.telegram.bot.model.PhotoMessage
import io.cutebot.telegram.bot.model.TextMessage
import io.cutebot.telegram.client.model.keyboard.builder.ReplyKeyboardSimpleBuilder
import io.cutebot.telegram.interaction.model.ChatAnswer
import org.apache.commons.io.FileUtils
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.imageio.ImageIO

internal class MemeDownloadBlock(
        private val tools: ManageTools
): BotBlock {
    private var currentMessage = "Send me meme as document.\n" +
            "Supported formats: png, jpg, gif (no animation), bmp, wbmp\n"

    override fun getAnswer(): ChatAnswer {
        return ChatAnswer.text(currentMessage, defaultKeyboard)
    }

    override fun handleDocument(message: DocumentMessage): BotBlock {
        val remotePath = message.document.getRemotePath()
        val extension = remotePath.substring(remotePath.lastIndexOf('.') + 1, remotePath.length)
        val filePath = tools.imageDir + "/" + UUID.randomUUID().toString() + "." + extension
        val localFile = File(filePath)

        Files.createDirectories(Paths.get(tools.imageDir))
        FileUtils.copyURLToFile(URL(remotePath), localFile)

        val image = ImageIO.read(localFile)
        if (image == null) {
            currentMessage = "Can't recognize image"
            return this
        }

        return MemeSetAliasBlock(MemeBuilder(filePath), tools)
    }

    override fun handlePhoto(message: PhotoMessage): BotBlock {
        currentMessage = onPhotoReaction
        return this
    }

    override fun handleText(message: TextMessage): BotBlock {
        if (message.text == backReaction) {
            return ManageMenuBlock(tools)
        }
        currentMessage = onTextReaction
        return this
    }

    companion object {
        private const val backReaction = "\uD83D\uDD19 Back"
        private const val onTextReaction = "Please, send me file or press '$backReaction'"
        private const val onPhotoReaction = "Please, send me meme as file"
        private val defaultKeyboard = ReplyKeyboardSimpleBuilder().oneButton(backReaction)
    }
}
