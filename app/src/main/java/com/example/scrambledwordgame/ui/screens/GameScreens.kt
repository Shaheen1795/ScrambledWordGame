package com.example.scrambledwordgame.ui.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scrambledwordgame.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect

@Composable
fun ShowShuffledWord(onValueChange:(String)-> Boolean = {false}, currentWord:String, onEndGame:() -> Unit = {}){
    Log.v("SHOW-SHUFFLED-SCREEN",currentWord)
    Column(modifier = Modifier.fillMaxSize().background(Color.White), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
        Text(text =currentWord , modifier = Modifier.padding(0.dp,10.dp,0.dp,10.dp), fontSize = 45.sp, fontFamily = FontFamily.Monospace)
        EditableText(onValueChange)
        Column(verticalArrangement = Arrangement.Bottom){
            Button(onEndGame) {
                Text("End game")
            }
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