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

    private var _lobbyCode = ""

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
        _lobbyCode = lobbyCode
        gamesRef.child(lobbyCode).setValue(Game(hostColor))
    }

    fun closeLobby() {
        gamesRef.child(_lobbyCode).removeValue()
    }

    fun joinLobby(
        lobbyCode: String,
        guestColor: String
    ) {
        _lobbyCode = lobbyCode
        gamesRef.child(lobbyCode).child("guest").setValue(guestColor)
    }

    fun leaveLobby() {
        gamesRef.child(_lobbyCode).child("guest").removeValue()
    }

    fun startGame(): String {
        gamesRef.child(_lobbyCode).child("moves").setValue("")
        return _lobbyCode
    }

    fun addMove(
        boardIndex: Int,
        markerChipColor: String
    ) {
        //movesRef.setValue(_moves.value + mapOf(boardIndex.toString() to markerChipColor))
    }
}
