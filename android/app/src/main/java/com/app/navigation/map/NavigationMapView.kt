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
import com.mapbox.navigation.ui.maps.route.arrow.model.RouteArrowOptions
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineApiOptions
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineViewOptions
import com.mapbox.navigation.ui.maps.route.RouteLayerConstants.TOP_LEVEL_ROUTE_LINE_LAYER_ID

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
    internal var cameraFollowing = false
    internal var navigationActive = false

    private val routeArrowApi = MapboxRouteArrowApi()

    private val routeArrowOptions by lazy {
        RouteArrowOptions.Builder(context)
            .withAboveLayerId(TOP_LEVEL_ROUTE_LINE_LAYER_ID)
            .build()
    }

    private val routeArrowView by lazy {
        MapboxRouteArrowView(routeArrowOptions)
    }

    init {
        viewportDataSource.followingPadding = EdgeInsets(
            100.0, 50.0, 400.0, 50.0
        )
        viewportDataSource.options.followingFrameOptions.zoomUpdatesAllowed = false
        viewportDataSource.options.followingFrameOptions.maxZoom = 17.0  // ← linha nova
        mapboxMap.loadStyleUri("mapbox://styles/mapbox/standard") {
            routeLineView.initializeLayers(it)  // ← linha nova
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
        viewportDataSource.onLocationChanged(location)
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
        if (navigationActive) return

        mapboxMap.setCamera(
            com.mapbox.maps.CameraOptions.Builder()
                .center(
                    com.mapbox.geojson.Point.fromLngLat(
                        longitude,
                        latitude
                    )
                )
                .zoom(17.0)
                .build()
        )
    }

    fun updateRoute(
        routes: List<com.mapbox.navigation.base.route.NavigationRoute>
    ) {
        routeLineApi.setNavigationRoutes(routes) { result ->
            mapboxMap.getStyle { style ->
                routeLineView.renderRouteDrawData(style, result)
            }
        }
    }

    fun updateRouteArrows(
        routeProgress: com.mapbox.navigation.base.trip.model.RouteProgress
    ) {
        val arrowUpdate = routeArrowApi.addUpcomingManeuverArrow(routeProgress)
        mapboxMap.getStyle { style ->
            routeArrowView.renderManeuverUpdate(style, arrowUpdate)
        }
    }

    fun clearRouteArrows() {
        val arrowUpdate = routeArrowApi.clearArrows()
        mapboxMap.getStyle { style ->
            routeArrowView.render(style, arrowUpdate)
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

    fun setZoomLevel(zoom: Double) {
        mapboxMap.setCamera(
            com.mapbox.maps.CameraOptions.Builder()
                .zoom(zoom)
                .build()
        )
    }
}