package io.github.garykam.sequence.ui.joingame

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
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.garykam.sequence.ui.components.MarkerChipSelection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinGameScreen(
    modifier: Modifier = Modifier,
    viewModel: JoinGameViewModel = viewModel(),
    onBack: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val lobbyCode by viewModel.lobbyCode.collectAsState()
    val markerChips by viewModel.markerChips.collectAsState()
    val markerChipIndex by viewModel.markerChipIndex.collectAsState()

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
                title = { Text(text = "Join a Game") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            onBack()
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
            AnimatedVisibility(visible = markerChips.isEmpty()) {
                OutlinedTextField(
                    value = lobbyCode,
                    onValueChange = { viewModel.updateLobbyCode(it) },
                    label = { Text(text = "Lobby Code") },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { viewModel.findLobby() }
                    )
                )
            }
            AnimatedVisibility(visible = markerChips.isNotEmpty()) {
                MarkerChipSelection(
                    modifier = Modifier.fillMaxWidth(),
                    items = markerChips,
                    selected = markerChipIndex,
                    onClick = { viewModel.selectMarkerChip(it) }
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = {
                    if (markerChips.isEmpty()) {
                        viewModel.findLobby()
                    } else {
                        viewModel.joinLobby()
                    }
                },
                modifier = Modifier.padding(horizontal = 40.dp)
            ) {
                Text(text = "Join Lobby")
            }
        }
    }
}
