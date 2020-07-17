package io.cutebot.markonimagemanage.bot.blocks

import io.cutebot.markonimagemanage.botclient.BotClient
import io.cutebot.telegram.bot.block.BotBlock
import io.cutebot.telegram.bot.block.BotTextBlock
import io.cutebot.telegram.bot.model.TextMessage
import io.cutebot.telegram.interaction.model.ChatAnswer

class StartBlock(
        private val botClient: BotClient,
        private val userId: Long
): BotTextBlock {

    override fun getAnswer(): ChatAnswer {
        val bots = botClient.getBots(userId)

        var message = welcomeMessage

        if (bots.isEmpty()) {
            message += "Currently you dont have any bots.\n"
        } else {
            message += "\nYour bots:\n"
            var i = 1
            bots.forEach {
                message += i.toString() + ". <b>" + it.title + "</b> " +
                        "@" + it.username + " /manage_" + it.botId + "\n"
                i++
            }
        }

        if (bots.size >= 9) {
            message += "\nYou already have 9 bots. You cannot add more to this account"
        } else {
            message += "\nYou can add new mark-on-image-bot /newbot"
        }

        return ChatAnswer.text(message)
    }

    override fun handleText(message: TextMessage): BotBlock {
        return this
    }

    companion object {
        private const val welcomeMessage =
"""
<b>Welcome!</b>

I can help create your own bot like:
@StoriesTellerBot - Russian 2020 election no campaign
@RickAndMortyPicBot - Rick&Morty fun bot
@watermarkTestBot - place watermark example
You can invite your team to make avatars or other labeled media for your campaigns
"""

    }
}
