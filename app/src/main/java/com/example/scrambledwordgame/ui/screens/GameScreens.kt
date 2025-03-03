package com.example.scrambledwordgame.ui.screens

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scrambledwordgame.R
import com.example.scrambledwordgame.ui.InfoDialog
import com.example.scrambledwordgame.viewmodel.GameViewModel

@Preview
@Composable
fun ShowShuffledWord(onValueChange:(String)-> Boolean = {false}, currentWord:String="", onEndGame:() -> Unit = {}, gameViewModel: GameViewModel = GameViewModel()){
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {

        if(gameViewModel.showHintDialog){
            val message = gameViewModel.getHint()
            InfoDialog("Hint",message, onDismissRequest = {gameViewModel.showHintDialog = false})
        }

        Column(

            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
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