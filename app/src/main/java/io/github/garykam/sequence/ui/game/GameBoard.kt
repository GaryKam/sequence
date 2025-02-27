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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.garykam.sequence.R
import io.github.garykam.sequence.ui.components.Zoomable
import io.github.garykam.sequence.util.MarkerChip

@Composable
fun GameBoard(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel
) {
    val board by viewModel.board.collectAsState()
    val moves by viewModel.moves.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = stringResource(R.string.infinite))

    val cardTint by infiniteTransition.animateColor(
        initialValue = Color.White,
        targetValue = Color.Black,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 500,
                easing = FastOutLinearInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = stringResource(R.string.card_highlight)
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
                    val isChipOnCard = moves.containsKey(index.toString())
                    val activeCardName = viewModel.card?.name

                    if (viewModel.oneEyedJacks.contains(activeCardName)) {
                        Image( // Special jack: remove opponent chip.
                            painter = painterResource(card.drawableId),
                            contentDescription = card.name
                        )

                        if (isChipOnCard) {
                            val markerChip = MarkerChip.getChip(moves.getValue(index.toString()))

                            if (viewModel.isUserChip(markerChip)) {
                                Image( // User chip.
                                    painter = painterResource(R.drawable.chip),
                                    contentDescription = stringResource(R.string.marker_chip),
                                    modifier = Modifier.scale(0.9f),
                                    colorFilter = ColorFilter.lighting(
                                        multiply = Color.White,
                                        add = markerChip.color!!
                                    )
                                )
                            } else {
                                val chipTint by infiniteTransition.animateColor(
                                    initialValue = markerChip.color!!,
                                    targetValue = Color.White,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(
                                            durationMillis = 500,
                                            easing = FastOutLinearInEasing
                                        ),
                                        repeatMode = RepeatMode.Reverse
                                    ),
                                    label = stringResource(R.string.chip_highlight)
                                )

                                Image( // Tinted opponent chip.
                                    painter = painterResource(R.drawable.chip),
                                    contentDescription = stringResource(R.string.marker_chip),
                                    modifier = Modifier
                                        .scale(0.9f)
                                        .clickable(
                                            interactionSource = null,
                                            indication = null,
                                            onClick = { viewModel.removeMarkerChip(index) }
                                        ),
                                    colorFilter = ColorFilter.lighting(
                                        multiply = Color.White,
                                        add = chipTint
                                    )
                                )
                            }
                        }
                    } else if (viewModel.twoEyedJacks.contains(activeCardName)) {
                        if (isChipOnCard) { // Special jack: place chip on open card.
                            val markerChip = MarkerChip.getChip(moves.getValue(index.toString()))

                            Image(
                                painter = painterResource(card.drawableId),
                                contentDescription = card.name
                            )
                            Image(
                                painter = painterResource(R.drawable.chip),
                                contentDescription = stringResource(R.string.marker_chip),
                                modifier = Modifier.scale(0.9f),
                                colorFilter = ColorFilter.lighting(
                                    multiply = Color.White,
                                    add = markerChip.color!!
                                )
                            )
                        } else {
                            Image(
                                painter = painterResource(card.drawableId),
                                contentDescription = card.name,
                                modifier = Modifier.clickable(
                                    interactionSource = null,
                                    indication = null,
                                    onClick = { viewModel.placeMarkerChip(index) }
                                )
                            )
                        }
                    } else if (card.name == activeCardName && !isChipOnCard) {
                        Image( // Tinted active card.
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
                        Image( // Plain card.
                            painter = painterResource(card.drawableId),
                            contentDescription = card.name
                        )

                        if (isChipOnCard) {
                            val markerChip = MarkerChip.getChip(moves.getValue(index.toString()))

                            Image(
                                painter = painterResource(R.drawable.chip),
                                contentDescription = stringResource(R.string.marker_chip),
                                modifier = Modifier.scale(0.9f),
                                colorFilter = ColorFilter.lighting(
                                    multiply = Color.White,
                                    add = markerChip.color!!
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
