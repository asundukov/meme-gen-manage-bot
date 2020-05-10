package io.cutebot.telegram.handlers

import io.cutebot.telegram.exception.TgBotNotFoundException
import org.slf4j.LoggerFactory

class LongPollProcess(
        private val longPollTimeout: Int,
        private val botHandler: TgBotLongPollHandler,
        private val token: String
) : Thread() {
    private var ok = true
    override fun run() {
        var offset = 0
        botHandler.stopWithWebHook(token)
        while (ok) {
            try {
                val tgResponseUpdate = botHandler.getMessages(token, offset, longPollTimeout, 50)
                if (!ok) {
                    return
                }
                for (tgUpdate in tgResponseUpdate.result) {
                    offset = tgUpdate.updateId + 1
                    botHandler.handle(tgUpdate)
                }
                if (interrupted()) {
                    ok = false
                }
            } catch (e: TgBotNotFoundException) {
                log.error("Bot not found via tg request. Dismissed from request pool. Token: {}", token.subSequence(0, 15))
                return
            } catch (e: Exception) {
                log.error(e.message, e)
                try {
                    sleep(2000)
                } catch (e1: InterruptedException) {
                    ok = false
                }
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(LongPollProcess::class.java)
    }
}