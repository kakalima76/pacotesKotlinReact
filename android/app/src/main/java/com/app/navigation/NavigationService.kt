package com.app.navigation

import android.content.Context
import android.util.Log

import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp

class NavigationService(
    private val context: Context
) {

    fun initialize() {

        Log.d("NavigationService", "initialize() chamado")

        if (MapboxNavigationApp.isSetup()) {
            Log.d("NavigationService", "Mapbox já estava inicializado")
            return
        }

        Log.d("NavigationService", "Inicializando Mapbox Navigation")

        MapboxNavigationApp.setup(
            NavigationOptions.Builder(context)
                .build()
        )

        Log.d("NavigationService", "Mapbox Navigation inicializado")
    }
}