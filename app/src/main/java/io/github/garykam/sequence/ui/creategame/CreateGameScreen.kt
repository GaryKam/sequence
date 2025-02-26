package io.github.garykam.sequence.ui.creategame

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.garykam.sequence.R
import io.github.garykam.sequence.ui.components.MarkerChipSelection
import io.github.garykam.sequence.util.MarkerChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGameScreen(
    modifier: Modifier = Modifier,
    viewModel: CreateGameViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onGameStart: () -> Unit
) {
    BackHandler {
        onBack()
        viewModel.closeLobby()
    }

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.errorId.collect {
            if (it != -1) {
                snackbarHostState.showSnackbar(message = context.getString(it))
                viewModel.hideErrorMessage()
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(R.string.create_game)) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBack()
                            viewModel.closeLobby()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = {
                    Snackbar {
                        Text(text = it.visuals.message)
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
                        text = stringResource(R.string.create_lobby_code, viewModel.lobbyCode),
                        modifier = Modifier.padding(bottom = 50.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(R.string.create_lobby_wait),
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
                                stringResource(R.string.create_lobby_1_player)
                            } else {
                                stringResource(R.string.create_lobby_2_player)
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
                        stringResource(R.string.create_lobby)
                    } else {
                        stringResource(R.string.start_game)
                    }
                )
            }
        }
    }
}
