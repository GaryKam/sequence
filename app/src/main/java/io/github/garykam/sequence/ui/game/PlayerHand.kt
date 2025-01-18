package io.github.garykam.sequence.ui.game

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.github.garykam.sequence.R
import io.github.garykam.sequence.data.Database

@Composable
fun PlayerHand(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel
) {
    val turn by viewModel.turn.collectAsState()
    val hand by viewModel.hand.collectAsState()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (turn == Database.userRole) {
                "Your Turn"
            } else {
                "Opponent's Turn"
            },
            color = colorResource(R.color.text_on_board),
            style = MaterialTheme.typography.headlineSmall
        )
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for ((index, card) in hand.withIndex()) {
                val cardOffset by animateIntAsState(
                    targetValue = if (index == viewModel.activeCardIndex) -30 else 0,
                    label = "cardHeight"
                )

                Image(
                    painter = painterResource(card.drawableId),
                    contentDescription = card.name,
                    modifier = Modifier
                        .size(50.dp, 75.dp)
                        .offset { IntOffset(0, cardOffset) }
                        .clickable(
                            interactionSource = null,
                            indication = null,
                            onClick = { viewModel.selectCardFromHand(index) }
                        )
                )
            }
        }
    }
}
