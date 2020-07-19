package io.cutebot.memegenmanage.bot.blocks.managebot.tools

import io.cutebot.memegenmanage.bot.blocks.managebot.tools.ImageGenerator.Companion.defaultColors
import io.cutebot.memegenmanage.bot.blocks.managebot.tools.ImageGenerator.Companion.m
import java.awt.Color
import java.io.File
import java.math.BigDecimal
import java.util.UUID
import javax.imageio.ImageIO
import kotlin.random.Random

fun generateAreas(filePath: String, areas: List<MemeTextArea>, imgDir: String): String {
    val img = ImageIO.read(File(filePath))

    val g = img.createGraphics()

    var i = 0

    areas.forEach {
        val width = ((it.right - it.left) * img.width.toBigDecimal().multiply(m)).toInt()
        val height = ((it.bottom - it.top) * img.height.toBigDecimal()).multiply(m).toInt()
        val left = (it.left * img.width.toBigDecimal()).multiply(m).toInt()
        val top = (it.top * img.height.toBigDecimal()).multiply(m).toInt()
        g.color = if (i < defaultColors.size) {
            defaultColors[i]
        } else {
            Color(Random.nextInt(1, 255), Random.nextInt(1, 255), Random.nextInt(1, 255), 200)
        }

        g.fillRect(left,top, width, height)
        i++
    }
    g.dispose()

    val tmpName = imgDir + "/" + UUID.randomUUID().toString() + ".png"
    val tmpFile = File(tmpName)

    ImageIO.write(img, "PNG", tmpFile)
    return tmpName
}

class ImageGenerator {
    companion object {
        val m = BigDecimal.valueOf(0.01)

        val defaultColors = listOf(
                Color(143,254,9, 200),
                Color(143,9,254, 200),
                Color(254,143,9, 200),
                Color(254, 9, 143, 200),
                Color(9, 143, 254, 200),
                Color(9, 254, 143, 200)
        )
    }
}
