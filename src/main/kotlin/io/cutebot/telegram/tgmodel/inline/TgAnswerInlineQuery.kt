package io.cutebot.telegram.tgmodel.inline

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.ArrayList

@JsonInclude(JsonInclude.Include.NON_NULL)
data class TgAnswerInlineQuery (
        @field: JsonProperty("inline_query_id")
        val inlineQueryId: String? = null,

        @field: JsonProperty
        val results: List<TgInlineQueryResult> = ArrayList(),

        @field: JsonProperty("cache_time")
        val cacheTime: Int = 15,

        @field: JsonProperty("is_personal")
        val isPersonal: Boolean = true,

        @JsonProperty("next_offset")
        val nextOffset: String = "",

        @JsonProperty("switch_pm_text")
        val switchPmText: String? = null,

        @JsonProperty("switch_pm_parameter")
        val switchPmParameter: String? = null
)
