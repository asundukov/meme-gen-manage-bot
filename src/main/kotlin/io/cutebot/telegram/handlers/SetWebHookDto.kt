package io.cutebot.telegram.handlers

class SetWebHookDto(
        val url: String
) {

    override fun toString(): String {
        return "SetWebhookDto{" +
                "url='" + url + '\'' +
                '}'
    }
}