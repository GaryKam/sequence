package io.github.garykam.sequence.ui.joingame

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.garykam.sequence.database.Database
import io.github.garykam.sequence.util.MarkerChip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class JoinGameViewModel @Inject constructor() : ViewModel() {
    private val _lobbyCode = MutableStateFlow("")
    val lobbyCode = _lobbyCode.asStateFlow()

    private val _markerChips = MutableStateFlow(emptyList<MarkerChip>())
    val markerChips = _markerChips.asStateFlow()

    private val _markerChipIndex = MutableStateFlow(0)
    val markerChipIndex = _markerChipIndex.asStateFlow()

    fun updateLobbyCode(lobbyCode: String) {
        _lobbyCode.update { lobbyCode.trim() }
    }

    fun findLobby() {
        if (_lobbyCode.value.isEmpty()) {
            return
        }

        Database.gamesRef
            .child(_lobbyCode.value)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result.exists()) {
                    val hostColor = task.result.child("host").getValue(String::class.java)
                    val markerChips = MarkerChip.entries.filter { it.name != hostColor }.toList()
                    _markerChips.update { markerChips }
                }
            }
    }

    fun selectMarkerChip(index: Int) {
        _markerChipIndex.update { index }
    }

    fun joinLobby() {
        Database.joinLobby(_lobbyCode.value, _markerChips.value[_markerChipIndex.value].name)
    }
}
