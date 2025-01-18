package io.github.garykam.sequence.ui.game

import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.garykam.sequence.R

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel()
) {
    val context = LocalContext.current
    val boardColor = colorResource(R.color.board).toArgb()

    LaunchedEffect(Unit) {
        viewModel.init(context)

        (context as ComponentActivity).window.apply {
            statusBarColor = boardColor
            navigationBarColor = boardColor
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
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
}
