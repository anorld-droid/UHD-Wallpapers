package com.anorlddroid.wallpapers4e.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * [Room] DAO for [users from unsplash] related operations.
 */
@Dao
abstract class UserDao {
    @Query("SELECT * FROM r_user")
    abstract fun getUsers(): Flow<List<UserEntity>>

    /**
     * The following methods should really live in a base interface. Unfortunately the Kotlin
     * Compiler which we need to use for Compose doesn't work with that.
     * TODO: remove this once we move to a more recent Kotlin compiler
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertUser(user: UserEntity): Long
}