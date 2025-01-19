package io.github.garykam.sequence.ui.joingame

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.garykam.sequence.data.Database
import io.github.garykam.sequence.util.MarkerChip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinGameViewModel @Inject constructor(
    private val database: Database
) : ViewModel() {
    var step by mutableStateOf(Step.FIND_LOBBY)
        private set

    var lobbyCode by mutableStateOf("")
        private set

    var markerChips by mutableStateOf(emptyList<MarkerChip>())
        private set

    var markerChipIndex by mutableIntStateOf(0)
        private set

    var isLobbyClosed by mutableStateOf(false)
        private set

    private lateinit var _gameListener: ValueEventListener

    fun updateLobbyCode(code: String) {
        if (code.trim().length <= 3) {
            lobbyCode = code.trim().toUpperCase(Locale.current)
        }
    }

    fun findLobby() {
        viewModelScope.launch(Dispatchers.IO) {
            if (!database.findLobby(lobbyCode)) {
                return@launch
            }

            database.gameRef
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
        database.joinLobby(markerChips[markerChipIndex].shortName)
        step = Step.WAIT_IN_LOBBY

        _gameListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild("turn")) {
                    database.gameRef.removeEventListener(_gameListener)
                    onGameStart()
                } else if (!snapshot.hasChild("host")) {
                    isLobbyClosed = true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("JoinGameViewModel", error.toString())
            }
        }

        _gameListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild("turn")) {
                    database.gameRef.removeEventListener(_gameListener)
                    onGameStart()
                } else if (!snapshot.hasChild("host")) {
                    isLobbyClosed = true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("JoinGameViewModel", error.toString())
            }
        }
        database.gameRef.addValueEventListener(_gameListener)
    }

    fun leaveLobby() {
        if (this::_gameListener.isInitialized) {
            database.gameRef.removeEventListener(_gameListener)
        }

        database.leaveLobby()
    }
}

enum class Step {
    FIND_LOBBY, SELECT_CHIP, WAIT_IN_LOBBY
}
