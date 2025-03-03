package com.example.scrambledwordgame.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scrambledwordgame.R
import com.example.scrambledwordgame.data.Score
import com.example.scrambledwordgame.data.Scores.scores
import com.example.scrambledwordgame.ui.InfoDialog
import com.example.scrambledwordgame.ui.theme.Yellow
import com.example.scrambledwordgame.ui.theme.YellowishWhte
import com.example.scrambledwordgame.utils.formatMillisToMinutesAndSeconds


@Preview
    @Composable
    fun HomeScreenPage(onClickAction:()-> Unit = {}){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Yellow)
            .padding(20.dp),
    ){


            Row(modifier = Modifier.align(Alignment.TopStart).background(YellowishWhte).fillMaxSize().padding(0.dp,15.dp,0.dp,0.dp)){
                BasicDropdownMenu(modifier = Modifier.padding(2.dp))

            }
            Column(modifier = Modifier.align(Alignment.TopCenter).padding(10.dp)) {
                Text("Scoreboard", fontFamily = FontFamily.Monospace, modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(20.dp, 50.dp, 20.dp, 5.dp), fontSize = 20.sp)
                ThreeColumnTable(scores)
            }
            Button(
                onClickAction, modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(0.dp, 10.dp, 0.dp, 100.dp)) {
                Text("Start game")
            }
        }

    }

@Composable
fun TableHeaderCell(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
            .padding(8.dp),
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
}
@Composable
fun TableCell(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
            .padding(8.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
fun ThreeColumnTable(tableData: List<Score>) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)) {
        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TableHeaderCell("Session", Modifier.weight(1f))
            TableHeaderCell("Score", Modifier.weight(1f))
            TableHeaderCell("Time", Modifier.weight(1f))
        }

        LazyColumn {

            items(tableData){row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    TableCell(row.name, Modifier.weight(1f))
                    TableCell(row.score.toString(), Modifier.weight(1f))
                    TableCell(formatMillisToMinutesAndSeconds(row.time), Modifier.weight(1f))
                }

            }
        }
    }
}



@Composable
fun BasicDropdownMenu(modifier: Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val items = listOf("About")
    val context = LocalContext.current
    var showDialog by remember{ mutableStateOf(false) }
    if(showDialog){
       InfoDialog(title = "About", message = context.getString(R.string.about)) {
           showDialog = false
       }
    }
    Column(modifier = modifier) {
        IconButton(onClick = { expanded = true }) {
            Image(painter = painterResource(R.drawable.menu),"")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item) },
                    onClick = {
                        showDialog  = true
                        expanded = false
                    }
                )
            }
        }
    }
}

