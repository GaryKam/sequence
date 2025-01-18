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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.garykam.sequence.R
import io.github.garykam.sequence.util.ScreenUtil

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel(),
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
                .weight(0.85f)
                .fillMaxSize()
                .background(colorResource(R.color.board)),
            viewModel = viewModel
        )

        PlayerHand(
            modifier = Modifier
                .weight(0.15f)
                .fillMaxWidth()
                .background(colorResource(R.color.board)),
            viewModel = viewModel
        )
    }

    val isExitDialogVisible = remember { mutableStateOf(false) }

    BackHandler {
        isExitDialogVisible.value = true
    }

    if (isExitDialogVisible.value) {
        AlertDialog(
            onDismissRequest = { isExitDialogVisible.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.leaveGame()
                        onGameLeave()
                    }
                ) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { isExitDialogVisible.value = false }) {
                    Text(text = "No")
                }
            },
            title = { Text(text = "Quit") },
            text = { Text(text = "Do you want to leave this game?") }
        )

        return
    }

    if (viewModel.isGameClosed) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.leaveGame()
                        viewModel.endGame()
                        onGameLeave()
                    }
                ) {
                    Text(text = "Okay")
                }
            },
            title = { Text(text = "Game Over") },
            text = { Text(text = "Your opponent left the game.") }
        )
    }
}
