package com.anorlddroid.wallpapers4e.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NetworkCheck
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.anorlddroid.wallpapers4e.R
import com.anorlddroid.wallpapers4e.ui.theme.Neutral0
import com.anorlddroid.wallpapers4e.ui.theme.Neutral7
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay
import kotlin.system.exitProcess

@Composable
fun ConnectionNotification(
    finishActivity: () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    val showConnectionNotification = remember { mutableStateOf(true) }
    val connection by connectivityState()
    val isConnected = remember { mutableStateOf(connection == ConnectionState.Available) }
//    if (isConnected.value) {
//        isConnectedNotification(
//            isConnected = isConnected,
//            showConnectionNotification = showConnectionNotification,
//            systemUiController = systemUiController
//        )
//    } else {
//        notConnectedNotification(systemUiController)
//    }
    ConnectionAlertDialog(finishActivity = finishActivity)
}

@Composable
fun ConnectionAlertDialog(finishActivity: () -> Unit) {
    val connection by connectivityState()
    val isConnected = connection == ConnectionState.Available
    if (!isConnected) {
        AlertDialog(
            backgroundColor = Neutral7,
            contentColor = Neutral0,
            shape = MaterialTheme.shapes.medium,
            title = {
                Text(
                    text = "No internet connection",
                    style = MaterialTheme.typography.h6,
                    color = Neutral0,
                )
            },
            text = {
                Text(
                    text = "Your internet connection seems to have dropped out.",
                    style = MaterialTheme.typography.subtitle1,
                    color = Neutral0,
                )
            },
            onDismissRequest = { },
            confirmButton = {
                TextButton(onClick = {
                    finishActivity()
                    exitProcess(0)
                }) {
                    Text(
                        text = "Exit",
                        style = MaterialTheme.typography.h6,
                        color = Neutral0,
                    )
                }
            }
        )
    }
}

@Composable
fun isConnectedNotification(
    isConnected: MutableState<Boolean>,
    showConnectionNotification: MutableState<Boolean>,
    systemUiController: SystemUiController
) {
    val statusBarColor = MaterialTheme.colors.onBackground
    LaunchedEffect(key1 = isConnected, block = {
        delay(3000L)
        showConnectionNotification.value = false
        systemUiController.setSystemBarsColor(color = statusBarColor)
    })
    if (showConnectionNotification.value) {
        systemUiController.setSystemBarsColor(color = MaterialTheme.colors.primary)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsHeight()
                .background(MaterialTheme.colors.primary)
        ) {
            Icon(
                imageVector = Icons.Outlined.NetworkCheck,
                contentDescription = null,
                tint = MaterialTheme.colors.secondary
            )
            Text(
                text = stringResource(id = R.string.back_online),
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(5.dp)
            )
        }
    }
}

@Composable
fun notConnectedNotification(
    systemUiController: SystemUiController
) {
    systemUiController.setSystemBarsColor(color = MaterialTheme.colors.background)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.background)
    ) {
        Icon(
            imageVector = Icons.Outlined.WifiOff,
            contentDescription = null,
            tint = MaterialTheme.colors.secondary
        )
        Text(
            text = stringResource(id = R.string.offline),
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.secondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(5.dp)
        )
    }
}