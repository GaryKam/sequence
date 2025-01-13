package io.github.garykam.sequence.ui.creategame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    onBack: () -> Unit
) {
    val markerChipIndex by viewModel.markerChipIndex.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Create a Game") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
            MarkerChipSelection(
                modifier = Modifier.fillMaxWidth(),
                items = MarkerChip.entries,
                selected = markerChipIndex,
                onClick = { viewModel.selectMarkerChip(it) }
            )
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = { viewModel.createLobby() },
                modifier = Modifier.padding(horizontal = 40.dp)
            ) {
                Text(text = "Create Lobby")
            }
        }
    }
}
