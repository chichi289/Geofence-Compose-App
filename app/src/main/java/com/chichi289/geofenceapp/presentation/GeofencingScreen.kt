package com.chichi289.geofenceapp.presentation

import android.Manifest
import android.location.Location
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import com.chichi289.geofenceapp.geofence.GeofenceBroadcastReceiver
import com.chichi289.geofenceapp.geofence.GeofenceManager
import com.chichi289.geofenceapp.notification.GeofenceNotification
import com.chichi289.geofenceapp.notification.GeofenceNotificationChannel
import com.chichi289.geofenceapp.util.CUSTOM_INTENT_GEOFENCE
import com.chichi289.geofenceapp.util.KEY_BOPAL
import com.chichi289.geofenceapp.util.KEY_ISKON
import com.chichi289.geofenceapp.util.KEY_SPACE_O_TECHNOLOGIES
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun GeofencingScreen() {
    val permissions = listOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    PermissionBox(
        permissions = permissions,
        requiredPermissions = listOf(permissions.first()),
    ) {
        // For Android 10 onwards, we need background permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            // For Android 13 onwards, we need post notification permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                PermissionBox(
                    permissions = listOf(Manifest.permission.POST_NOTIFICATIONS),
                ) {
                    AskBackgroundLocationPermission()
                }
            } else {
                AskBackgroundLocationPermission()
            }
        } else {
            GeofencingControls()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun AskBackgroundLocationPermission() {
    PermissionBox(
        permissions = listOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
    ) {
        GeofencingControls()
    }
}

@Composable
private fun GeofencingControls() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val geofenceManager = remember { GeofenceManager(context) }
    val geofenceTransitionEventInfo = remember {
        mutableStateListOf("")
    }
    val geofenceNotification = remember {
        GeofenceNotification(context, GeofenceNotificationChannel(context))
    }

    // for geofences
    val checkedGeoFence1 = remember { mutableStateOf(false) }
    val checkedGeoFence2 = remember { mutableStateOf(false) }
    val checkedGeoFence3 = remember { mutableStateOf(false) }

    DisposableEffect(LocalLifecycleOwner.current) {
        onDispose {
            scope.launch(Dispatchers.IO) {
                geofenceManager.deregisterGeofence()
            }
        }
    }

    // Register a local broadcast to receive activity transition updates
    GeofenceBroadcastReceiver(systemAction = CUSTOM_INTENT_GEOFENCE,
        onEvent = { geofences, transition, dateTime ->
            geofenceManager.parseGeofence(
                KEY_ISKON, geofences, transition, dateTime
            ) {
                geofenceTransitionEventInfo.add(it)
                val data = it.split("-")
                geofenceNotification.show(
                    id = System.currentTimeMillis().toInt(),
                    title = data.take(2).joinToString(""),
                    description = data.last()

                )
                Log.e("CHIRAG", it)
            }
        })

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        GeofenceList(checkedGeoFence1, checkedGeoFence2, checkedGeoFence3, geofenceManager)
        Button(
            onClick = {
                if (geofenceManager.geofenceList.isNotEmpty()) {
                    geofenceManager.registerGeofence()
                } else {
                    Toast.makeText(
                        context,
                        "Please add at least one geofence to monitor",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            },
        ) {
            Text(text = "Register Geofences")
        }

        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    geofenceManager.deregisterGeofence()

                    checkedGeoFence1.value = false
                    checkedGeoFence2.value = false
                    checkedGeoFence3.value = false
                }
            },
        ) {
            Text(text = "Deregister Geofences")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = geofenceTransitionEventInfo.joinToString("\n"))
    }
}

@Composable
fun GeofenceList(
    checkedGeoFence1: MutableState<Boolean>,
    checkedGeoFence2: MutableState<Boolean>,
    checkedGeoFence3: MutableState<Boolean>,
    geofenceManager: GeofenceManager
) {

    Text(text = "GeofenceApp")
    /*Row(
        Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checkedGeoFence1.value,
            onCheckedChange = { checked ->
                if (checked) {
                    geofenceManager.addGeofence(
                        KEY_SPACE_O_TECHNOLOGIES,
                        location = Location("").apply {
                            latitude = 23.033916215363774
                            longitude = 72.5242423108757
                        },
                    )
                } else {
                    geofenceManager.removeGeofence(KEY_SPACE_O_TECHNOLOGIES)
                }
                checkedGeoFence1.value = checked
            },
        )
        Text(text = "Space O Technologies")
    }*/
    Row(
        Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checkedGeoFence2.value,
            onCheckedChange = { checked ->
                if (checked) {
                    geofenceManager.addGeofence(
                        KEY_ISKON,
                        location = Location("").apply {
                            latitude = 23.028413329055216
                            longitude = 72.50682237387505
                        },
                    )
                } else {
                    geofenceManager.removeGeofence(KEY_ISKON)
                }
                checkedGeoFence2.value = checked
            },
        )
        Text(text = "Iskon")
    }
    Row(
        Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checkedGeoFence3.value,
            onCheckedChange = { checked ->
                if (checked) {
                    geofenceManager.addGeofence(
                        KEY_BOPAL,
                        location = Location("").apply {
                            latitude = 23.036208271886572
                            longitude = 72.46254993487591
                        },
                    )
                } else {
                    geofenceManager.removeGeofence(KEY_BOPAL)
                }
                checkedGeoFence3.value = checked
            },
        )
        Text(text = "Bopal")
    }
}