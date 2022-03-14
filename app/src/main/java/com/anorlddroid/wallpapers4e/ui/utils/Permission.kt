package com.anorlddroid.wallpapers4e.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Permission(
    permission: String = android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
    permissionNotAvailableContent: @Composable () -> Unit = { },
    content: @Composable () -> Unit = { }
) {
    val permissionState = rememberPermissionState(permission = permission)
    val permissionNotGranted = remember { mutableStateOf(true) }
    PermissionRequired(
        permissionState = permissionState,
        permissionNotGrantedContent = {
            permissionNotGranted.value = true
        },
        permissionNotAvailableContent = permissionNotAvailableContent,
        content = content
    )
    if (permissionNotGranted.value) {
        SideEffect {
            permissionState.launchPermissionRequest()
        }
    }
}

