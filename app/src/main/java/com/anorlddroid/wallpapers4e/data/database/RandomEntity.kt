package com.anorlddroid.wallpapers4e.data.database

import androidx.compose.runtime.Immutable
import androidx.room.*

@Entity(
    tableName = "random",
    indices = [
        Index("random_id", unique = true)
    ]
)
@Immutable
data class RandomEntity(
    @PrimaryKey() @ColumnInfo(name = "random_id") val random_id: String,
    @ColumnInfo(name = "created_at") val created_at: String,
    @ColumnInfo(name = "likes") val likes: Int,
    @ColumnInfo(name = "description") val description: String?,
)

data class RandomPhotos(
    @Embedded val randomEntity: RandomEntity,
    @Relation(parentColumn = "random_id", entityColumn = "photoId") val urlsEntity: UrlsEntity?,
    @Relation(parentColumn = "random_id", entityColumn = "photoId") val userEntity: UserEntity?
)



