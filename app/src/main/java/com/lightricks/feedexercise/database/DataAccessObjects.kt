package com.lightricks.feedexercise.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable

const val DATABASE_NAME = "FeedItemEntity"

@Dao
interface FeedDao {
    @Query("SELECT * FROM "+DATABASE_NAME)
    fun getAllLiveData(): LiveData<List<FeedItemEntity>>

    @Query("SELECT COUNT(id) FROM "+ DATABASE_NAME)
    fun getCount():Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg itemEntities: FeedItemEntity): Completable

    @Query("DELETE FROM "+DATABASE_NAME)
    fun deleteAll(): Completable
}