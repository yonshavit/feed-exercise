package com.lightricks.feedexercise.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(FeedItemEntity::class), version = 1)
abstract class FeedDatabase : RoomDatabase() {
    abstract fun feedDao(): FeedDao
}