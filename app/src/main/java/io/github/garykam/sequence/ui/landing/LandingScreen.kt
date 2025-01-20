package io.github.garykam.sequence.ui.landing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.garykam.sequence.R
import io.github.garykam.sequence.util.ScreenUtil

@Composable
fun LandingScreen(
    modifier: Modifier = Modifier,
    onJoinGameClick: () -> Unit,
    onCreateGameClick: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        ScreenUtil.hideSystemBars(context)
    }

    Box(modifier = modifier) {
        Text(
            text = stringResource(R.string.app_name),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineLarge
        )

        Row(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(0.25f))
            Column(modifier = Modifier.weight(0.5f)) {
                Button(
                    onClick = onJoinGameClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.join_game))
                }
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onCreateGameClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.create_game))
                }
            }
            Box(modifier = Modifier.weight(0.25f))
        }
    }
}
