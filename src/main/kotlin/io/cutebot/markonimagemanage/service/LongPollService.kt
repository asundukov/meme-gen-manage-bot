package io.cutebot.markonimagemanage.service

import io.cutebot.telegram.handlers.LongPollProcess
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class LongPollService(
        private val botHandleService: BotHandleService,

        @Value("\${telegram.longpoll.enable}")
        private val longPollEnable: Boolean,

        @Value("\${telegram.longpoll.timeout}")
        private val longPollTimeout: Int,

        @Value("\${bot.token}")
        private val token: String

) {
    @PostConstruct
    fun init() {
        startLongPoll(token)
    }

    fun startLongPoll(token: String) {
        if (longPollEnable) {
            LongPollProcess(longPollTimeout, botHandleService, token).start()
        }
    }

}