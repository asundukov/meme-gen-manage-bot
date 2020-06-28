package io.cutebot.markonimagemanage.service

import io.cutebot.markonimagemanage.service.messagehandlers.handler.AboutCommandHandler
import io.cutebot.markonimagemanage.service.messagehandlers.handler.CommandHandler
import io.cutebot.markonimagemanage.service.messagehandlers.handler.ManageBotCommandHandler
import io.cutebot.markonimagemanage.service.messagehandlers.handler.NewBotCommandHandler
import io.cutebot.markonimagemanage.service.messagehandlers.handler.StartCommandHandler
import io.cutebot.markonimagemanage.service.messagehandlers.handler.UnknownCommandHandler
import io.cutebot.markonimagemanage.service.messagehandlers.process.BotProcess
import io.cutebot.telegram.TelegramService
import io.cutebot.telegram.handlers.TgBotLongPollHandler
import io.cutebot.telegram.handlers.TgBotWebHookHandler
import io.cutebot.telegram.tgmodel.TgBotCommand
import io.cutebot.telegram.tgmodel.TgBotCommands
import io.cutebot.telegram.tgmodel.TgResponseUpdate
import io.cutebot.telegram.tgmodel.TgUpdate
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kotlin.math.min

@Service
class BotHandleService(
        private val telegramService: TelegramService,
        private val unknownMessageHandler: UnknownCommandHandler,
        startHandler: StartCommandHandler,
        newBotHandler: NewBotCommandHandler,
        manageHandler: ManageBotCommandHandler,
        aboutHandler: AboutCommandHandler

): TgBotWebHookHandler, TgBotLongPollHandler {

    private val userProcesses: HashMap<Long, BotProcess> = HashMap()

    private val messagesMap: Map<String, CommandHandler> = mapOf(
            "/start" to startHandler,
            "/mybots" to startHandler,
            "/cancel" to startHandler,
            "/about" to aboutHandler,
            "/newbot" to newBotHandler,
            "/manage" to manageHandler
    )

    override fun handle(token: String, update: TgUpdate) {
        return handle(token, update)
    }

    override fun handle(update: TgUpdate) {
        if (update.message != null) {
            val chatId = update.message.chat.id
            val usr = update.message.from!!
            if (update.message.text != null) {
                val (command, params) = extractCommand(update.message.text)
                val handler = messagesMap[command]

                if (handler != null) {
                    val process = handler.handle(params, chatId, usr)
                    userProcesses[usr.id] = process
                    return
                }
            }

            val process = userProcesses[usr.id] ?: unknownMessageHandler.handle("", chatId, usr)

            userProcesses[usr.id] = process.handle(update)

        }
    }

    private fun extractCommand(text: String): CommandWithParams {
        val txt = text.trim()
        var spaceIndex = txt.indexOf(" ")
        var underscopeIndex = txt.indexOf("_")

        if (spaceIndex == -1) {
            spaceIndex = txt.length
        }

        if (underscopeIndex == -1) {
            underscopeIndex = txt.length
        }

        val minIndex = min(spaceIndex, underscopeIndex)

        val command = txt.subSequence(0, minIndex).toString()
        var params = txt.subSequence(minIndex, txt.length).toString().trim()

        if (params.isNotEmpty() && params[0] == '_') {
            params = params.substring(1, params.length)
        }

        return CommandWithParams(command, params)
    }

    override fun getMessages(botToken: String, offset: Int, timeout: Int, limit: Int): TgResponseUpdate {
        return telegramService.getUpdates(offset, limit, timeout)
    }

    override fun stopWithWebHook(botToken: String) {
        telegramService.deleteWebhook()
    }

    fun startWithWebHook(botToken: String) {
        telegramService.setWebHook()
    }

    fun setCommands() {
        val commands = ArrayList<TgBotCommand>()
        commands.add(TgBotCommand("mybots", "Show bots"))
        commands.add(TgBotCommand("newbot", "Add new  bot"))
        commands.add(TgBotCommand("about", "About info"))

        telegramService.setCommands(TgBotCommands(commands))
    }

    companion object {
        private val log = LoggerFactory.getLogger(BotHandleService::class.java)
        private val UNKNOWN_MESSAGE = "unknown";
    }

    data class CommandWithParams(
            val command: String,
            val params: String
    )

}