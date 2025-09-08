package com.fairytale.scrambledwordgame.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fairytale.scrambledwordgame.Hint
import com.fairytale.scrambledwordgame.data.Level
import com.fairytale.scrambledwordgame.data.Score
import com.fairytale.scrambledwordgame.data.Scores
import com.fairytale.scrambledwordgame.data.Words
import com.fairytale.scrambledwordgame.database.ScoreDao
import com.fairytale.scrambledwordgame.utils.createShuffledDict
import com.fairytale.scrambledwordgame.utils.getWordScorePercentage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

sealed class HomeScreenUiState(){
    object Loading : HomeScreenUiState()
    data class Success(val scores: List<Score>) : HomeScreenUiState()
    data class Error(val exception: Throwable) : HomeScreenUiState()
}

class GameViewModel(dao: ScoreDao): ViewModel() {

    private var mutableMap:MutableMap<String, String> = mutableMapOf()
    private var size = Words.mapOfWords.getValue(Level.L1).size
    private var currentInd = Random.nextInt(0,size)
    val currentLevel = Level.L1
    var currentIndex by mutableIntStateOf(currentInd)
    var score by mutableFloatStateOf(0.0F)
    var wordStatus by mutableStateOf(false )
    private var usedIndexes =  mutableListOf<Int>()
    var channel = Channel<GameUiState>(1)
    var flow: Flow<GameUiState> = channel.receiveAsFlow()
    private var counter:Int by mutableIntStateOf(0)
    private var unlockHint:Int by mutableIntStateOf(4)
    private var currentHint by mutableStateOf(Hint("","",""))
    var showHintDialog by mutableStateOf(false)
    private var diffScore by mutableFloatStateOf(0.0F)
    var hintButtonText by mutableStateOf("Hint")
    var _homeScreenUiState = MutableStateFlow<HomeScreenUiState>(HomeScreenUiState.Loading)
    val homeScreenUiState: StateFlow<HomeScreenUiState>
        get() = _homeScreenUiState.asStateFlow()

    var startTimer = 0L
    private var endTimer = 0L
    private val scoreDao: ScoreDao = dao
    private var scoreList = mutableListOf<Score>()
    var message by mutableStateOf("")

    init{
        setMapOfWords(currentLevel)
        getScores()
        populateIndex()
    }

    fun populateIndex(){
        for(i in 0 until size){
            usedIndexes.add(i)
        }
        usedIndexes.remove(currentIndex)
    }

    fun closeDialog(){
        _homeScreenUiState.value = HomeScreenUiState.Success(scoreList)
    }

    fun getScores(){

        _homeScreenUiState.value = HomeScreenUiState.Loading
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                try{
                    val listOfScores = scoreDao.getAllScores()
                    scoreList.addAll(listOfScores)
                    if(listOfScores.isNullOrEmpty()){
                        _homeScreenUiState.value = HomeScreenUiState.Success(emptyList())
                    }
                    else _homeScreenUiState.value = HomeScreenUiState.Success(listOfScores)

                }
                catch (e:Exception){
                    Log.v("Exception","$e")
                    _homeScreenUiState.value = HomeScreenUiState.Error(e)
                }
            }
        }
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
              scoreList.clear()
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

    private fun fetchIndex(skip: Boolean = false ): Int {
        var temp = currentIndex
        var index = Random.nextInt(0, usedIndexes.size)
        var currentValue = usedIndexes[index]
        usedIndexes.remove(currentValue)
        if(skip) usedIndexes.add(temp)
        return currentValue
    }

    fun actionOnEndGame(){
        endTimerForCurrentSession()
        val currScore = Score(scoreList.size + 1,"Session ${scoreList.size + 1}",getWordScorePercentage(score),endTimer-startTimer)
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                scoreDao.insertScore(currScore)
            }
            getScores()
        }
    }


    fun getCurrWord(skip:Boolean = false ): String{
        if(usedIndexes.isEmpty()){
               viewModelScope.launch {
                   channel.send(GameUiState.FINISHED(GameStatus.FINISHED,
                   ))
                   message =  "Congratulations, You mastered $size GRE words"
               }
            return ""
        }
        if(wordStatus || skip){
            currentIndex = fetchIndex(skip)
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
    }

    class GameViewModelFactory(private val dao: ScoreDao) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return GameViewModel(dao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}