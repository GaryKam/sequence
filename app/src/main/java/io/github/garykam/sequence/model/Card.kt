package io.github.garykam.sequence.model

data class Card(
    val name: String,
    val drawableId: Int = -1,
    val state: String = ""
)
