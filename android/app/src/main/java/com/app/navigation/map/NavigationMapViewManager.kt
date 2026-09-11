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
            android.util.Log.d(
                "NavigationMapView",
                "UPDATE CAMERA: $latitude, $longitude"
            )

            android.util.Log.d(
                "NavigationMapView",
                "VIEW EXISTE: ${instance?.view != null}"
            )

            instance?.view?.updateLocation(
                latitude,
                longitude
            )
        }

        fun updatePuckLocation(
            location: com.mapbox.common.location.Location
        ) {
            android.util.Log.d(
                "NavigationMapView",
                "UPDATE PUCK: ${location.latitude}, ${location.longitude}"
            )

            android.util.Log.d(
                "NavigationMapView",
                "VIEW EXISTE PUCK: ${instance?.view != null}"
            )

            instance?.view?.navigationLocationProvider?.changePosition(
                location = location,
                keyPoints = emptyList()
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

            android.util.Log.d(
                "NavigationMapView",
                "ANTES de chamar updateNavigationLocation()"
            )

            instance?.view?.updateNavigationLocation(
                location,
                keyPoints
            )

            android.util.Log.d(
                "NavigationMapView",
                "DEPOIS de chamar updateNavigationLocation()"
            )
        }

        fun updateRoute(
            routes: List<com.mapbox.navigation.base.route.NavigationRoute>
        ) {
            android.util.Log.d(
                "NavigationMapView",
                "updateRoute() MANAGER: ${routes.size}"
            )

            instance?.view?.updateRoute(routes)

            android.util.Log.d(
                "NavigationMapView",
                "updateRoute() MANAGER FINALIZADO"
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