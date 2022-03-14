package com.anorlddroid.wallpapers4e.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow


/**
 * [Room] DAO for [Random images from unsplash] related operations.
 */
@Dao
abstract class RecentDao {
    @Transaction
    @Query("SELECT * FROM recent")
    abstract fun getRecentPhotos(): Flow<List<RecentPhotos>>

    /**
     * The following methods should really live in a base interface. Unfortunately the Kotlin
     * Compiler which we need to use for Compose doesn't work with that.
     * TODO: remove this once we move to a more recent Kotlin compiler
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(recentEntity: RecentEntity): Long


}