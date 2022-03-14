package com.anorlddroid.wallpapers4e.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow


/**
 * [Room] DAO for [Category] related operations.
 */
@Dao
abstract class CategoriesDao {
    @Query("SELECT name FROM categories")
    abstract fun getAllCategories(): Flow<List<String>>
    /**
     * The following methods should really live in a base interface. Unfortunately the Kotlin
     * Compiler which we need to use for Compose doesn't work with that.
     * TODO: remove this once we move to a more recent Kotlin compiler
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(name: CategoryEntity): Long
}