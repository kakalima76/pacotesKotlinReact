package com.app.gps

import android.location.Location
import java.util.concurrent.CopyOnWriteArraySet  // ← ALTERAÇÃO: import adicionado

object GpsLocationBus {
    private val listeners =
        CopyOnWriteArraySet<(Location) -> Unit>()  // ← ALTERAÇÃO: mutableSetOf → CopyOnWriteArraySet

    fun subscribe(listener: (Location) -> Unit) {
        listeners.add(listener)
    }

    fun unsubscribe(listener: (Location) -> Unit) {
        listeners.remove(listener)
    }

    fun emit(location: Location) {
        listeners.forEach { listener ->
            try {
                listener(location)
            } catch (e: Exception) {
                // Exceção do listener é ignorada para não interromper os demais
            }
        }
    }
}