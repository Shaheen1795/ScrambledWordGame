package com.example.scrambledwordgame.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.room.util.copy
import com.example.scrambledwordgame.data.Level
import com.example.scrambledwordgame.data.Score
import com.example.scrambledwordgame.data.Scores
import com.example.scrambledwordgame.data.Words
import com.example.scrambledwordgame.getWordScorePercentage
import com.example.scrambledwordgame.utils.createShuffledDict
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
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
    var score:Int by mutableIntStateOf(0)
    var wordStatus by mutableStateOf(false )
    private var usedIndexes =  mutableSetOf<Int>(currentInd)
    var channel = Channel<GameUiState>(1)
    var flow: Flow<GameUiState> = channel.receiveAsFlow()
    private var counter:Int by mutableIntStateOf(0)

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
                  score = 0
              }
        }
    }


    fun validateWord(currentWord: String): Boolean {
        val status = currentWord == Words.mapOfWords.getValue(currentLevel)[currentIndex]
        wordStatus = status

        if(status) {
            score+=OFFSET
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
        Scores.scores.add(Score("Session $counter",getWordScorePercentage(score).toInt(),endTimer-startTimer))
    }

    fun getCurrWord(): String{
        if(usedIndexes.size==Words.mapOfWords.getValue(currentLevel).size){
               return ""
        }

        if(wordStatus){
            currentIndex = fetchIndex()
        }

        return mutableMap.getValue(Words.mapOfWords.getValue(currentLevel)[currentIndex])
    }

    companion object {
        const val OFFSET = 1

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {

                GameViewModel()
            }
        }
    }
}