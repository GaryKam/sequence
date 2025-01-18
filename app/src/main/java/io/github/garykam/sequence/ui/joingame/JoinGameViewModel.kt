package io.github.garykam.sequence.ui.joingame

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.garykam.sequence.database.Database
import io.github.garykam.sequence.util.MarkerChip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    private lateinit var _gameListener: ValueEventListener

    fun updateLobbyCode(code: String) {
        lobbyCode = code.trim()
    }

    fun findLobby() {
        viewModelScope.launch(Dispatchers.IO) {
            if (!Database.findLobby(lobbyCode)) {
                return@launch
            }

            Database.gameRef
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result.exists()) {
                        val hostColor = task.result.child("host/color").getValue(String::class.java)
                        val markerChips = MarkerChip.entries
                            .filter { it.shortName != hostColor }
                            .toList()
                        this@JoinGameViewModel.markerChips = markerChips
                        step = Step.SELECT_CHIP
                    }
                }
        }
    }

    fun selectMarkerChip(index: Int) {
        markerChipIndex = index
    }

    fun joinLobby(onGameStart: () -> Unit) {
        Database.joinLobby(markerChips[markerChipIndex].shortName)
        step = Step.WAIT_IN_LOBBY

        _gameListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild("turn")) {
                    Database.gameRef.removeEventListener(_gameListener)
                    onGameStart()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        Database.gameRef.addValueEventListener(_gameListener)
    }

    fun leaveLobby() {
        if (this::_gameListener.isInitialized) {
            Database.gameRef.removeEventListener(_gameListener)
        }
        Database.leaveLobby()
    }
}

enum class Step {
    FIND_LOBBY, SELECT_CHIP, WAIT_IN_LOBBY
}
