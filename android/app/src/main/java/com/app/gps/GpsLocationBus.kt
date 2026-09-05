package com.app.gps

object GpsLocationBus {

    private val listeners =
        mutableSetOf<(Double, Double) -> Unit>()

    fun subscribe(listener: (Double, Double) -> Unit) {
        listeners.add(listener)
    }

    fun unsubscribe(listener: (Double, Double) -> Unit) {
        listeners.remove(listener)
    }

    fun emit(
        latitude: Double,
        longitude: Double
    ) {
        listeners.forEach { listener ->
            listener(latitude, longitude)
        }
    }
}