package com.anorlddroid.wallpapers4e.data.network

import com.anorlddroid.wallpapers4e.R
import com.anorlddroid.wallpapers4e.data.network.pojo.UnsplashPhoto

enum class APIStatus{
    SUCCESS, LOADING, ERROR
}

sealed class APIResult<out T >(val status: APIStatus, val data: T?, val message: String?){
    data class Success<out R>(val _data: R?): APIResult<R>(
        status = APIStatus.SUCCESS,
        data = _data,
        message = null
    )

    data class Error(val exception: String): APIResult<Nothing>(
        status = APIStatus.ERROR,
        data = null,
        message = exception
    )

    data class Loading<out R>(val _data: R?, val isLoading: Boolean): APIResult<R>(
        status = APIStatus.LOADING,
        data = _data,
        message = null
    )

}