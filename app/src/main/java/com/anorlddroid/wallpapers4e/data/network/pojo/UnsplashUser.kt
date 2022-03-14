package com.anorlddroid.wallpapers4e.data.network.pojo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UnsplashUser(
    val name: String,
    val profile_image: UnsplashUrls,
) : Parcelable