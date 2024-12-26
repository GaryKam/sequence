package io.github.garykam.sequence

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.garykam.sequence.ui.navigation.AppNavigation
import io.github.garykam.sequence.ui.theme.SequenceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SequenceTheme {
                AppNavigation()
            }
        }
    }
}
