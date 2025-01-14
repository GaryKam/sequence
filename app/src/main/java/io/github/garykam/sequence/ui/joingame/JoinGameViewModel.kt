package io.github.garykam.sequence.ui.joingame

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.garykam.sequence.database.Database
import io.github.garykam.sequence.util.MarkerChip
import javax.inject.Inject

@HiltViewModel
class JoinGameViewModel @Inject constructor() : ViewModel() {
    var step by mutableStateOf(Step.FIND_LOBBY)
        private set

    var lobbyCode by mutableStateOf("")
        private set

    var markerChips by mutableStateOf(emptyList<MarkerChip>())
        private set

    var markerChipIndex by mutableIntStateOf(0)
        private set

    fun updateLobbyCode(code: String) {
        lobbyCode = code.trim()
    }

    fun findLobby() {
        if (lobbyCode.isEmpty()) {
            return
        }

        Database.gamesRef
            .child(lobbyCode)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result.exists()) {
                    val hostColor = task.result.child("host").getValue(String::class.java)
                    val markerChips = MarkerChip.entries.filter { it.name != hostColor }.toList()
                    this.markerChips = markerChips
                    step = Step.SELECT_CHIP
                }
            }
    }

    fun selectMarkerChip(index: Int) {
        markerChipIndex = index
    }

    fun joinLobby(onGameStart: (String) -> Unit) {
        Database.joinLobby(lobbyCode, markerChips[markerChipIndex].name)
        step = Step.WAIT_IN_LOBBY

        Database.gamesRef.child(lobbyCode).addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild("moves")) {
                        onGameStart(lobbyCode)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
        )
    }

    fun leaveLobby() {
        Database.leaveLobby()
    }
}

enum class Step {
     FIND_LOBBY, SELECT_CHIP, WAIT_IN_LOBBY
}
