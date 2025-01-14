package io.github.garykam.sequence.ui.navigation

import kotlinx.serialization.Serializable

sealed class Destination

@Serializable
data object Landing : Destination()

@Serializable
data object JoinGame : Destination()

@Serializable
data object CreateGame : Destination()

@Serializable
data class Game(val lobbyCode: String) : Destination()
