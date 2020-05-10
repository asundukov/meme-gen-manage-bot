package io.cutebot.markonimagemanage.botclient.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.util.Calendar

data class GetMarkResponse(
        @field: JsonProperty
        val id: Int,
        @field: JsonProperty
        val botId: Int,
        @field: JsonProperty
        val createdOn: Calendar,
        @field: JsonProperty
        val totalImages: Int,
        @field: JsonProperty
        val sizeValue: BigDecimal,
        @field: JsonProperty
        val title: String,
        @field: JsonProperty
        val description: String

)
