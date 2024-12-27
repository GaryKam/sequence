package io.github.garykam.sequence.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.garykam.sequence.ui.navigation.creategame.CreateGameScreen
import io.github.garykam.sequence.ui.navigation.joingame.JoinGameScreen
import io.github.garykam.sequence.ui.navigation.landing.LandingScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: Destination = Landing
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
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
                    modifier = Modifier.fillMaxSize()
                )
            }

            composable<CreateGame> {
                CreateGameScreen(

                )
            }
        }
    }
}
