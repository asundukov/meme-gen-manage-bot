package io.cutebot.markonimagemanage.botclient.model

import com.fasterxml.jackson.annotation.JsonProperty

data class UpdateBotRequest (
        @field: JsonProperty
        private val token: String?,

        @field: JsonProperty
        private val title: String
)
