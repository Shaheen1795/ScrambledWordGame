package com.fairytale.scrambledwordgame.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.fairytale.scrambledwordgame.ui.InfoDialog
import com.fairytale.scrambledwordgame.viewmodels.GameViewModel
import com.fairytale.scrambledwordgame.viewmodels.TimerUiState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun ShowShuffledWord(onValueChange:(String)-> Boolean = {false}, currentWord:String="", onEndGame:() -> Unit = {}, gameViewModel: GameViewModel){
    BackHandler(enabled = true) {
        // Do nothing (blocks back press)
    }


    Box(modifier = Modifier.fillMaxSize().background(Color.White)
       ) {

        if(gameViewModel.showHintDialog){
            val message = gameViewModel.getHint()
            InfoDialog("Hint",message, onDismissRequest = {gameViewModel.showHintDialog = false})
        }

        Column(

            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TimerScreen(LocalLifecycleOwner.current, gameViewModel)
            Text(
                text = currentWord,
                modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 10.dp),
                fontSize = 45.sp,
                fontFamily = FontFamily.Monospace
            )
            EditableText(onValueChange)
            Row(modifier = Modifier.padding(5.dp)){
              Button(modifier = Modifier.padding(5.dp),onClick = {
                  gameViewModel.showDialogHint()
              }){
                  Text(gameViewModel.hintButtonText)
              }
                Button(modifier = Modifier.padding(5.dp), onClick = {
                    gameViewModel.skip()
                }){
                    Text("Skip")
                }
            }

        }
            Button(modifier = Modifier.align(Alignment.BottomCenter).padding(0.dp,0.dp,0.dp,30.dp), onClick = onEndGame) {
                Text("End game")
            }
    }
}

@Composable
fun TimerScreen(lifecycleOwner: LifecycleOwner, gameViewModel: GameViewModel){

    val lifecycleOwner: LifecycleOwner = lifecycleOwner

    var timing by remember {
        mutableStateOf("")
    }

    DisposableEffect(lifecycleOwner) {

        val observer = LifecycleEventObserver { _, event ->
        }

        lifecycleOwner.lifecycleScope.launch {
            gameViewModel.timerUiState.collectLatest {
                when(it){
                    is TimerUiState.CurrentTimeUiState -> {
                        timing = it.time
                    }
                }

            }
        }

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }

    }

    Text(timing)

}



@Preview
@Composable
fun EditableText(onValueChange:(String)-> Boolean = {false}){
    var currentVal by remember {  mutableStateOf("")}
    TextField(value = currentVal, singleLine = true, onValueChange = {
        currentVal = it
        onValueChange(it)
    } , modifier = Modifier.border(border = BorderStroke(5.dp, Color.DarkGray))
     , shape = CircleShape)
}