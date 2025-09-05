package com.fairytale.scrambledwordgame.database

import androidx.room.Room

object DbProvider {
    fun build(context: android.content.Context): AppDb =
        Room.databaseBuilder(context, AppDb::class.java, "app.db")
            .fallbackToDestructiveMigration() // replace with real migrations in prod
            .build()
}