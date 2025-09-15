package com.fairytale.scrambledwordgame

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fairytale.scrambledwordgame.database.DbProvider
import com.fairytale.scrambledwordgame.ui.screens.HomeScreenPage
import com.fairytale.scrambledwordgame.ui.screens.ScoreScreen
import com.fairytale.scrambledwordgame.ui.screens.ShowShuffledWord
import com.fairytale.scrambledwordgame.ui.theme.ScrambledWordGameTheme
import com.fairytale.scrambledwordgame.utils.getWordScorePercentage
import com.fairytale.scrambledwordgame.viewmodels.GameStatus
import com.fairytale.scrambledwordgame.viewmodels.GameUiState
import com.fairytale.scrambledwordgame.viewmodels.GameViewModel
import com.fairytale.scrambledwordgame.viewmodels.GameViewModel.GameViewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = DbProvider.build(applicationContext)
        val dao = db.scoreDao()
        val gameViewModel: GameViewModel by viewModels {
            GameViewModelFactory(dao)
        }
        setContent {
            ScrambledWordGameTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    GameNavigationGraph(this, gameViewModel,modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}





@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun GameNavigationGraph( lifecycleOwner: LifecycleOwner,gameViewModel: GameViewModel ,modifier:Modifier) {

    val navController = rememberNavController()

    NavHost(navController, startDestination = GameStatus.STARTED.name) {

        composable(route = GameStatus.STARTED.name) {
            HomeScreenPage(
                gameViewModel,
                {
                    gameViewModel.startTimer()
                    navController.navigate("${GameStatus.IN_PROGRESS.name}/${gameViewModel.getCurrWord(false)}")
                })
        }
        composable(route = "${GameStatus.IN_PROGRESS.name}/{currentWord}") { navBackStackEntry ->
            val current = navBackStackEntry.arguments?.getString("currentWord")
            current?.let { it ->
                ShowShuffledWord(
                    {
                        gameViewModel.validateWord(it)
                    }, it, {
                        navController.navigate(GameStatus.FINISHED.name){
                            popUpTo(GameStatus.IN_PROGRESS.name){
                                inclusive = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },gameViewModel
                )
            }
        }
        composable(route = GameStatus.FINISHED.name) {
            ScoreScreen(gameViewModel.message,getWordScorePercentage(gameViewModel.score).roundToInt().toString()) {
                gameViewModel.actionOnEndGame()
                gameViewModel.reset()
                navController.navigate(GameStatus.STARTED.name)
            }
        }
    }


        lifecycleOwner.lifecycleScope.launch {

            gameViewModel.flow.collectLatest {
                when (it) {
                    is GameUiState.PROGRESS -> {
                        navController.navigate("${it.event.name}/${(it).word}"){
                            popUpTo(GameStatus.IN_PROGRESS.name){
                                inclusive = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    is GameUiState.STARTED -> navController.navigate(GameStatus.STARTED.name){
                        popUpTo(GameStatus.STARTED.name){
                            inclusive = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                    is GameUiState.FINISHED -> navController.navigate(it.event.name){
                        popUpTo(GameStatus.IN_PROGRESS.name){
                            inclusive = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }

        }
    }


