package com.example.scrambledwordgame.utils

import com.example.scrambledwordgame.data.Level
import com.example.scrambledwordgame.data.Score
import com.example.scrambledwordgame.data.Words
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.random.Random

fun shuffleWord(word: String): String {

    val current = StringBuilder(word)
    val reversed = current.reverse()

    val random = Random.nextInt(0,word.length/2+1)
    return  reversed.substring(random) + reversed.substring(0,random)

}

fun createShuffledDict(mapOfWords: MutableMap<String, String >,level: Level){
    val words = Words.mapOfWords.getValue(level)
    for(word in words){
        mapOfWords[word] = shuffleWord(word)
    }
}

fun getPairofScoreMappings(listOfscores:List<Score>):List<Pair<String,String>>{

    var mutableList = mutableListOf<Pair<String,String>>()
    for(score in listOfscores){
        mutableList.add(Pair(score.name,score.score.toString()))
    }
    return mutableList

}

fun formatMillisToMinutesAndSeconds(millis: Long): String {
    require(millis >= 0) { "Milliseconds must be non-negative" }

    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)

    return String.format(Locale.ENGLISH,"%02d:%02d", minutes, seconds)
}
