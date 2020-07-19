package io.cutebot.memegenmanage.botclient.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

class GetAreaResponse(
        @field: JsonProperty
        val left: BigDecimal,

        @field: JsonProperty
        val top: BigDecimal,

        @field: JsonProperty
        val right: BigDecimal,

        @field: JsonProperty
        val bottom: BigDecimal
)
