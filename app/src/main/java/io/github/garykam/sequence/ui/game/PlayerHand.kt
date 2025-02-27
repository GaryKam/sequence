package io.github.garykam.sequence.ui.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.github.garykam.sequence.R

@Composable
fun PlayerHand(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel
) {
    val hand by viewModel.hand.collectAsState()
    val turn by viewModel.turn.collectAsState()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (viewModel.oneEyedJacks.contains(viewModel.card?.name)) {
            Text(
                text = stringResource(R.string.one_eyed_jack_text),
                color = colorResource(R.color.text_on_board),
                style = MaterialTheme.typography.bodySmall
            )
        } else if (viewModel.twoEyedJacks.contains(viewModel.card?.name)) {
            Text(
                text = stringResource(R.string.two_eyed_jack_text),
                color = colorResource(R.color.text_on_board),
                style = MaterialTheme.typography.bodySmall
            )
        } else if (!viewModel.isCardPlayable(viewModel.card)) {
            Button(
                onClick = {
                    if (turn == viewModel.userRole) {
                        viewModel.swapDeadCard()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Text(text = stringResource(R.string.swap_card))
            }
        }
        AnimatedVisibility(visible = turn == viewModel.userRole && !viewModel.isWinnerDeclared) {
            Text(
                text = stringResource(R.string.user_turn),
                color = colorResource(R.color.text_on_board),
                style = MaterialTheme.typography.headlineSmall
            )
        }
        AnimatedVisibility(visible = turn != viewModel.userRole && !viewModel.isWinnerDeclared) {
            Text(
                text = stringResource(R.string.opponent_turn),
                color = colorResource(R.color.text_on_board),
                style = MaterialTheme.typography.headlineSmall
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for ((index, card) in hand.withIndex()) {
                val cardOffset by animateIntAsState(
                    targetValue = if (index == viewModel.activeCardIndex) -30 else 0,
                    label = stringResource(R.string.card_height)
                )

                Image(
                    painter = painterResource(card.drawableId),
                    contentDescription = card.name,
                    modifier = Modifier
                        .size(50.dp, 75.dp)
                        .offset {
                            IntOffset(
                                x = 0,
                                y = cardOffset
                            )
                        }
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
