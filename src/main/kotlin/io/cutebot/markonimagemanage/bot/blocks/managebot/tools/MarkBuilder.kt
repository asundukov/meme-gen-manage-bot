package io.cutebot.markonimagemanage.bot.blocks.managebot.tools

import io.cutebot.markonimagemanage.botclient.model.MarkPosition

data class MarkBuilder(
        var filePath: String,
        var title: String = "",
        var description: String = "",
        var sizePercent: Int = 50,
        var opacity: Int = 100,
        var position: MarkPosition = MarkPosition.LB
)
