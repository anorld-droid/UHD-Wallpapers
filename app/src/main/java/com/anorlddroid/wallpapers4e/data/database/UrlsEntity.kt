package com.anorlddroid.wallpapers4e.data.database

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "r_urls",
    indices = [
        Index("id", unique = true)
    ]
)
@Immutable
data class UrlsEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "photoId") val photoId: String,
    @ColumnInfo(name = "small") val small: String,
    @ColumnInfo(name = "medium") val medium: String?,
)