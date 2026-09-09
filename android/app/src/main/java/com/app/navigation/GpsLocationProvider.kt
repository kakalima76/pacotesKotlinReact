package com.app.navigation

import android.app.PendingIntent
import android.location.Location

import com.mapbox.common.Cancelable
import com.mapbox.common.location.BaseLocationProvider
import com.mapbox.common.location.DeviceLocationProvider
import com.mapbox.common.location.GetLocationCallback
import com.mapbox.common.location.toCommonLocation
import android.util.Log


class GpsLocationProvider : BaseLocationProvider(), DeviceLocationProvider {

    private var lastLocation: Location? = null

    fun updateLocation(location: Location) {

        lastLocation = location

        Log.d(
            "GpsLocationProvider",
            "Enviando localização para o Mapbox: " +
                    "${location.latitude}, ${location.longitude}"
        )

        notifyLocationUpdate(
            listOf(location.toCommonLocation())
        )
    }

    override fun getLastLocation(
        callback: GetLocationCallback
    ): Cancelable {

        callback.run(
            lastLocation?.toCommonLocation()
        )

        return object : Cancelable {
            override fun cancel() {
                // Nada para cancelar
            }
        }
    }

    override fun getName(): String {
        return "GpsLocationProvider"
    }

    override fun requestLocationUpdates(
        pendingIntent: PendingIntent
    ) {
        // O GPS já é controlado pelo GpsService.
    }

    override fun removeLocationUpdates(
        pendingIntent: PendingIntent
    ) {
        // O GPS já é controlado pelo GpsService.
    }
}