package io.cutebot.markonimagemanage.botclient.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import javax.validation.constraints.DecimalMin

class CreateMarkRequest(
        val botId: Int,

        val position: MarkPosition,

        val sizeValue: BigDecimal,

        val imagePath: String,

        val title: String,

        val description: String

)