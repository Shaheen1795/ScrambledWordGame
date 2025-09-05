package com.fairytale.scrambledwordgame.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fairytale.scrambledwordgame.data.Score

@Database(entities = [Score::class], version = 1, exportSchema = true)
abstract class AppDb : RoomDatabase() {
    abstract fun scoreDao(): ScoreDao
}
