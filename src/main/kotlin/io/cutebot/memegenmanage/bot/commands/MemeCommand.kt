package io.cutebot.memegenmanage.bot.commands

import io.cutebot.memegenmanage.bot.blocks.StartBlock
import io.cutebot.memegenmanage.bot.blocks.managebot.ManageMenuBlock
import io.cutebot.memegenmanage.bot.blocks.managebot.MemeConfirmationBlock
import io.cutebot.memegenmanage.bot.blocks.managebot.tools.ManageToolsFactory
import io.cutebot.memegenmanage.bot.blocks.managebot.tools.MemeBuilder
import io.cutebot.memegenmanage.bot.blocks.managebot.tools.MemeTextArea
import io.cutebot.memegenmanage.bot.blocks.newbot.BotBuilder
import io.cutebot.memegenmanage.bot.blocks.newbot.SetTitleBlock
import io.cutebot.memegenmanage.botclient.BotClient
import io.cutebot.telegram.bot.block.BotBlock
import io.cutebot.telegram.bot.command.Command
import io.cutebot.telegram.bot.model.RawMessage
import org.apache.commons.io.FileUtils
import org.springframework.stereotype.Service
import java.io.File
import java.lang.Exception
import java.lang.NumberFormatException
import java.net.URL
import java.util.UUID

@Service
class MemeCommand(
        private val botClient: BotClient,
        private val manageToolsFactory: ManageToolsFactory
): Command {

    override fun getCommand(): String {
        return "/meme"
    }

    override fun getCommandDescription(): String {
        return ""
    }

    override fun handleCommand(query: String, message: RawMessage): BotBlock {
        val userId = message.from!!.id
        val startBlock = StartBlock(botClient, userId)
        val memeId = try {
            query.trim().toInt()
        } catch (e: NumberFormatException) {
            return startBlock
        }
        val meme = try {
            botClient.getMeme(memeId)
        } catch (e: Exception) {
            return startBlock
        }

        val bot = try {
            botClient.getBot(meme.botId)
        } catch (e: Exception) {
            return startBlock
        }

        if (bot.adminUsrId != userId) {
            return startBlock
        }

        val startTools = manageToolsFactory.getTools(userId, meme.botId, startBlock)
        val tools = manageToolsFactory.getTools(userId, meme.botId, ManageMenuBlock(startTools))

        val filePath = tools.imageDir + "/" + UUID.randomUUID() + ".jpg"
        FileUtils.copyURLToFile(URL(botClient.getMemeImageUrl(memeId)), File(filePath))

        val memeBuilder = MemeBuilder(
                filePath = filePath,
                memeId = memeId,
                alias = meme.alias,
                areas = ArrayList(meme.areas.map { MemeTextArea.existed(it) })
        )

        return MemeConfirmationBlock(memeBuilder, tools)
    }

    override fun isSystemCommand(): Boolean {
        return false
    }
}
