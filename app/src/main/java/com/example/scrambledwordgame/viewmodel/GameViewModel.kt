package com.example.scrambledwordgame.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.scrambledwordgame.Hint
import com.example.scrambledwordgame.data.Level
import com.example.scrambledwordgame.data.Score
import com.example.scrambledwordgame.data.Scores
import com.example.scrambledwordgame.data.Words
import com.example.scrambledwordgame.utils.createShuffledDict
import com.example.scrambledwordgame.utils.getWordScorePercentage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random
enum class GameStatus{

    STARTED,
    IN_PROGRESS,
    FINISHED
}

sealed class GameUiState(open var event:GameStatus){

    data class STARTED(override var event: GameStatus= GameStatus.STARTED): GameUiState(event)
    data class PROGRESS(override var event: GameStatus= GameStatus.IN_PROGRESS, var word: String): GameUiState(event)
    data class FINISHED(override var event: GameStatus = GameStatus.FINISHED): GameUiState(event)
}

class GameViewModel: ViewModel() {

    private var mutableMap:MutableMap<String, String> = mutableMapOf()
    private var currentInd = Random.nextInt(0,10)
    val currentLevel = Level.L1
    var currentIndex by mutableIntStateOf(currentInd)
    var score by mutableFloatStateOf(0.0F)
    var wordStatus by mutableStateOf(false )
    private var usedIndexes =  mutableSetOf<Int>(currentInd)
    var channel = Channel<GameUiState>(1)
    var flow: Flow<GameUiState> = channel.receiveAsFlow()
    private var counter:Int by mutableIntStateOf(0)
    private var unlockHint:Int by mutableIntStateOf(4)
    private var currentHint by mutableStateOf(Hint("","",""))
    var showHintDialog by mutableStateOf(false)
    private var diffScore by mutableFloatStateOf(0.0F)
    var hintButtonText by mutableStateOf("Hint")


    var startTimer = 0L
    private var endTimer = 0L

    init{
        setMapOfWords(currentLevel)
    }

    fun startTimerForCurrentSession(){
        counter++
        startTimer = System.currentTimeMillis()
    }

    private fun endTimerForCurrentSession(){
        endTimer = System.currentTimeMillis()
    }

    private fun setMapOfWords(level: Level){
        createShuffledDict(mutableMap,level)
    }

    fun reset(){
        viewModelScope.launch {
              usedIndexes.clear()
              wordStatus = false
              channel.send(GameUiState.STARTED())

              withContext(Dispatchers.IO){
                  delay(1000)
                  score = 0.0F
              }
        }
    }

    fun skip(){
        viewModelScope.launch {
            wordStatus = false
            channel.send(GameUiState.PROGRESS(GameStatus.IN_PROGRESS, getCurrWord(true)))

        }
    }

    fun showDialogHint(){
        if(unlockHint>0){
            unlockHint--
            diffScore+=0.25F
        }
        showHintDialog = true
    }

    fun getHint():String{

        when(unlockHint){

            3 -> {
                hintButtonText = "More Hints"
                return currentHint.positionHint1
            }
            2 -> {
                return currentHint.positionHint1+"\n"+currentHint.positionHint2
            }
            1 -> {
                hintButtonText = "Reveal the word"
                return currentHint.positionHint1+"\n"+currentHint.positionHint2+"\n"+currentHint.meaningHint
            }

            else  -> {
                return currentHint.positionHint1+"\n"+currentHint.positionHint2+"\n"+currentHint.meaningHint+"\n"+Words.mapOfWords.getValue(currentLevel)[currentIndex]
            }

        }

    }

    fun validateWord(currentWord: String): Boolean {
        val status = currentWord == Words.mapOfWords.getValue(currentLevel)[currentIndex]
        wordStatus = status

        if(status) {
            score+=(OFFSET-diffScore)
            viewModelScope.launch {
                var nextWord = getCurrWord()
                if(nextWord.isNotEmpty()){
                    channel.send(GameUiState.PROGRESS(GameStatus.IN_PROGRESS, nextWord))
                }
                else {
                    channel.send(GameUiState.FINISHED())
                }
            }

        }

        return status
    }

    private fun fetchIndex(): Int {

        var index = Random.nextInt(0,Words.mapOfWords.getValue(currentLevel).size)
        while(usedIndexes.contains(index)){
            index = Random.nextInt(0,Words.mapOfWords.getValue(currentLevel).size)
        }
        usedIndexes.add(index)
        return index
    }

    fun actionOnEndGame(){
        endTimerForCurrentSession()
        Scores.scores.add(Score("Session $counter",getWordScorePercentage(score),endTimer-startTimer))
    }


    fun getCurrWord(skip:Boolean = false ): String{
        if(usedIndexes.size==Words.mapOfWords.getValue(currentLevel).size){
               return ""
        }

        if(wordStatus || skip){
            currentIndex = fetchIndex()
        }
        unlockHint = 4
        diffScore = 0.0F
        hintButtonText = "Hint"
        val currentWord = Words.mapOfWords.getValue(currentLevel)[currentIndex]
        currentHint = Hint("The word starts with ${currentWord[0]}",
            "The word ends with ${currentWord[currentWord.length-1]}",Words.mapOfMeanings.getOrDefault(currentWord,""))

        return mutableMap.getValue(currentWord)
    }

    companion object {
        const val OFFSET = 1.0F

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {

                GameViewModel()
            }
        }
    }
}