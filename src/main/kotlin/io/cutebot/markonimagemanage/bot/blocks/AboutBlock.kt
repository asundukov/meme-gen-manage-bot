package io.cutebot.markonimagemanage.bot.blocks

import io.cutebot.markonimagemanage.botclient.BotClient
import io.cutebot.telegram.bot.block.BotBlock
import io.cutebot.telegram.bot.block.BotTextBlock
import io.cutebot.telegram.bot.model.TextMessage
import io.cutebot.telegram.interaction.model.ChatAnswer
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class AboutBlock(
        @Value("\${bot.contact}")
        private val tgContact: String,
        @Value("\${bot.twitter}")
        private val twitterContact: String,

        private val botClient: BotClient
): BotTextBlock {
    val message =
            """
<b>About</b>

This bot is a bot-client for open-source project
<a href="https://github.com/asundukov/mark-on-image-bot">mark-on-image-bot</a>
Contact: @$tgContact
Twitter: <a href="https://twitter.com/$twitterContact">@$twitterContact</a>
"""

    override fun getAnswer(): ChatAnswer {
        return ChatAnswer.text(message)
    }

    override fun handleText(message: TextMessage): BotBlock {
        return StartBlock(botClient, message.user!!.id)
    }
}
