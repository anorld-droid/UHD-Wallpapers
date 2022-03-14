package com.anorlddroid.wallpapers4e.data.database

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "settings",
    indices = [
        Index("name", unique = true)
    ]
)
@Immutable
data class SettingsEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "setting") var setting: String
)

class DataGenerator {
    companion object {
        fun insertSettings(): List<SettingsEntity> = listOf(
            SettingsEntity(name = "Theme", setting = "Auto"),
        )
    }
}