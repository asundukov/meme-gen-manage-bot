package io.cutebot.markonimagemanage.bot.commands

import io.cutebot.markonimagemanage.bot.blocks.StartBlock
import io.cutebot.markonimagemanage.botclient.BotClient
import io.cutebot.telegram.bot.block.BotBlock
import io.cutebot.telegram.bot.block.DoNothingBotBlock.Companion.DO_NOTHING_BOT_BLOCK
import io.cutebot.telegram.bot.command.Command
import io.cutebot.telegram.bot.model.RawMessage
import org.springframework.stereotype.Service

@Service
class StartCommand(
        private val botClient: BotClient
) : Command {

    override fun handleCommand(query: String, message: RawMessage): BotBlock {
        if (message.from == null || message.chat.id != message.from!!.id) {
            return DO_NOTHING_BOT_BLOCK
        }
        return StartBlock(botClient, message.from!!.id)
    }

    override fun getCommand(): String {
        return "/start"
    }

    override fun getCommandDescription(): String {
        return "show start screen"
    }

    override fun isSystemCommand(): Boolean {
        return true
    }
}
