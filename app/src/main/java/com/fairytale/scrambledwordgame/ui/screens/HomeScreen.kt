package com.fairytale.scrambledwordgame.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fairytale.scrambledwordgame.R
import com.fairytale.scrambledwordgame.data.Score
import com.fairytale.scrambledwordgame.ui.theme.Yellow
import com.fairytale.scrambledwordgame.ui.theme.YellowishWhte
import com.fairytale.scrambledwordgame.ui.InfoDialog
import com.fairytale.scrambledwordgame.utils.formatMillisToMinutesAndSeconds
import com.fairytale.scrambledwordgame.viewmodels.GameViewModel
import com.fairytale.scrambledwordgame.viewmodels.HomeScreenUiState
import com.fairytale.scrambledwordgame.viewmodels.SortScores
import kotlin.math.roundToInt


@Composable
    fun HomeScreenPage(gameViewModel: GameViewModel, onClickAction:()-> Unit = {}){
        Column( modifier = Modifier
            .fillMaxSize()
            .background(Yellow),) {

            Box(
                modifier = Modifier
                    .background(Yellow)
                    .fillMaxWidth()
                    ,
            ){
                Row(modifier = Modifier.align(Alignment.TopStart).background(YellowishWhte)){
                    BasicDropdownMenu(modifier = Modifier.padding(2.dp))
                }
            }
            Box(
                modifier = Modifier
                    .background(Yellow)
                    .fillMaxHeight(),
            ){
                val uiState = gameViewModel.homeScreenUiState.collectAsState()
                Column(modifier = Modifier.align(Alignment.TopCenter).padding(10.dp)) {
                    when(val state = uiState.value){
                        is HomeScreenUiState.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                        is HomeScreenUiState.Success -> {
                            Text("Scoreboard", fontFamily = FontFamily.Monospace, modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(5.dp, 5.dp, 5.dp, 5.dp), fontSize = 20.sp
                                , color = Color.DarkGray)
                            ThreeColumnTable(state.scores, gameViewModel)
                        }
                        is HomeScreenUiState.Error -> {
                            InfoDialog("Error","Something went wrong") {
                                gameViewModel.closeDialog()
                            }
                        }
                    }

                }
                Button(
                    onClickAction, modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(0.dp, 10.dp, 0.dp, 100.dp)) {
                    Text("Start game")
                }
            }

        }


    }

@Composable
fun TableHeaderCell(text: String, modifier: Modifier = Modifier, addArrows:Boolean = false, gameViewModel: GameViewModel, directionState: Boolean = true) {
    var directionUp by remember { mutableStateOf(directionState) }
   Row(modifier = Modifier.width(120.dp)) {
       Text(
           text = text,
           modifier = modifier
               .padding(8.dp).weight(1f),
           fontWeight = FontWeight .Bold,
           textAlign = TextAlign.Center,
           fontSize = 10.sp,
           color = Color.DarkGray
       )
       if(addArrows){
           Image(painter =
               if(directionUp){
                   painterResource(R.drawable.ic_up_foreground)
               }
               else painterResource(R.drawable.ic_down_foreground), modifier = Modifier.clickable{

               if(directionUp==true && text == "Score"){
                   gameViewModel.getScores(SortScores.SCORE_DESC, !directionUp)
               }
               else if(directionUp==false && text == "Score"){
                   gameViewModel.getScores(SortScores.SCORE_ASC, !directionUp)
               }
               else if(directionUp==true && text == "Time"){
                   gameViewModel.getScores(SortScores.TIME_DESC, !directionUp)
               }
               else if(directionUp==false && text == "Time"){
                   gameViewModel.getScores(SortScores.TIME_ASC, !directionUp)
               }
               directionUp = !directionUp

           }.size(40.dp).padding(5.dp),
               contentDescription = "Sorting arrow",

              )
       }

   }
}
@Composable
fun TableCell(text: String, modifier: Modifier = Modifier) {

     Text(
        text = text,
        color = Color.DarkGray,
        modifier = modifier
            .padding(4.dp),
        textAlign = TextAlign.Center,
        maxLines = 1

    )
}

@Composable
fun ThreeColumnTable(tableData: List<Score>, gameViewModel: GameViewModel) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)) {
        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TableHeaderCell("Session", Modifier.weight(1f), false, gameViewModel)
            TableHeaderCell("Score", Modifier.weight(1f), true, gameViewModel)
            TableHeaderCell("Time", Modifier.weight(1f), true, gameViewModel)
        }

        LazyColumn {

            items(tableData){row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth().border(
                            width = 3.dp,
                            color = Color.LightGray
                        )
                ){
                    Image(
                        painter = painterResource( R.drawable.ic_close),
                        contentDescription = "Close",
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Black),
                        modifier = Modifier.clickable {
                            gameViewModel.deleteScore(row.id)
                        }.size(25.dp).padding(8.dp)
                    )
                    TableCell(row.name, Modifier.weight(1f).width(160.dp))
                    TableCell(row.score.roundToInt().toString( ), Modifier.weight(1f))
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

