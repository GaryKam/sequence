package io.github.garykam.sequence.util

import android.content.Context
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

object ScreenUtil {
    fun hideSystemBars(context: Context) {
        (context as ComponentActivity).enableEdgeToEdge()
    }

    fun showSystemBars(
        context: Context,
        color: Color
    ) {
        (context as ComponentActivity).window.apply {
            statusBarColor = color.toArgb()
            navigationBarColor = color.toArgb()
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
    }
}
