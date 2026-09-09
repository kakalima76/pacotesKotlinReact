package com.app.gps

import android.location.Location
import android.util.Log


object GpsLocationBus {

    private val listeners =
        mutableSetOf<(Location) -> Unit>()

    fun subscribe(listener: (Location) -> Unit) {
        listeners.add(listener)
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
                "Executando listener $index"
            )

            listener(location)
        }
    }
}
