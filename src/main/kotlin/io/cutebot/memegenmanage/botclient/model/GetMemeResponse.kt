package io.cutebot.memegenmanage.botclient.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Calendar

@JsonIgnoreProperties(ignoreUnknown = true)
data class GetMemeResponse(
        @field: JsonProperty
        val id: Int,
        @field: JsonProperty
        val botId: Int,
        @field: JsonProperty
        val createdOn: Calendar,
        @field: JsonProperty
        val totalImages: Int,
        @field: JsonProperty
        val alias: String,
        @field: JsonProperty
        val areas: List<GetAreaResponse>
)
