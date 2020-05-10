package io.cutebot.markonimagemanage.botclient.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Calendar

data class GetBotResponse(

        @field: JsonProperty
        val botId: Int,

        @field: JsonProperty
        val totalImages: Int,

        @field: JsonProperty
        val createdOn: Calendar,

        @field: JsonProperty
        val title: String,

        @field: JsonProperty
        val adminUsrId: Long
)
