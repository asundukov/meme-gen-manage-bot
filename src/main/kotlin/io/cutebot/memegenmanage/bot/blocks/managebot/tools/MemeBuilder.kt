package io.cutebot.memegenmanage.bot.blocks.managebot.tools

class MemeBuilder(
        var filePath: String,
        var memeId: Int? = null,
        var alias: String = "",
        var areas: ArrayList<MemeTextArea> = ArrayList()
) {
    fun getAreasAsString(): String {
        return areas.map { it.getAsString() }.joinToString(",")
    }
}
