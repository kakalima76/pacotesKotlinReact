package com.app.gps

import android.location.Location

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
        listeners.forEach { listener ->
            listener(location)
        }
    }
}
