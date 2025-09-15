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

    @Query("DELETE FROM scores")
    fun deleteAllScores()

    @Query("DELETE FROM scores WHERE id = :id")
    fun deleteScoreById(id: Int)

    @Query("SELECT * FROM scores")
    fun getAllScores():List<Score>

    @Query("SELECT * FROM scores ORDER BY score ASC")
    fun getScoresByScoreAscending():List<Score>

    @Query("SELECT * FROM scores ORDER BY time DESC")
    fun getScoresByTimeDescending():List<Score>

    @Query("SELECT * FROM scores ORDER BY time ASC")
    fun getScoresByTimeAscending():List<Score>

    @Query("SELECT * FROM scores ORDER BY score DESC")
    fun getScoresByScoreDescending():List<Score>
}