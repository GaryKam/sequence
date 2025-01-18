package io.github.garykam.sequence.ui.game

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.garykam.sequence.R
import io.github.garykam.sequence.util.MarkerChip
import io.github.garykam.sequence.ui.components.Zoomable

@Composable
fun GameBoard(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel
) {
    val board by viewModel.board.collectAsState()
    val hand by viewModel.hand.collectAsState()
    val moves by viewModel.moves.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "infinite")
    val cardTint by infiniteTransition.animateColor(
        initialValue = Color.White,
        targetValue = Color.Black,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 700,
                easing = FastOutLinearInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cardHighlight"
    )

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

            itemsIndexed(board) { index, card ->
                Box(contentAlignment = Alignment.Center) {
                    val cardHasChip = moves.containsKey(index.toString())
                    val activeCard = if (viewModel.activeCardIndex != -1) {
                        hand[viewModel.activeCardIndex]
                    } else {
                        null
                    }

                    if (card.name == activeCard?.name && !cardHasChip) {
                        Image(
                            painter = painterResource(card.drawableId),
                            contentDescription = card.name,
                            modifier = Modifier.clickable(
                                interactionSource = null,
                                indication = null,
                                onClick = { viewModel.placeMarkerChip(index) }
                            ),
                            colorFilter = ColorFilter.tint(
                                color = cardTint.copy(alpha = 0.4f),
                                blendMode = BlendMode.Darken
                            )
                        )
                    } else {
                        Image(
                            painter = painterResource(card.drawableId),
                            contentDescription = card.name
                        )
                    }

                    if (cardHasChip) {
                        val markerChipColor = MarkerChip.entries
                            .firstOrNull { it.shortName == moves.getValue(index.toString()) }
                            ?.color

                        Image(
                            painter = painterResource(R.drawable.chip),
                            contentDescription = "marker chip",
                            modifier = Modifier.scale(0.9f),
                            colorFilter = ColorFilter.lighting(
                                multiply = Color.White,
                                add = markerChipColor ?: Color.Black
                            )
                        )
                    }
                }
            }
        }
    }
}
