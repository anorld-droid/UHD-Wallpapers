package com.anorlddroid.wallpapers4e.data.network.pojo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UnsplashPhoto(
    val id: String,
    val created_at: String,
    val likes: Int,
    val description: String?,
    val urls: UnsplashUrls,
    val user: UnsplashUser
) : Parcelable