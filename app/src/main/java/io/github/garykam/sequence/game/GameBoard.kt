package io.github.garykam.sequence.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.garykam.sequence.R

@Composable
fun GameBoard(modifier: Modifier = Modifier) {
    val sections = (0 until 100).toList()
    var offset by remember { mutableStateOf(Offset.Zero) }
    var zoom by remember { mutableFloatStateOf(1f) }

    Box(modifier = modifier
        .pointerInput(Unit) {
            detectTransformGestures { centroid, pan, gestureZoom, _ ->
                val oldScale = zoom
                val newScale = zoom * gestureZoom
                offset = (offset + centroid / oldScale) - (centroid / newScale + pan / oldScale)
                zoom = newScale
            }
        }
        .graphicsLayer {
            translationX = -offset.x * zoom
            translationY = -offset.y * zoom
            scaleX = zoom
            scaleY = zoom
            transformOrigin = TransformOrigin(0f, 0f)
        }
        .background(Color.Blue)
        .fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(10),
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            userScrollEnabled = false
        ) {
            items(sections) {
                Image(
                    painter = painterResource(R.drawable.clubs_king),
                    contentDescription = "card"
                )
            }
        }
    }
}
