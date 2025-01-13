package io.github.garykam.sequence.database

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.github.garykam.sequence.util.Game
import kotlinx.coroutines.flow.MutableStateFlow

object Database {
    private val firebase = Firebase.database
    val gamesRef = firebase.getReference("games")
    private var _moves = MutableStateFlow(emptyMap<String, String>())
    val moves = _moves

    init {
        /*movesRef.setValue(null)
        movesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _moves.update { snapshot.getValue<Map<String, String>>().orEmpty() }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })*/
    }

    fun createLobby(
        lobbyCode: String,
        hostColor: String
    ) {
        gamesRef.child(lobbyCode).setValue(Game(hostColor))
    }

    fun joinLobby(
        lobbyCode: String,
        guestColor: String
    ) {
        gamesRef.child(lobbyCode).child("guest").setValue(guestColor)
    }

    fun addMove(
        boardIndex: Int,
        markerChipColor: String
    ) {
        //movesRef.setValue(_moves.value + mapOf(boardIndex.toString() to markerChipColor))
    }
}
