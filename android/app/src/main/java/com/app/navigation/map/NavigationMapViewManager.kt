package com.app.navigation.map

import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext

class NavigationMapViewManager :
    SimpleViewManager<NavigationMapView>() {
    companion object {
        private var instance: NavigationMapViewManager? = null

        // ← ALTERAÇÃO: setter para ativar/desativar modo navegação
        fun setNavigationActive(active: Boolean) {
            instance?.view?.navigationActive = active
            if (!active) {
                instance?.view?.cameraFollowing = false
            } else {
                instance?.view?.setZoomLevel(15.0)  // ← ALTERAÇÃO: zoom padrão ao iniciar
            }
        }

        fun updateLocation(
            latitude: Double,
            longitude: Double
        ) {
            instance?.view?.updateLocation(
                latitude,
                longitude
            )
        }
        fun updatePuckLocation(
            location: com.mapbox.common.location.Location
        ) {
            // ← ALTERAÇÃO: durante navegação, o updateNavigationLocation
            // controla o puck com keyPoints reais (route snapping)
            val view = instance?.view ?: return
            if (view.navigationActive) return

            view.navigationLocationProvider.changePosition(
                location = location,
                keyPoints = emptyList()
            )
        }

        fun updateNavigationLocation(
            location: com.mapbox.common.location.Location,
            keyPoints: List<com.mapbox.common.location.Location>
        ) {
            instance?.view?.navigationLocationProvider?.changePosition(
                location = location,
                keyPoints = keyPoints
            )
            instance?.view?.updateNavigationLocation(
                location,
                keyPoints
            )
        }
        fun updateRoute(
            routes: List<com.mapbox.navigation.base.route.NavigationRoute>
        ) {
            instance?.view?.updateRoute(routes)
        }

        fun setZoomLevel(zoom: Double) {
            instance?.view?.setZoomLevel(zoom)
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

    // ← ALTERAÇÃO: limpa referências quando o React Native desmonta a view
    override fun onDropViewInstance(view: NavigationMapView) {
        this.view = null
        // ← Se for o único manager ativo, remove a referência estática
        if (instance === this) {
            instance = null
        }
        super.onDropViewInstance(view)
    }
}