package com.fairytale.scrambledwordgame.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScoreScreen( message: String = "", score:String,onClick:()->Unit,){
    BackHandler(enabled = true) {

    }
    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
        if(message.isNotEmpty()){
            Text(text = message, modifier = Modifier.padding(10.dp), fontSize = 30.sp, fontFamily = FontFamily.Cursive, lineHeight = 40.sp, color =  Color.Magenta)
        }
        Text(text = "Your Score is ${score}", modifier = Modifier.padding(0.dp,10.dp,0.dp,10.dp), fontSize = 30.sp, fontFamily = FontFamily.Cursive)

        Button(onClick = onClick
        ) {
            Text("StartOver")
        }
    }
}