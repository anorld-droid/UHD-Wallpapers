package com.anorlddroid.wallpapers4e.data.database

import com.anorlddroid.wallpapers4e.data.network.pojo.UnsplashPhoto
import com.anorlddroid.wallpapers4e.data.network.pojo.UnsplashUrls
import com.anorlddroid.wallpapers4e.data.network.pojo.UnsplashUser

object UnsplashConvertors {
    fun List<RandomPhotos>.toUnsplashPhotos(): List<UnsplashPhoto> {
        val unsplashPhotos: MutableList<UnsplashPhoto> = mutableListOf()
        for (photo in this) {
            if (photo.urlsEntity != null && photo.userEntity != null) {
                val unsplashPhoto = UnsplashPhoto(
                    id = photo.randomEntity.random_id,
                    created_at = photo.randomEntity.created_at,
                    likes = photo.randomEntity.likes,
                    description = photo.randomEntity.description,
                    urls = UnsplashUrls(
                        small = photo.urlsEntity.small,
                        medium = photo.urlsEntity.medium
                    ),
                    user = UnsplashUser(
                        name = photo.userEntity.name,
                        profile_image = UnsplashUrls(
                            small = photo.userEntity.profile_image,
                            medium = null
                        )
                    )
                )
                unsplashPhotos.add(unsplashPhoto)
            }
        }
        return unsplashPhotos
    }

    fun toRandomEntity(unsplashPhoto: UnsplashPhoto): RandomEntity {
        return RandomEntity(
            random_id = unsplashPhoto.id,
            created_at = unsplashPhoto.created_at,
            likes = unsplashPhoto.likes,
            description = unsplashPhoto.description,
        )
    }

    fun List<RecentPhotos>.convertToUnsplashPhotos(): List<UnsplashPhoto> {
        val unsplashPhotos: MutableList<UnsplashPhoto> = mutableListOf()
        for (photo in this) {
            if (photo.urlsEntity != null && photo.userEntity != null) {
                val unsplashPhoto = UnsplashPhoto(
                    id = photo.recentEntity.recent_id,
                    created_at = photo.recentEntity.created_at,
                    likes = photo.recentEntity.likes,
                    description = photo.recentEntity.description,
                    urls = UnsplashUrls(
                        small = photo.urlsEntity.small,
                        medium = photo.urlsEntity.medium
                    ),
                    user = UnsplashUser(
                        name = photo.userEntity.name,
                        profile_image = UnsplashUrls(
                            small = photo.userEntity.profile_image,
                            medium = null
                        )
                    )
                )
                unsplashPhotos.add(unsplashPhoto)
            }
        }
        return unsplashPhotos
    }

    fun toRecentEntity(unsplashPhoto: UnsplashPhoto): RecentEntity {
        return RecentEntity(
            recent_id = unsplashPhoto.id,
            created_at = unsplashPhoto.created_at,
            likes = unsplashPhoto.likes,
            description = unsplashPhoto.description,
        )
    }

    fun toUrlsEntity(unsplashPhoto: UnsplashPhoto): UrlsEntity {
        return UrlsEntity(
            photoId = unsplashPhoto.id,
            small = unsplashPhoto.urls.small,
            medium = unsplashPhoto.urls.medium
        )
    }

    fun toUserEntity(unsplashPhoto: UnsplashPhoto): UserEntity {
        return UserEntity(
            photoId = unsplashPhoto.id,
            name = unsplashPhoto.user.name,
            profile_image = unsplashPhoto.user.profile_image.small
        )
    }
}