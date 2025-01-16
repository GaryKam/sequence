package io.github.garykam.sequence.database

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.github.garykam.sequence.util.Game
import io.github.garykam.sequence.util.Host
import kotlinx.coroutines.tasks.await

object Database {
    val gameRef: DatabaseReference
        get() = firebase.getReference("games").child(_lobbyCode)

    var userRole = ""
        private set

    var userColor = ""
        private set

    private val firebase = Firebase.database

    private var _lobbyCode = ""

    fun createLobby(
        lobbyCode: String,
        hostColor: String
    ) {
        _lobbyCode = lobbyCode
        userRole = "host"
        userColor = hostColor
        gameRef.setValue(Game(Host(hostColor)))
    }

    fun closeLobby() {
        gameRef.removeValue()
    }

    suspend fun findLobby(lobbyCode: String): Boolean {
        val lobbyExists = firebase.getReference("games").child(lobbyCode)
            .get()
            .await()
            .exists()
        if (lobbyExists) {
            _lobbyCode = lobbyCode
            userRole = "guest"
        }
        return lobbyExists
    }

    fun joinLobby(
        guestColor: String
    ) {
        userColor = guestColor
        gameRef.child("guest/color").setValue(guestColor)
    }

    fun leaveLobby() {
        gameRef.child("guest").removeValue()
    }

    fun startGame() {
        gameRef.child("turn").setValue(if ((0..1).random() == 0) "host" else "guest")
    }

    fun setupDeckAndHands(cards: List<String>) {
        if (userRole != "host") {
            return
        }

        val deck = cards.toMutableList()
        val hostHand = mutableListOf<String>()
        val guestHand = mutableListOf<String>()

        repeat(7) {
            var card = deck.random()
            hostHand.add(card)
            deck.remove(card)

            card = deck.random()
            guestHand.add(card)
            deck.remove(card)
        }

        gameRef.child("deck").setValue(deck)
        gameRef.child("host/hand").setValue(hostHand)
        gameRef.child("guest/hand").setValue(guestHand)
    }

    fun setMoves(moves: Map<String, String>) {
        gameRef.child("moves").setValue(moves)
    }
}
