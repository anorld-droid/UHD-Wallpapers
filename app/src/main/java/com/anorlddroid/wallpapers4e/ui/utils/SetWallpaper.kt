package com.anorlddroid.wallpapers4e.ui.details

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.widget.Toast

fun setWallpaper(context: Context, bitmap: Bitmap, location: Int) {
    val wallpaperManager = WallpaperManager.getInstance(context)
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when (location) {
                0 -> {
                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM)
                    Toast.makeText(context, "Wallpaper applied to home screen", Toast.LENGTH_LONG)
                        .show()
                }
                1 -> {
                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
                    Toast.makeText(context, "Wallpaper applied to lock screen", Toast.LENGTH_LONG)
                        .show()
                }
                else -> {
                    wallpaperManager.setBitmap(bitmap, null, true)
                    Toast.makeText(
                        context,
                        "Wallpaper applied to home screen and lock screen",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } else {
            Toast.makeText(
                context,
                "Sorry, your phone doesn't allow those options",
                Toast.LENGTH_LONG
            ).show()
            wallpaperManager.setBitmap(bitmap)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}