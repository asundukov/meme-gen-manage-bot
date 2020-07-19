package io.cutebot.memegenmanage.bot.blocks.managebot.tools

import io.cutebot.memegenmanage.botclient.model.GetAreaResponse
import java.math.BigDecimal

class MemeTextArea (
        var left: BigDecimal = BigDecimal.valueOf(25),
        var top: BigDecimal = BigDecimal.valueOf(25),
        var right: BigDecimal = BigDecimal.valueOf(75),
        var bottom: BigDecimal = BigDecimal.valueOf(75)
) {
    fun normalize() {
        if (left > right) {
            val tmp = left
            left = right
            right = tmp
        }
        if (top > bottom) {
            val tmp = top
            top = bottom
            bottom = tmp
        }
    }

    fun getAsString(): String {
        return "$left,$top,$right,$bottom"
    }

    companion object {
        fun existed(area: GetAreaResponse): MemeTextArea {
            return MemeTextArea(
                    left = area.left,
                    top = area.top,
                    right = area.right,
                    bottom = area.bottom
            )
        }
    }
}
