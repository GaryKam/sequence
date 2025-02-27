package io.github.garykam.sequence.data

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
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

    var userColor = ""
        private set

    private val firebase = Firebase.database

    var connected = false
        private set

    private var _lobbyCode = ""

    init {
        firebase.getReference(".info/connected").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    connected = snapshot.getValue(Boolean::class.java) ?: false
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Database", error.toString())
                }
            }
        )
    }

    fun createLobby(
        lobbyCode: String,
        hostColor: String
    ): Boolean {
        if (connected) {
            _lobbyCode = lobbyCode
            userRole = "host"
            userColor = hostColor
            gameRef.setValue(Game(Host(hostColor)))
        }

        return connected
    }

    fun removeGame() {
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

    fun joinLobby(guestColor: String) {
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
            .toMutableList()
        val nextCard = deck.removeFirstOrNull()
        val newHand = hand
            .map { it.name }
            .toMutableList()
            .apply {
                if (nextCard != null) {
                    this[cardIndex] = nextCard
                } else {
                    removeAt(cardIndex)
                }
            }
        val isChipOnCard = currentMoves.containsKey(boardIndex.toString())
        val newMove = boardIndex.toString() to userColor
        val newMoves = if (isChipOnCard) {
            currentMoves - boardIndex.toString()
        } else {
            currentMoves + newMove
        }

        val update = hashMapOf(
            "moves" to newMoves,
            "turn" to if (userRole == "host") "guest" else "host",
            "deck" to deck,
            "$userRole/hand" to newHand
        )

        gameRef.updateChildren(update)
    }

    fun winGame() {
        gameRef.child("winner").setValue(userRole)
    }

    fun stopGame() {
        gameRef.child("turn").removeValue()
    }
}
