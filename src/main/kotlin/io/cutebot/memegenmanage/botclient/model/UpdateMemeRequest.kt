package io.cutebot.memegenmanage.botclient.model

import com.fasterxml.jackson.annotation.JsonProperty

class UpdateMemeRequest(
        @field: JsonProperty
        val alias: String,

        @field: JsonProperty
        val textAreaCoords: String
)
