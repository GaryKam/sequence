package io.github.garykam.sequence.ui.joingame

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.garykam.sequence.R
import io.github.garykam.sequence.ui.components.MarkerChipSelection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinGameScreen(
    modifier: Modifier = Modifier,
    viewModel: JoinGameViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onGameStart: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    BackHandler {
        focusManager.clearFocus()
        onBack()
        viewModel.leaveLobby()
    }

    Scaffold(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    focusManager.clearFocus()
                }
            )
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(R.string.join_game)) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            onBack()
                            viewModel.leaveLobby()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
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
            AnimatedVisibility(visible = viewModel.step == Step.FIND_LOBBY) {
                OutlinedTextField(
                    value = viewModel.lobbyCode,
                    onValueChange = { viewModel.updateLobbyCode(it) },
                    label = { Text(text = stringResource(R.string.join_lobby_code)) },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { viewModel.findLobby() }
                    )
                )
            }
            AnimatedVisibility(visible = viewModel.step == Step.SELECT_CHIP) {
                MarkerChipSelection(
                    modifier = Modifier.fillMaxWidth(),
                    items = viewModel.markerChips,
                    selected = viewModel.markerChipIndex,
                    onClick = { viewModel.selectMarkerChip(it) }
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            AnimatedVisibility(visible = viewModel.step != Step.WAIT_IN_LOBBY) {
                Button(
                    onClick = {
                        if (viewModel.step == Step.FIND_LOBBY) {
                            viewModel.findLobby()
                        } else {
                            viewModel.joinLobby(onGameStart = onGameStart)
                        }
                    },
                    modifier = Modifier.padding(horizontal = 40.dp)
                ) {
                    Text(text = stringResource(R.string.join_lobby))
                }
            }
            AnimatedVisibility(visible = viewModel.step == Step.WAIT_IN_LOBBY) {
                Text(
                    text = stringResource(R.string.join_lobby_wait),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (viewModel.isLobbyClosed) {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {
                    TextButton(
                        onClick = {
                            onBack()
                            viewModel.leaveLobby()
                        }
                    ) {
                        Text(text = stringResource(R.string.leave))
                    }
                },
                title = { Text(text = stringResource(R.string.lobby_closed)) },
                text = { Text(text = stringResource(R.string.lobby_closed_text)) }
            )
        }
    }
}
