package com.anorlddroid.wallpapers4e.data.network.pojo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UnsplashUrls(
    val small: String,
    val medium: String?,
) : Parcelable