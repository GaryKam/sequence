package io.github.garykam.sequence.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.garykam.sequence.util.MarkerChip

@Composable
fun MarkerChipSelection(
    modifier: Modifier = Modifier,
    items: List<MarkerChip>,
    selected: Int,
    onClick: (Int) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Choose your color:",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for ((index, markerChip) in items.withIndex()) {
                IconButton(onClick = { onClick(index) }) {
                    Box(
                        modifier = Modifier
                            .border(
                                width = 2.dp,
                                color = if (index == selected) {
                                    MaterialTheme.colorScheme.onBackground
                                } else {
                                    Color.Transparent
                                },
                                shape = CircleShape
                            )
                            .padding(5.dp)
                            .clip(CircleShape)
                            .fillMaxSize()
                            .background(markerChip.color)
                    )
                }
            }
        }
    }
}
