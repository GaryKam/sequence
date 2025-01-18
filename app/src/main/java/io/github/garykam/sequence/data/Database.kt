package io.github.garykam.sequence.data

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.github.garykam.sequence.model.Card
import io.github.garykam.sequence.model.Game
import io.github.garykam.sequence.model.Host
import kotlinx.coroutines.tasks.await

class Database {
    val gameRef: DatabaseReference
        get() = firebase.getReference("games").child(_lobbyCode)

    var userRole = ""
        private set

    private var userColor = ""

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

        val deck = cards.shuffled().toMutableList()
        val hostHand = mutableListOf<String>()
        val guestHand = mutableListOf<String>()

        repeat(7) {
            hostHand.add(deck.removeFirstOrNull()!!)
            guestHand.add(deck.removeFirstOrNull()!!)
        }

        gameRef.child("deck").setValue(deck)
        gameRef.child("host/hand").setValue(hostHand)
        gameRef.child("guest/hand").setValue(guestHand)
    }

    suspend fun addMove(
        boardIndex: Int,
        currentMoves: Map<String, String>,
        hand: List<Card>,
        cardIndex: Int
    ) {
        val deck = gameRef.child("deck")
            .get()
            .await()
            .getValue(object : GenericTypeIndicator<List<String>>() {})
            .orEmpty()
        val newDeck = deck.toMutableList()
        val nextCard = newDeck.removeFirstOrNull()
        val newHand = hand
            .map { it.name }
            .toMutableList()
            .apply {
                if (nextCard != null) {
                    this[cardIndex] = nextCard
                }
            }
        val newMove = boardIndex.toString() to userColor
        val update = hashMapOf(
            "moves" to currentMoves + newMove,
            "turn" to if (userRole == "host") "guest" else "host",
            "deck" to newDeck,
            "$userRole/hand" to newHand
        )

        gameRef.updateChildren(update)
    }

    fun stopGame() {
        gameRef.child("turn").removeValue()
    }
}
