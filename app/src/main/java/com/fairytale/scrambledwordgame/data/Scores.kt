package com.fairytale.scrambledwordgame.data

import androidx.room.Entity
import androidx.room.PrimaryKey

object Scores {

    val scores = mutableListOf<Score>()
}

@Entity(tableName = "scores")
data class Score(@PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name:String = "", var score:Float = 0.0F, var time:Long)