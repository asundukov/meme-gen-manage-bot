package io.cutebot.memegenmanage.botclient.model

class CreateMemeRequest(
        val botId: Int,

        val imagePath: String,

        val alias: String,

        val textAreaCoords: String
)
