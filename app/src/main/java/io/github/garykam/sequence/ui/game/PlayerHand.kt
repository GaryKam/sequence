package io.github.garykam.sequence.ui.game

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
fun PlayerHand(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel
) {
    val hand by viewModel.hand.collectAsState()
    val activeCardIndex by viewModel.activeCardIndex.collectAsState()

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (hand.isEmpty()) {
            Button(onClick = { viewModel.dealCards() }) {
                Text(text = "Random hand")
            }
        }

        for ((index, card) in hand.withIndex()) {
            val cardOffset by animateIntAsState(
                targetValue = if (index == activeCardIndex) -30 else 0,
                label = "cardHeight"
            )

            Image(
                painter = painterResource(card.drawableId),
                contentDescription = card.name,
                modifier = Modifier
                    .size(50.dp, 75.dp)
                    .offset { IntOffset(0, cardOffset) }
                    .clickable { viewModel.selectCardFromHand(index) }
            )
        }
    }
}
