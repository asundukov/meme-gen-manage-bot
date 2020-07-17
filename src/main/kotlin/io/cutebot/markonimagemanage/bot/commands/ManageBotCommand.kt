package io.cutebot.markonimagemanage.bot.commands

import io.cutebot.markonimagemanage.bot.blocks.StartBlock
import io.cutebot.markonimagemanage.bot.blocks.managebot.ManageMenuBlock
import io.cutebot.markonimagemanage.bot.blocks.managebot.tools.ManageTools
import io.cutebot.markonimagemanage.botclient.BotClient
import io.cutebot.telegram.bot.block.BotBlock
import io.cutebot.telegram.bot.command.Command
import io.cutebot.telegram.bot.model.RawMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.lang.NumberFormatException

@Service
class ManageBotCommand(
        private val botClient: BotClient,

        @Value("\${image.dir}")
        private val imageDir: String
): Command {
    override fun getCommand(): String {
        return "/manage"
    }

    override fun getCommandDescription(): String {
        return ""
    }

    override fun handleCommand(query: String, message: RawMessage): BotBlock {
        val botId = try{
            query.toInt()
        } catch (e: NumberFormatException) {
            return StartBlock(botClient, message.from!!.id)
        }

        val bot = botClient.getBot(botId)
        if (bot.adminUsrId != message.from!!.id) {
            return StartBlock(botClient, message.from!!.id)
        }

        val tools = ManageTools(
                userId = message.from!!.id,
                botClient = botClient,
                selectedBotId = botId,
                imageDir = imageDir,
                returnBlock = StartBlock(botClient, message.from!!.id)
        )
        return ManageMenuBlock(tools)
    }

    override fun isSystemCommand(): Boolean {
        return false
    }

}
