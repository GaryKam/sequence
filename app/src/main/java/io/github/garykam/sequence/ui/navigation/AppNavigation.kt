package io.github.garykam.sequence.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.garykam.sequence.ui.creategame.CreateGameScreen
import io.github.garykam.sequence.ui.game.GameScreen
import io.github.garykam.sequence.ui.joingame.JoinGameScreen
import io.github.garykam.sequence.ui.landing.LandingScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: Destination = Landing
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Landing> {
            LandingScreen(
                modifier = Modifier.fillMaxSize(),
                onJoinGameClick = { navController.navigate(JoinGame) },
                onCreateGameClick = { navController.navigate(CreateGame) }
            )
        }

        composable<JoinGame> {
            JoinGameScreen(
                modifier = Modifier.fillMaxSize(),
                onBack = { navController.navigate(Landing) }
            )
        }

        composable<CreateGame> {
            CreateGameScreen(
                modifier = Modifier.fillMaxSize(),
                onBack = { navController.navigate(Landing) }
            )
        }

        composable<Game> {
            GameScreen(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
