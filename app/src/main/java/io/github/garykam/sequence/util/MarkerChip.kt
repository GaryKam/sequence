package io.github.garykam.sequence.util

import androidx.compose.ui.graphics.Color

enum class MarkerChip(
    val shortName: String,
    val color: Color
) {
    RED("R", Color(255, 71, 91)),
    GREEN("G", Color(34, 139, 34)),
    BLUE("B", Color(25, 116, 210)),
    PURPLE("P", Color(128, 0, 128))
}
