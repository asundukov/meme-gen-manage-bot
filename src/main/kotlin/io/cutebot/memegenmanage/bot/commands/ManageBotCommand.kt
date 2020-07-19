package io.cutebot.memegenmanage.bot.commands

import io.cutebot.memegenmanage.bot.blocks.StartBlock
import io.cutebot.memegenmanage.bot.blocks.managebot.ManageMenuBlock
import io.cutebot.memegenmanage.bot.blocks.managebot.tools.ManageTools
import io.cutebot.memegenmanage.bot.blocks.managebot.tools.ManageToolsFactory
import io.cutebot.memegenmanage.botclient.BotClient
import io.cutebot.telegram.bot.block.BotBlock
import io.cutebot.telegram.bot.command.Command
import io.cutebot.telegram.bot.model.RawMessage
import org.springframework.stereotype.Service

@Service
class ManageBotCommand(
        private val botClient: BotClient,
        private val manageToolsFactory: ManageToolsFactory
): Command {
    override fun getCommand(): String {
        return "/manage"
    }

    override fun getCommandDescription(): String {
        return ""
    }

    override fun handleCommand(query: String, message: RawMessage): BotBlock {
        val userId = message.from!!.id
        val botId = try{
            query.toInt()
        } catch (e: NumberFormatException) {
            return StartBlock(botClient, userId)
        }

        val bot = botClient.getBot(botId)
        if (bot.adminUsrId != userId) {
            return StartBlock(botClient, userId)
        }

        return ManageMenuBlock(manageToolsFactory.getTools(userId, botId, StartBlock(botClient, userId)))
    }

    override fun isSystemCommand(): Boolean {
        return false
    }

}
