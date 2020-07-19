package io.cutebot.memegenmanage.botclient.model

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.Min

data class CreateBotRequest (
        @field: JsonProperty
        private val token: String,

        @field: JsonProperty
        @field: Min(1)
        private val adminUsrId: Long,

        @field: JsonProperty
        private val title: String

)