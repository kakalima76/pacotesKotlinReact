package com.app.gps
import com.app.gps.GpsLocationBus
import android.location.Location
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.modules.core.DeviceEventManagerModule

class GpsServiceModule(
    reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {

    private val locationListener:
                (Location) -> Unit = { location ->

        sendLocation(location)
    }

    init {
        GpsLocationBus.subscribe(locationListener)
    }

    override fun getName(): String {
        return "GpsService"
    }

    private fun sendLocation(
        location: Location
    ) {
        val params = Arguments.createMap()

        params.putDouble(
            "latitude",
            location.latitude
        )

        params.putDouble(
            "longitude",
            location.longitude
        )

        params.putDouble(
            "accuracy",
            location.accuracy.toDouble()
        )

        params.putDouble(
            "altitude",
            location.altitude
        )

        params.putDouble(
            "speed",
            location.speed.toDouble()
        )

        params.putDouble(
            "bearing",
            location.bearing.toDouble()
        )

        params.putDouble(
            "time",
            location.time.toDouble()
        )

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

