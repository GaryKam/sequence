package io.github.garykam.sequence.ui.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.garykam.sequence.R
import io.github.garykam.sequence.util.ScreenUtil

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = hiltViewModel(),
    onGameLeave: () -> Unit
) {
    val context = LocalContext.current
    val boardColor = colorResource(R.color.board)

    LaunchedEffect(Unit) {
        viewModel.init(context)

        ScreenUtil.showSystemBars(context, boardColor)
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GameBoard(
            modifier = Modifier
                .weight(0.82f)
                .fillMaxSize()
                .background(colorResource(R.color.board)),
            viewModel = viewModel
        )

        PlayerHand(
            modifier = Modifier
                .weight(0.18f)
                .fillMaxWidth()
                .background(colorResource(R.color.board)),
            viewModel = viewModel
        )
    }

    val winner by viewModel.winner.collectAsState()
    val isExitDialogVisible = remember { mutableStateOf(false) }

    BackHandler {
        isExitDialogVisible.value = true
    }

    if (winner.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton(onClick = { viewModel.hideWinner() }) {
                    Text(text = stringResource(R.string.ok))
                }
            },
            title = { Text(text = stringResource(R.string.game_over)) },
            text = {
                if (winner == viewModel.userRole) {
                    Text(text = stringResource(R.string.game_won))
                } else {
                    Text(text = stringResource(R.string.game_lost))
                }
            }
        )
    } else if (isExitDialogVisible.value) {
        AlertDialog(
            onDismissRequest = { isExitDialogVisible.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onGameLeave()
                        viewModel.leaveGame()

                        if (viewModel.isGameEnded) {
                            viewModel.endGame()
                        }
                    }
                ) {
                    Text(text = stringResource(R.string.yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { isExitDialogVisible.value = false }) {
                    Text(text = stringResource(R.string.no))
                }
            },
            title = { Text(text = stringResource(R.string.quit_game)) },
            text = { Text(text = stringResource(R.string.quit_game_text)) }
        )
    } else if (viewModel.isGameEnded) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton(
                    onClick = {
                        onGameLeave()
                        viewModel.leaveGame()
                        viewModel.endGame()
                    }
                ) {
                    Text(text = stringResource(R.string.leave))
                }
            },
            title = { Text(text = stringResource(R.string.game_over)) },
            text = { Text(text = stringResource(R.string.game_over_text)) }
        )
    }
}
