package io.github.garykam.sequence.ui.creategame

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.garykam.sequence.ui.components.MarkerChipSelection
import io.github.garykam.sequence.util.MarkerChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGameScreen(
    modifier: Modifier = Modifier,
    viewModel: CreateGameViewModel = viewModel(),
    onBack: () -> Unit,
    onGameStart: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Create a Game") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBack()
                            viewModel.closeLobby()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(visible = viewModel.step == Step.SELECT_CHIP) {
                MarkerChipSelection(
                    modifier = Modifier.fillMaxWidth(),
                    items = MarkerChip.entries,
                    selected = viewModel.markerChipIndex,
                    onClick = { viewModel.selectMarkerChip(it) }
                )
            }
            AnimatedVisibility(visible = viewModel.step != Step.SELECT_CHIP) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Waiting for guest...",
                        modifier = Modifier.padding(bottom = 5.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null
                        )
                        Text(
                            text = if (viewModel.step == Step.WAIT_IN_LOBBY) {
                                "1/2"
                            } else {
                                "2/2"
                            },
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = {
                    if (viewModel.step == Step.SELECT_CHIP) {
                        viewModel.createLobby()
                    } else {
                        viewModel.startGame()
                        onGameStart()
                    }
                },
                modifier = Modifier.padding(horizontal = 40.dp),
                enabled = viewModel.step != Step.WAIT_IN_LOBBY
            ) {
                Text(
                    text = if (viewModel.step == Step.SELECT_CHIP) {
                        "Create Lobby"
                    } else {
                        "Start Game"
                    }
                )
            }
        }
    }
}
