package io.github.garykam.sequence.util

import androidx.compose.ui.graphics.Color

enum class MarkerChip(
    val char: String,
    val color: Color? = null
) {
    RED("R", Color(204, 0, 0)),
    GREEN("G", Color(0, 153, 0)),
    BLUE("B", Color(0, 76, 153)),
    PURPLE("P", Color(76, 0, 153)),
    FREE("x"),
    EMPTY(" ");

    companion object {
        fun getChip(char: String): MarkerChip {
            return entries.first { char == it.char }
        }
    }
}
