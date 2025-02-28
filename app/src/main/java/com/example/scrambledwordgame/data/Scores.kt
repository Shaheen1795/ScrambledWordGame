package com.example.scrambledwordgame.data

object Scores {

    val scores = mutableListOf<Score>()
}

data class Score(val name:String = "", val score:Int = 0, val time:Long)