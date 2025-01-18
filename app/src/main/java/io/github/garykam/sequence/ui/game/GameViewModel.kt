package io.github.garykam.sequence.ui.game

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.garykam.sequence.data.Database
import io.github.garykam.sequence.model.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val database: Database
) : ViewModel() {
    var activeCardIndex by mutableIntStateOf(-1)
        private set

    var isGameEnded by mutableStateOf(false)
        private set

    val userRole: String
        get() = database.userRole

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

    private var _moves = MutableStateFlow(emptyMap<String, String>())
    val moves = _moves

    private var _hand = MutableStateFlow(listOf<Card>())
    val hand = _hand.asStateFlow()

    private var _turn = MutableStateFlow("")
    val turn = _turn.asStateFlow()

    private var _jackCards = mutableSetOf<Card>()

    private var _gameListeners = mutableSetOf<ValueEventListener>()

    @SuppressLint("DiscouragedApi")
    fun init(context: Context) {
        // Client: grid of cards.
        _board.value = buildList {
            for (cardName in _boardSetup) {
                val drawableId = context.resources.getIdentifier(
                    cardName,
                    "drawable",
                    context.packageName
                )
                add(Card(cardName, drawableId))
            }
        }

        // Client: jack wild cards.
        val jackNames = listOf("cj", "dj", "hj", "sj")
        for (cardName in jackNames) {
            val drawableId = context.resources.getIdentifier(
                cardName,
                "drawable",
                context.packageName
            )
            _jackCards.add(Card(cardName, drawableId))
        }

        // Server: cards in deck.
        val cards = (_boardSetup + jackNames - setOf("b"))
        database.setupDeckAndHands(cards)

        // Server: listen for hand.
        database.gameRef.child("$userRole/hand").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val hand = snapshot.getValue<List<String>>().orEmpty()

                    _hand.update {
                        buildList {
                            for (cardName in hand) {
                                val card = _board.value.firstOrNull { it.name == cardName }
                                card?.let { add(it) }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }.also {
                _gameListeners.add(it)
            }
        )

        // Server: listen for moves.
        database.gameRef.child("moves").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _moves.update { snapshot.getValue<Map<String, String>>().orEmpty() }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }.also {
                _gameListeners.add(it)
            }
        )

        // Server: listen for turn.
        database.gameRef.child("turn").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val turn = snapshot.getValue<String>().orEmpty()

                    if (turn.isEmpty()) {
                        leaveGame()
                    } else {
                        _turn.update { turn }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }.also {
                _gameListeners.add(it)
            }
        )
    }

    fun selectCardFromHand(cardIndex: Int) {
        activeCardIndex = if (activeCardIndex == cardIndex) -1 else cardIndex
    }

    fun placeMarkerChip(boardIndex: Int) {
        if (_turn.value != userRole) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            database.addMove(boardIndex, _moves.value, _hand.value, activeCardIndex)
            activeCardIndex = -1
        }
    }

    fun leaveGame() {
        for (listener in _gameListeners) {
            database.gameRef.removeEventListener(listener)
        }

        database.stopGame()
        isGameEnded = true
    }

    fun endGame() {
        database.removeGame()
    }
}
