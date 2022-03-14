package com.anorlddroid.wallpapers4e.ui.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

sealed class ConnectionState {
    object Available : ConnectionState()
    object Unavailable : ConnectionState()
}

/**
 * get the current state of internet connection
 */
val Context.currentConnectionState: ConnectionState
    get() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return getCurrentConnectionState(connectivityManager)
    }

private fun getCurrentConnectionState(
    connectivityManager: ConnectivityManager
): ConnectionState {
    val connected = connectivityManager.allNetworks.any { network ->
        connectivityManager.getNetworkCapabilities(network)
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
    }
    return if (connected) ConnectionState.Available else ConnectionState.Unavailable
}

/**
 * observes network availability
 */

fun Context.observeConnectivityAsFlow() = callbackFlow {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val callback = NetworkCallback { connectionState -> trySend(connectionState) }
    val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()
    connectivityManager.registerNetworkCallback(networkRequest, callback)
    //set current state
    val currentState = getCurrentConnectionState(connectivityManager)
    trySend(currentState)

    //remove callback when not used
    awaitClose {
        //remove listeners
        connectivityManager.unregisterNetworkCallback(callback)
    }
}

private fun NetworkCallback(callback: (ConnectionState) -> Unit): ConnectivityManager.NetworkCallback {
    return object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            callback(ConnectionState.Available)
        }

        override fun onUnavailable() {
            callback(ConnectionState.Unavailable)
        }
    }
}

@Composable
fun connectivityState(): State<ConnectionState> {
    val context = LocalContext.current

    //creates a state connection with current connectivity state as initial value
    return produceState(initialValue = context.currentConnectionState) {
        context.observeConnectivityAsFlow().collect { value = it }
    }
}

