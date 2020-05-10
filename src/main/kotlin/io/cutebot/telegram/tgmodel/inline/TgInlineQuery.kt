package io.cutebot.telegram.tgmodel.inline

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.cutebot.telegram.tgmodel.TgUser


@JsonIgnoreProperties(ignoreUnknown = true)
data class TgInlineQuery (
        @field: JsonProperty
        val id: String,
        @field: JsonProperty
        val from: TgUser,
        @field: JsonProperty
        val query: String,
        @field: JsonProperty
        val offset: String
)
