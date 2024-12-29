package io.github.garykam.sequence.ui.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.garykam.sequence.util.Zoomable

@Composable
fun GameBoard(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel
) {
    val board by viewModel.board.collectAsState()
    val activeCard by viewModel.activeCard.collectAsState()

    Zoomable(modifier = modifier) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(10),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            userScrollEnabled = false
        ) {
            if (board.isEmpty()) {
                return@LazyVerticalGrid
            }

            items(board) { card ->
                if (card.name == activeCard) {
                    Image(
                        painter = painterResource(card.drawableId),
                        contentDescription = card.name,
                        modifier = Modifier.clickable { },
                        colorFilter = ColorFilter.tint(
                            color = Color.DarkGray.copy(alpha = 0.5f),
                            blendMode = BlendMode.Darken
                        )
                    )
                } else {
                    Image(
                        painter = painterResource(card.drawableId),
                        contentDescription = card.name,
                        modifier = Modifier.clickable { }
                    )
                }
            }
        }
    }
}
