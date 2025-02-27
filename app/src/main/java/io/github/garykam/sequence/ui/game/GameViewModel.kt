package io.github.garykam.sequence.ui.game

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
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
import io.github.garykam.sequence.util.MarkerChip
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

    val card: Card?
        get() = if (activeCardIndex != -1) _hand.value[activeCardIndex] else null

    var isGameEnded by mutableStateOf(false)
        private set

    val userRole: String
        get() = database.userRole

    val oneEyedJacks = setOf("hj", "sj")
    val twoEyedJacks = setOf("dj", "cj")

    private val jackNames = oneEyedJacks + twoEyedJacks

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

    private var _winner = MutableStateFlow("")
    val winner = _winner.asStateFlow()

    var isWinnerDeclared = false
        private set

    private var _jackCards = mutableSetOf<Card>()

    private var _gameListeners = mutableListOf<ValueEventListener>()

    private val _blankCardIndices = buildSet {
        for ((index, cardName) in _boardSetup.withIndex()) {
            if (cardName == "b") {
                add(index)
            }
        }
    }

    private val emptySpace = MarkerChip.EMPTY.char.single()
    private val freeSpace = MarkerChip.FREE.char.single()
    private var _chipArray = Array(10) {
        CharArray(10) { emptySpace }
    }.apply {
        this[0][0] = freeSpace
        this[0][9] = freeSpace
        this[9][0] = freeSpace
        this[9][9] = freeSpace
    }

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
        for (cardName in jackNames) {
            val drawableId = context.resources.getIdentifier(
                cardName,
                "drawable",
                context.packageName
            )

            _jackCards.add(Card(cardName, drawableId))
        }

        // Server: cards in deck.
        val cards = (_boardSetup + jackNames + jackNames - setOf("b"))
        database.setupDeckAndHands(cards)

        // Server: listen for hand.
        database.gameRef.child("${database.userRole}/hand").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val hand = snapshot.getValue<List<String>>().orEmpty()

                    if (isGameEnded) {
                        return
                    }

                    _hand.update {
                        buildList {
                            for (cardName in hand) {
                                if (jackNames.contains(cardName)) {
                                    add(_jackCards.first { it.name == cardName })
                                } else {
                                    add(_board.value.first { it.name == cardName })
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("GameViewModel", error.toString())
                }
            }.also {
                _gameListeners.add(it)
            }
        )

        // Server: listen for moves.
        database.gameRef.child("moves").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val moves = buildMap {
                        for (child in snapshot.children) {
                            put(child.key!!, child.value!! as String)
                        }
                    }

                    _moves.update { moves }

                    updateChipArray()
                    checkGameOver()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("GameViewModel", error.toString())
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
                        isGameEnded = true
                    } else {
                        _turn.update { turn }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("GameViewModel", error.toString())
                }
            }.also {
                _gameListeners.add(it)
            }
        )

        // Server: listen for winner.
        database.gameRef.child("winner").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val winner = snapshot.getValue<String>().orEmpty()

                    _winner.update { winner }

                    if (winner.isNotEmpty()) {
                        isWinnerDeclared = true
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("GameViewModel", error.toString())
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
        if (_turn.value != database.userRole
            || _blankCardIndices.contains(boardIndex)
            || isWinnerDeclared
        ) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            database.addMove(boardIndex, _moves.value, _hand.value, activeCardIndex)
            activeCardIndex = -1
        }
    }

    fun removeMarkerChip(boardIndex: Int) {
        if (_turn.value != database.userRole || isWinnerDeclared) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            database.addMove(boardIndex, _moves.value, _hand.value, activeCardIndex)
            activeCardIndex = -1
        }
    }

    fun isUserChip(markerChip: MarkerChip?): Boolean {
        return markerChip?.char == database.userColor
    }

    fun isCardPlayable(card: Card?): Boolean {
        if (card == null) {
            return true
        }

        val cardIndices = _board.value.mapIndexed { index, cardOnBoard ->
            if (cardOnBoard.name == card.name) {
                index
            } else {
                null
            }
        }.filterNotNull()

        for (index in cardIndices) {
            if (!_moves.value.containsKey(index.toString())) {
                return true
            }
        }

        return false
    }

    fun swapDeadCard() {
        viewModelScope.launch(Dispatchers.IO) {
            database.drawCardFromDeck(_hand.value, activeCardIndex)
            activeCardIndex = -1
        }
    }

    fun leaveGame() {
        _gameListeners.forEach { database.gameRef.removeEventListener(it) }
        database.stopGame()
    }

    fun endGame() {
        database.removeGame()
    }

    fun hideWinner() {
        _winner.update { "" }
    }

    private fun updateChipArray() {
        for (row in _chipArray.indices) {
            for (column in _chipArray[row].indices) {
                if (_chipArray[row][column] != freeSpace) {
                    _chipArray[row][column] = emptySpace
                }
            }
        }

        for ((index, chipColor) in _moves.value.entries) {
            val boardIndex = index.toInt()
            val row = boardIndex / 10
            val column = boardIndex % 10

            _chipArray[row][column] = chipColor.single()
        }
    }

    private fun checkGameOver() {
        var sequences = 0

        // Horizontal sequence.
        for (row in _chipArray) {
            var chipsInARow = 0
            var chipColor = emptySpace

            for (char in row) {
                when {
                    char == emptySpace -> chipsInARow = 0
                    char == chipColor -> chipsInARow++
                    char == freeSpace -> chipsInARow++
                    chipColor == freeSpace && char != emptySpace -> chipsInARow++
                    char != chipColor -> chipsInARow = 1
                }

                if (chipColor.toString() == database.userColor) {
                    if (chipsInARow == 5 || chipsInARow == 9) {
                        sequences++
                    }
                }

                chipColor = char
            }
        }

        // Vertical sequence.
        for (column in _chipArray[0].indices) {
            var chipsInARow = 0
            var chipColor = emptySpace

            for (row in _chipArray.indices) {
                val char = _chipArray[row][column]

                when {
                    char == emptySpace -> chipsInARow = 0
                    char == chipColor -> chipsInARow++
                    char == freeSpace -> chipsInARow++
                    chipColor == freeSpace && char != emptySpace -> chipsInARow++
                    char != chipColor -> chipsInARow = 1
                }

                if (chipColor.toString() == database.userColor) {
                    if (chipsInARow == 5 || chipsInARow == 9) {
                        sequences++
                    }
                }

                chipColor = char
            }
        }

        // Diagonal sequence top-left to bottom-right, lower half.
        for (row in _chipArray.indices) {
            var chipsInARow = 0
            var chipColor = emptySpace
            var i = row
            var j = 0

            for (repeat in row until _chipArray.size) {
                val char = _chipArray[i][j]

                when {
                    char == emptySpace -> chipsInARow = 0
                    char == chipColor -> chipsInARow++
                    char == freeSpace -> chipsInARow++
                    chipColor == freeSpace && char != emptySpace -> chipsInARow++
                    char != chipColor -> chipsInARow = 1
                }

                if (chipColor.toString() == database.userColor) {
                    if (chipsInARow == 5 || chipsInARow == 9) {
                        sequences++
                    }
                }

                chipColor = char
                i++
                j++
            }
        }

        // Diagonal sequence top-left to bottom-right, upper half.
        for (row in _chipArray.lastIndex - 1 downTo 0) {
            var chipsInARow = 0
            var chipColor = emptySpace
            var i = row
            var j = _chipArray[0].lastIndex

            for (repeat in 0 until row + 1) {
                val char = _chipArray[i][j]

                when {
                    char == emptySpace -> chipsInARow = 0
                    char == chipColor -> chipsInARow++
                    char == freeSpace -> chipsInARow++
                    chipColor == freeSpace && char != emptySpace -> chipsInARow++
                    char != chipColor -> chipsInARow = 1
                }

                if (chipColor.toString() == database.userColor) {
                    if (chipsInARow == 5 || chipsInARow == 9) {
                        sequences++
                    }
                }

                chipColor = char
                i--
                j--
            }
        }

        // Diagonal sequence top-right to bottom-left, upper half.
        for (column in _chipArray[0].indices) {
            var chipsInARow = 0
            var chipColor = emptySpace
            var i = 0
            var j = column

            for (repeat in 0 until column + 1) {
                val char = _chipArray[i][j]

                when {
                    char == emptySpace -> chipsInARow = 0
                    char == chipColor -> chipsInARow++
                    char == freeSpace -> chipsInARow++
                    chipColor == freeSpace && char != emptySpace -> chipsInARow++
                    char != chipColor -> chipsInARow = 1
                }

                if (chipColor.toString() == database.userColor) {
                    if (chipsInARow == 5 || chipsInARow == 9) {
                        sequences++
                    }
                }

                chipColor = char
                i++
                j--
            }
        }

        // Diagonal sequence top-right to bottom-left, lower half.
        for (column in 1 until _chipArray[0].lastIndex) {
            var chipsInARow = 0
            var chipColor = emptySpace
            var i = 9
            var j = column

            for (repeat in column until _chipArray[0].lastIndex + 1) {
                val char = _chipArray[i][j]

                when {
                    char == emptySpace -> chipsInARow = 0
                    char == chipColor -> chipsInARow++
                    char == freeSpace -> chipsInARow++
                    chipColor == freeSpace && char != emptySpace -> chipsInARow++
                    char != chipColor -> chipsInARow = 1
                }

                if (chipColor.toString() == database.userColor) {
                    if (chipsInARow == 5 || chipsInARow == 9) {
                        sequences++
                    }
                }

                chipColor = char
                i--
                j++
            }
        }

        if (sequences == 2) {
            database.winGame()
            isWinnerDeclared = true
        }
    }
}
