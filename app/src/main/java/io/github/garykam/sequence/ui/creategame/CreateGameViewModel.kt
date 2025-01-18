package io.github.garykam.sequence.ui.creategame

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
class CreateGameViewModel @Inject constructor() : ViewModel() {
    var step by mutableStateOf(Step.SELECT_CHIP)
        private set

    var markerChipIndex by mutableIntStateOf(0)
        private set

    var lobbyCode by mutableStateOf("")
        private set

    fun selectMarkerChip(index: Int) {
        markerChipIndex = index
    }

    private lateinit var _gameListener: ValueEventListener

    fun createLobby() {
        val charPool = ('A'..'Z') + ('0'..'9')
        val lobbyCode = List(3) { charPool.random() }.joinToString("")
        Database.createLobby(lobbyCode, MarkerChip.entries[markerChipIndex].shortName)
        this.lobbyCode = lobbyCode
        step = Step.WAIT_IN_LOBBY

        _gameListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                step = if (snapshot.hasChild("guest")) {
                    Step.READY_IN_LOBBY
                } else {
                    Step.WAIT_IN_LOBBY
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        Database.gameRef.addValueEventListener(_gameListener)
    }

    fun startGame() {
        Database.gameRef.removeEventListener(_gameListener)
        Database.startGame()
    }

    fun closeLobby() {
        Database.gameRef.removeEventListener(_gameListener)
        Database.closeLobby()
    }
}

enum class Step {
    SELECT_CHIP, WAIT_IN_LOBBY, READY_IN_LOBBY
}
