package com.example.scrambledwordgame.data

object Scores {

    val scores = mutableListOf<Score>()
}

data class Score(val name:String = "", val score:Float = 0.0F, val time:Long)