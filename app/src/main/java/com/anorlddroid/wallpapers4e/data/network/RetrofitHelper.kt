package com.anorlddroid.wallpapers4e.data.network

import com.anorlddroid.wallpapers4e.data.network.pojo.SearchResults
import com.anorlddroid.wallpapers4e.data.network.pojo.UnsplashPhoto
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


private const val BASE_URL = "https://api.unsplash.com/"
const val CLIENT_ID = "" //TODO(Enter your client id from unsplash)
interface UnsplashApi {
    @GET("search/photos")
    suspend fun searchPhoto(
        @Query("client_id") client_id: String = CLIENT_ID,
        @Query("query") criteria: String,
        @Query("page") page: Int = 1,
        @Query("per_page") pageSize: Int = 30,
    ): Response<SearchResults>


    @GET("photos/random")
    suspend fun getRandomPhotos(
        @Query("page") page: Int = 1,
        @Query("per_page") per_page: Int = 30,
        @Query("count") amount: Int = 210,
        @Query("client_id") client_id: String = CLIENT_ID
    ): Response<List<UnsplashPhoto>>

    @GET("photos")
    suspend fun getRecentPhotos(
        @Query("page") page: Int = 1,
        @Query("per_page") per_page: Int = 30,
        @Query("client_id") client_id: String = CLIENT_ID
    ): Response<List<UnsplashPhoto>>
}

object RetrofitHelper {
    fun getInstance(): UnsplashApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(UnsplashApi::class.java)
    }
}