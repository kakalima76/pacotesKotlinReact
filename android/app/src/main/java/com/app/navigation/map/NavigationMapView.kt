package com.app.navigation.map

import android.content.Context
import com.mapbox.maps.MapView
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.ui.maps.camera.NavigationCamera
import com.mapbox.navigation.ui.maps.camera.data.MapboxNavigationViewportDataSource
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.EdgeInsets
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowApi
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowView
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineApiOptions
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineViewOptions



class NavigationMapView(
    context: Context
) : MapView(context) {

    val navigationLocationProvider = NavigationLocationProvider()

    private val viewportDataSource =
        MapboxNavigationViewportDataSource(mapboxMap)

    private val navigationCamera =
        NavigationCamera(
            mapboxMap,
            camera,
            viewportDataSource
        )

    private var cameraFollowing = false

    init {

        viewportDataSource.followingPadding = EdgeInsets(
            100.0,
            50.0,
            400.0,
            50.0
        )
        mapboxMap.loadStyleUri(
            "mapbox://styles/mapbox/standard"
        ) {
            location.apply {
                setLocationProvider(navigationLocationProvider)
                locationPuck = createDefault2DPuck(withBearing = true)
                puckBearingEnabled = true
                enabled = true
            }


        }
    }

    fun updateNavigationLocation(
        location: com.mapbox.common.location.Location,
        keyPoints: List<com.mapbox.common.location.Location>
    ) {
        viewportDataSource.onLocationChanged(
            location
        )

        viewportDataSource.evaluate()

        if (!cameraFollowing) {
            navigationCamera.requestNavigationCameraToFollowing()
            cameraFollowing = true
        }
    }

    fun updateLocation(
        latitude: Double,
        longitude: Double
    ) {
        mapboxMap.setCamera(
            com.mapbox.maps.CameraOptions.Builder()
                .center(
                    com.mapbox.geojson.Point.fromLngLat(
                        longitude,
                        latitude
                    )
                )
                .zoom(15.0)
                .build()
        )
    }

    fun updateRoute(
        routes: List<com.mapbox.navigation.base.route.NavigationRoute>
    ) {
        android.util.Log.d(
            "NavigationMapView",
            "updateRoute() VIEW: ${routes.size}"
        )

        routeLineApi.setNavigationRoutes(routes) { result ->
            mapboxMap.getStyle { style ->
                routeLineView.renderRouteDrawData(style, result)

                android.util.Log.d(
                    "NavigationMapView",
                    "Route Line renderizada"
                )
            }
        }
    }

    private val routeLineApi by lazy {
        MapboxRouteLineApi(
            MapboxRouteLineApiOptions.Builder().build()
        )
    }

    private val routeLineView by lazy {
        MapboxRouteLineView(
            MapboxRouteLineViewOptions.Builder(context).build()
        )
    }



}