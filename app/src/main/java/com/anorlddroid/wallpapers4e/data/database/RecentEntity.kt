package com.anorlddroid.wallpapers4e.data.database

import androidx.compose.runtime.Immutable
import androidx.room.*

@Entity(
    tableName = "recent",
    indices = [
        Index("recent_id", unique = true)
    ]
)
@Immutable
data class RecentEntity(
    @PrimaryKey() @ColumnInfo(name = "recent_id") val recent_id: String,
    @ColumnInfo(name = "created_at") val created_at: String,
    @ColumnInfo(name = "likes") val likes: Int,
    @ColumnInfo(name = "description") val description: String?,
)

data class RecentPhotos(
    @Embedded val recentEntity: RecentEntity,
    @Relation(parentColumn = "recent_id", entityColumn = "photoId") val urlsEntity: UrlsEntity?,
    @Relation(parentColumn = "recent_id", entityColumn = "photoId") val userEntity: UserEntity?
)