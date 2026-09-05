package com.app.gps

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.modules.core.DeviceEventManagerModule

class GpsServiceModule(
    reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {

    private val locationListener:
                (Double, Double) -> Unit = { latitude, longitude ->

        sendLocation(
            latitude,
            longitude
        )
    }

    init {
        GpsLocationBus.subscribe(locationListener)
    }

    override fun getName(): String {
        return "GpsService"
    }

    private fun sendLocation(
        latitude: Double,
        longitude: Double
    ) {
        val params = Arguments.createMap()

        params.putDouble("latitude", latitude)
        params.putDouble("longitude", longitude)

        reactApplicationContext
            .getJSModule(
                DeviceEventManagerModule.RCTDeviceEventEmitter::class.java
            )
            .emit(
                "gpsLocation",
                params
            )
    }

    override fun invalidate() {
        GpsLocationBus.unsubscribe(locationListener)
        super.invalidate()
    }
}