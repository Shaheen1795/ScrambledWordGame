package com.fairytale.scrambledwordgame.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fairytale.scrambledwordgame.data.Score

@Dao
interface ScoreDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertScore(score: Score)

    @Query("SELECT * FROM scores ORDER BY score DESC")
    fun getAllScores():List<Score>
}