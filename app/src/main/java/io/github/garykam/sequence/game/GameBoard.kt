package io.github.garykam.sequence.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.garykam.sequence.util.Zoomable

@Composable
fun GameBoard(modifier: Modifier = Modifier) {
    val grid = listOf(
        "b", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "b",
        "c6", "c5", "c4", "c3", "c2", "h1", "hk", "hq", "h10", "s10",
        "c7", "s1", "d2", "d3", "d4", "d5", "d6", "d7", "h9", "sq",
        "c8", "sk", "c6", "c5", "c4", "c3", "c2", "d8", "h8", "sk",
        "c9", "sq", "c7", "h6", "h5", "h4", "h1", "d9", "h7", "s1",
        "c10", "s10", "c8", "h7", "h2", "h3", "hk", "d10", "h6", "d2",
        "cq", "s9", "c9", "h8", "h9", "h10", "hq", "dq", "h5", "d3",
        "ck", "s8", "c10", "cq", "ck", "c1", "d1", "dk", "h4", "d4",
        "c1", "s7", "s6", "s5", "s4", "s3", "s2", "h2", "h3", "d5",
        "b", "d1", "dk", "dq", "d10", "d9", "d8", "d7", "d6", "b"
    )

    Zoomable {
        LazyVerticalGrid(
            columns = GridCells.Fixed(10),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            userScrollEnabled = false
        ) {
            items(grid) { card ->
                val context = LocalContext.current
                val drawableId = remember(card) {
                    context.resources.getIdentifier(
                        card,
                        "drawable",
                        context.packageName
                    )
                }

                Image(
                    painter = painterResource(drawableId),
                    contentDescription = "card"
                )
            }
        }
    }
}
