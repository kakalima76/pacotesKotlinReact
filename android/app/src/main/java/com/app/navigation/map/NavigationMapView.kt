package com.app.navigation.map

import android.content.Context
import com.mapbox.maps.MapView
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.ui.maps.camera.NavigationCamera
import com.mapbox.navigation.ui.maps.camera.data.MapboxNavigationViewportDataSource
import com.mapbox.maps.plugin.animation.camera

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
}