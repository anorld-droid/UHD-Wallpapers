package com.anorlddroid.wallpapers4e.data.network.pojo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchResults(
    val total: Int,
    val total_pages: Int,
    val results: List<UnsplashPhoto>
) : Parcelable