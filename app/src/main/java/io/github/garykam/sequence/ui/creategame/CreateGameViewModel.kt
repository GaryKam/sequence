package io.github.garykam.sequence.ui.creategame

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.garykam.sequence.database.Database
import io.github.garykam.sequence.util.MarkerChip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CreateGameViewModel @Inject constructor() : ViewModel() {
    private val _markerChipIndex = MutableStateFlow(0)
    val markerChipIndex = _markerChipIndex.asStateFlow()

    fun selectMarkerChip(index: Int) {
        _markerChipIndex.update { index }
    }

    fun createLobby() {
        val charPool = ('A'..'Z') + ('0'..'9')
        val lobbyCode = List(3) { charPool.random() }.joinToString("")
        Database.createLobby(lobbyCode, MarkerChip.entries[_markerChipIndex.value].name)
    }
}
