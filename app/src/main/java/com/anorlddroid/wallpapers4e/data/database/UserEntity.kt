package com.anorlddroid.wallpapers4e.data.database

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "r_user",
    indices = [
        Index("id", unique = true)
    ]
)
@Immutable
data class UserEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "photoId") val photoId: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "profile_image") val profile_image: String,
)