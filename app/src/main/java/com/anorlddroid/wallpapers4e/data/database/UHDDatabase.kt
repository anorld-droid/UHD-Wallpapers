package com.anorlddroid.wallpapers4e.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * The [RoomDatabase] we use in this app.
 */
@Database(
    entities = [
        CategoryEntity::class,
        SettingsEntity::class,
        RandomEntity::class,
        UrlsEntity::class,
        UserEntity::class,
        RecentEntity::class,
    ],
    version = 3,
    exportSchema = false
)
abstract class UHDDatabase : RoomDatabase() {
    abstract fun categoriesDao(): CategoriesDao
    abstract fun settingsDao(): SettingsDao
    abstract fun randomDao(): RandomDao
    abstract fun recentDao(): RecentDao
    abstract fun urlsDao(): UrlsDao
    abstract fun userDao(): UserDao


    companion object {
        @Volatile
        private var INSTANCE: UHDDatabase? = null
        fun getDatabase(context: Context): UHDDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UHDDatabase::class.java,
                    "uhd_wallpaper_db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
