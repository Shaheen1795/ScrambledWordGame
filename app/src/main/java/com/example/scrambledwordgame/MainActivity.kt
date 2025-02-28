package com.example.scrambledwordgame

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.times
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.scrambledwordgame.data.Words
import com.example.scrambledwordgame.ui.screens.HomeScreenPage
import com.example.scrambledwordgame.ui.screens.ScoreScreen
import com.example.scrambledwordgame.ui.screens.ShowShuffledWord
import com.example.scrambledwordgame.ui.theme.ScrambledWordGameTheme
import com.example.scrambledwordgame.viewmodel.GameStatus
import com.example.scrambledwordgame.viewmodel.GameUiState
import com.example.scrambledwordgame.viewmodel.GameViewModel
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScrambledWordGameTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val gameViewModel: GameViewModel by viewModels()
                    GameNavigationGraph(this, gameViewModel,modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun GameNavigationGraph( lifecycleOwner: LifecycleOwner,gameViewModel: GameViewModel ,modifier:Modifier) {

    val navController = rememberNavController()

    NavHost(navController, startDestination = GameStatus.STARTED.name) {

        composable(route = GameStatus.STARTED.name) {
            HomeScreenPage {
                gameViewModel.startTimerForCurrentSession()
                navController.navigate("${GameStatus.IN_PROGRESS.name}/${gameViewModel.getCurrWord()}") }
        }
        composable(route = "${GameStatus.IN_PROGRESS.name}/{currentWord}") { navBackStackEntry ->
            val current = navBackStackEntry.arguments?.getString("currentWord")
            current?.let { it ->
                ShowShuffledWord(
                    {
                        gameViewModel.validateWord(it)
                    }, it, {
                        navController.navigate(GameStatus.FINISHED.name)
                    }
                )
            }
        }
        composable(route = GameStatus.FINISHED.name) {
            ScoreScreen(getWordScorePercentage(gameViewModel.score)) {
                navController.navigate(GameStatus.STARTED.name)
                gameViewModel.actionOnEndGame()
                gameViewModel.reset()
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
                        }
                    }
                    is GameUiState.STARTED -> navController.navigate(GameStatus.STARTED.name)
                    is GameUiState.FINISHED -> navController.navigate(it.event.name)
                }
            }

        }
    }

   fun getWordScorePercentage(score:Int):String{

       return (score*100/Words.LIST_SIZE).toString()
   }
