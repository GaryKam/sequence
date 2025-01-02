package io.github.garykam.sequence.ui.game

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.garykam.sequence.database.Database
import io.github.garykam.sequence.util.Card
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor() : ViewModel() {
    private val _boardSetup = listOf(
        "b", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "b",
        "c6", "c5", "c4", "c3", "c2", "h1", "hk", "hq", "h10", "s10",
        "c7", "s1", "d2", "d3", "d4", "d5", "d6", "d7", "h9", "sq",
        "c8", "sk", "c6", "c5", "c4", "c3", "c2", "d8", "h8", "sk",
        "c9", "sq", "c7", "h6", "h5", "h4", "h1", "d9", "h7", "s1",
        "c10", "s10", "c8", "h7", "h2", "h3", "hk", "d10", "h6", "d2",
        "cq", "s9", "c9", "h8", "h9", "h10", "hq", "dq", "h5", "d3",
        "ck", "s8", "c10", "cq", "ck", "c1", "d1", "dk", "h4", "d4",
        "c1", "s7", "s6", "s5", "s4", "s3", "s2", "h2", "h3", "d5",
        "b", "d1", "dk", "dq", "d10", "d9", "d8", "d7", "d6", "b"
    )

    private var _board = MutableStateFlow(listOf<Card>())
    val board = _board.asStateFlow()

    private var _hand = MutableStateFlow(listOf<Card>())
    val hand = _hand.asStateFlow()

    private var _activeCardIndex: MutableStateFlow<Int?> = MutableStateFlow(null)
    val activeCardIndex = _activeCardIndex.asStateFlow()

    private var _jackCards = mutableSetOf<Card>()

    @SuppressLint("DiscouragedApi")
    fun init(context: Context) {
        _board.value = buildList {
            for (cardName in _boardSetup) {
                val drawableId = context.resources.getIdentifier(cardName, "drawable", context.packageName)
                add(Card(cardName, drawableId))
            }
        }

        val jackNames = listOf("cj", "dj", "hj", "sj")
        for (cardName in jackNames) {
            val drawableId = context.resources.getIdentifier(cardName, "drawable", context.packageName)
            _jackCards.add(Card(cardName, drawableId))
        }
    }

    fun dealCards() {
        val availableCards = (_board.value + _jackCards).filter { it.name != "b" }
        _hand.update {
            buildList {
                repeat(7) {
                    add(availableCards.random())
                }
            }
        }
    }

    fun selectCardFromHand(cardIndex: Int) {
        if (_activeCardIndex.value == cardIndex) {
            _activeCardIndex.update { null }
        } else {
            _activeCardIndex.update { cardIndex }
        }
    }

    fun placeMarkerChip(boardIndex: Int) {
        Database.addMove(boardIndex, "R")
    }
}
