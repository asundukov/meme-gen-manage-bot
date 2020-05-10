package io.cutebot.telegram.handlers

import io.cutebot.telegram.tgmodel.TgUpdate
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
        path = ["/webhook"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
)
class WebHookListener(
        private val botHandleService: TgBotWebHookHandler
) {
    @RequestMapping("/{token}")
    fun update(
            @PathVariable("token") token: String,
            @RequestBody update: TgUpdate
    ): String {
        botHandleService.handle(token, update)
        return "ok"
    }
}
