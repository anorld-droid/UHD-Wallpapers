package com.anorlddroid.wallpapers4e.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * [Room] DAO for [urls from unsplash] related operations.
 */
@Dao
abstract class UrlsDao {
    @Query("SELECT * FROM r_urls")
    abstract fun getUrls(): Flow<List<UrlsEntity>>

    /**
     * The following methods should really live in a base interface. Unfortunately the Kotlin
     * Compiler which we need to use for Compose doesn't work with that.
     * TODO: remove this once we move to a more recent Kotlin compiler
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertUrl(url: UrlsEntity): Long
}