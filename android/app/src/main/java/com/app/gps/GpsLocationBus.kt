package com.app.gps

import android.location.Location
import android.util.Log


object GpsLocationBus {

    private val listeners =
        mutableSetOf<(Location) -> Unit>()

    fun subscribe(listener: (Location) -> Unit) {

        Log.d(
            "GpsLocationBus",
            "SUBSCRIBE: ${listener}"
        )

        listeners.add(listener)

        Log.d(
            "GpsLocationBus",
            "TOTAL LISTENERS: ${listeners.size}"
        )
    }

    fun unsubscribe(listener: (Location) -> Unit) {
        listeners.remove(listener)
    }

    fun emit(location: Location) {

        Log.d(
            "GpsLocationBus",
            "emit() chamado — listeners: ${listeners.size}"
        )

        listeners.forEachIndexed { index, listener ->

            Log.d(
                "GpsLocationBus",
                "ANTES listener $index — " +
                        "${location.latitude}, ${location.longitude}"
            )

            try {

                listener(location)

                Log.d(
                    "GpsLocationBus",
                    "DEPOIS listener $index"
                )

            } catch (e: Exception) {

                Log.e(
                    "GpsLocationBus",
                    "ERRO ao executar listener $index",
                    e
                )
            }
        }
    }
}
