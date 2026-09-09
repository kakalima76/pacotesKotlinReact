package com.app.navigation.map

import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext

class NavigationMapViewManager :
    SimpleViewManager<NavigationMapView>() {

    companion object {
        private var instance: NavigationMapViewManager? = null

        fun updateLocation(
            latitude: Double,
            longitude: Double
        ) {
            instance?.view?.updateLocation(
                latitude,
                longitude
            )
        }

        fun updateNavigationLocation(
            location: com.mapbox.common.location.Location,
            keyPoints: List<com.mapbox.common.location.Location>
        ) {
            android.util.Log.d(
                "NavigationMapView",
                "Enviando MATCHED para NavigationLocationProvider: " +
                        "${location.latitude}, ${location.longitude}"
            )

            instance?.view?.navigationLocationProvider?.changePosition(
                location = location,
                keyPoints = keyPoints
            )
        }
    }

    private var view: NavigationMapView? = null

    init {
        instance = this
    }

    override fun getName(): String {
        return "NavigationMapView"
    }

    override fun createViewInstance(
        reactContext: ThemedReactContext
    ): NavigationMapView {

        val mapView = NavigationMapView(reactContext)

        view = mapView

        return mapView
    }
}