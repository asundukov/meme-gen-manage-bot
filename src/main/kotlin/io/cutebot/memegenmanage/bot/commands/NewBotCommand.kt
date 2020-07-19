package io.cutebot.memegenmanage.bot.commands

import io.cutebot.memegenmanage.bot.blocks.StartBlock
import io.cutebot.memegenmanage.bot.blocks.newbot.BotBuilder
import io.cutebot.memegenmanage.bot.blocks.newbot.SetTitleBlock
import io.cutebot.memegenmanage.botclient.BotClient
import io.cutebot.telegram.bot.block.BotBlock
import io.cutebot.telegram.bot.command.Command
import io.cutebot.telegram.bot.model.RawMessage
import org.springframework.stereotype.Service

@Service
class NewBotCommand(
        private val botClient: BotClient
): Command {

    override fun getCommand(): String {
        return "/newbot"
    }

    override fun getCommandDescription(): String {
        return "Create new bot"
    }

    override fun handleCommand(query: String, message: RawMessage): BotBlock {
        val startBlock = StartBlock(botClient, message.from!!.id)
        return SetTitleBlock(BotBuilder(message.from!!.id), botClient, startBlock)
    }

    override fun isSystemCommand(): Boolean {
        return true
    }
}
