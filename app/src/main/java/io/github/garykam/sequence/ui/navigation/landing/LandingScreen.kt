package io.github.garykam.sequence.ui.navigation.landing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LandingScreen(
    modifier: Modifier = Modifier,
    onJoinGameClick: () -> Unit,
    onCreateGameClick: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onJoinGameClick) {
            Text(text = "Join a game")
        }
        
        Button(onClick = onCreateGameClick) {
            Text(text = "Create a game")
        }
    }
}
