package com.app.navigation.map

import android.content.Context
import com.mapbox.maps.MapView

class NavigationMapView(
    context: Context
) : MapView(context) {

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