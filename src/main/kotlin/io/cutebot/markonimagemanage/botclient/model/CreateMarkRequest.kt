package io.cutebot.markonimagemanage.botclient.model

import java.math.BigDecimal

class CreateMarkRequest(
        val botId: Int,

        val position: MarkPosition,

        val sizeValue: BigDecimal,

        val imagePath: String,

        val title: String,

        val description: String,

        val opacity: BigDecimal

)