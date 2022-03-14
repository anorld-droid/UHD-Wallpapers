package com.anorlddroid.wallpapers4e.data.database

import androidx.room.*

@Dao
abstract class SettingsDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(vararg entity: SettingsEntity): List<Long>

    @Query("SELECT setting FROM settings WHERE name = :name")
    abstract suspend fun getSetting(name: String): String
}
