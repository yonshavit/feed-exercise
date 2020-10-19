package com.lightricks.feedexercise.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable

const val TABLE_NAME = "FeedItemEntity"

@Dao
interface FeedDao {
    @Query("SELECT * FROM $TABLE_NAME")
    fun getAllLiveData(): LiveData<List<FeedItemEntity>>

    @Query("SELECT COUNT(id) FROM $TABLE_NAME")
    fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(itemEntities: List<FeedItemEntity>): Completable

    @Query("DELETE FROM $TABLE_NAME")
    fun deleteAll(): Completable
}