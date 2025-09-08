package com.fairytale.scrambledwordgame.utils


import com.fairytale.scrambledwordgame.data.Level
import com.fairytale.scrambledwordgame.data.Words
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.random.Random

fun shuffleWord(word: String): String {
    val random = Random.nextInt(1,word.length)
    return  word.substring(random) + word.substring(0,random).reversed()
}

fun getWordScorePercentage(score:Float):Float{
    return ((score/(Words.wordsList_level1.size*1.0F))*100)
}

fun createShuffledDict(mapOfWords: MutableMap<String, String >,level: Level){
    val words = Words.mapOfWords.getValue(level)
    for(word in words){
        mapOfWords[word] = shuffleWord(word)
    }
}

fun formatMillisToMinutesAndSeconds(millis: Long): String {
    require(millis >= 0) { "Milliseconds must be non-negative" }

    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)

    return String.format(Locale.ENGLISH,"%02d:%02d", minutes, seconds)
}
