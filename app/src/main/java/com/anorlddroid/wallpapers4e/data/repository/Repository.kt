package com.anorlddroid.wallpapers4e.data.repository

import com.anorlddroid.wallpapers4e.data.database.CategoryEntity
import com.anorlddroid.wallpapers4e.data.database.SettingsEntity
import com.anorlddroid.wallpapers4e.data.database.UHDDatabase
import com.anorlddroid.wallpapers4e.data.database.UnsplashConvertors
import com.anorlddroid.wallpapers4e.data.network.APIResult
import com.anorlddroid.wallpapers4e.data.network.RetrofitHelper
import com.anorlddroid.wallpapers4e.data.network.pojo.UnsplashPhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class Repository(private val dbInstance: UHDDatabase) {
    private val unsplashApi = RetrofitHelper.getInstance()
    suspend fun searchPhoto(query: String): Flow<APIResult<List<UnsplashPhoto>>> =
        flow {
            emit(APIResult.Loading(null, true))
            try {
                val unsplashPhotos: MutableList<UnsplashPhoto> = mutableListOf()
                var response = unsplashApi.searchPhoto(criteria = query)
                var responseList = response.body()?.results
                for (i in 2..3) {
                    response = unsplashApi.searchPhoto(criteria = query, page = i)
                    responseList = response.body()?.results
                    if (responseList != null) {
                        unsplashPhotos.addAll(responseList)
                    }
                }
                if (response.isSuccessful) {
                    if (responseList != null) {
                        emit(APIResult.Success(unsplashPhotos))
                    }
                } else {
                    response.errorBody()?.let { responseBody ->
                        val errorMessage = responseBody.toString()
                        responseBody.close()
                        emit(APIResult.Error(errorMessage))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    suspend fun downloadRandomPhotos() =
        withContext(Dispatchers.Default) {
            try {
                for (i in 1..2) {
                    val response = unsplashApi.getRandomPhotos(page = i)
                    if (response.isSuccessful) {
                        val photosList = response.body()
                        if (photosList != null) {
                            for (photo in photosList) {
                                val randomEntity = UnsplashConvertors.toRandomEntity(photo)
                                dbInstance.randomDao().insert(randomEntity)
                                val urlsEntity = UnsplashConvertors.toUrlsEntity(photo)
                                dbInstance.urlsDao().insertUrl(urlsEntity)
                                val userEntity = UnsplashConvertors.toUserEntity(photo)
                                dbInstance.userDao().insertUser(userEntity)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    suspend fun getRandomPhotos(): Flow<APIResult<List<UnsplashPhoto>>> =
        flow {
            emit(APIResult.Loading(null, true))
            val randomPhotos = dbInstance.randomDao().getRandomPhotos().distinctUntilChanged()
            randomPhotos.collect {
                UnsplashConvertors.apply {
                    val unsplashPhotos = it.toUnsplashPhotos()
                    emit(APIResult.Success(unsplashPhotos))
                }
            }
        }


    suspend fun downloadRecentPhotos() =
        withContext(Dispatchers.IO) {
            try {
                for (i in 1..2) {
                    val response = unsplashApi.getRecentPhotos(page = i)
                    if (response.isSuccessful) {
                        val photos = response.body()
                        if (photos != null) {
                            for (photo in photos) {
                                val recentEntity = UnsplashConvertors.toRecentEntity(photo)
                                dbInstance.recentDao().insert(recentEntity)
                                val urlsEntity = UnsplashConvertors.toUrlsEntity(photo)
                                dbInstance.urlsDao().insertUrl(urlsEntity)
                                val userEntity = UnsplashConvertors.toUserEntity(photo)
                                dbInstance.userDao().insertUser(userEntity)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    suspend fun getRecentPhotos(): Flow<APIResult<List<UnsplashPhoto>>> =
        flow {
            emit(APIResult.Loading(null, true))
            val recentPhotos = dbInstance.recentDao().getRecentPhotos().distinctUntilChanged()
            recentPhotos.collect {
                UnsplashConvertors.apply {
                    val unsplashPhotos = it.convertToUnsplashPhotos()
                    emit(APIResult.Success(unsplashPhotos))
                }
            }
        }

    fun getAllCategories() =
        dbInstance.categoriesDao().getAllCategories().distinctUntilChanged()

    suspend fun insertCategory(name: CategoryEntity) =
        dbInstance.categoriesDao().insert(name)

    suspend fun getSetting(name: String) = dbInstance.settingsDao().getSetting(name)
    suspend fun insertSetting(entity: SettingsEntity) =
        dbInstance.settingsDao().insert(entity)
}