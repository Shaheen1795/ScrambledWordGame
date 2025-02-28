package com.example.scrambledwordgame.ui.screens

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
fun ScoreScreen(score:String,onClick:()->Unit){
    Column(modifier = Modifier.fillMaxSize().background(Color.White), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
        Text(text = "Your Score is ${score}", modifier = Modifier.padding(0.dp,10.dp,0.dp,10.dp), fontSize = 30.sp, fontFamily = FontFamily.Monospace)

        Button(onClick = onClick
        ) {
            Text("Start Over")

        }
    }
}