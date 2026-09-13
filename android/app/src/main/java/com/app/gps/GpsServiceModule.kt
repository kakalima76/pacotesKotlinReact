package com.app.gps

import android.location.Location
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.modules.core.DeviceEventManagerModule
import java.util.concurrent.atomic.AtomicInteger  // ← ALTERAÇÃO: import adicionado

class GpsServiceModule(
    reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {
    private val listenerCount = AtomicInteger(0)  // ← ALTERAÇÃO: Int → AtomicInteger
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

    @ReactMethod
    fun addListener(eventName: String) {
        listenerCount.incrementAndGet()  // ← ALTERAÇÃO: ++ → incrementAndGet()
    }

    @ReactMethod
    fun removeListeners(count: Int) {
        listenerCount.addAndGet(-count)  // ← ALTERAÇÃO: -= → addAndGet(-count)
        if (listenerCount.get() < 0) {   // ← ALTERAÇÃO: leitura via .get()
            listenerCount.set(0)
        }
    }

    private fun sendLocation(
        location: Location
    ) {
        if (listenerCount.get() == 0) {  // ← ALTERAÇÃO: leitura via .get()
            return
        }
        if (!reactApplicationContext.hasActiveReactInstance()) {
            return
        }
        val params = Arguments.createMap()
        params.putDouble("latitude", location.latitude)
        params.putDouble("longitude", location.longitude)
        params.putDouble("accuracy", location.accuracy.toDouble())
        params.putDouble("altitude", location.altitude)
        params.putDouble("speed", location.speed.toDouble())
        params.putDouble("bearing", location.bearing.toDouble())
        params.putDouble("time", location.time.toDouble())
        reactApplicationContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit("gpsLocation", params)
    }

    override fun invalidate() {
        GpsLocationBus.unsubscribe(locationListener)
        super.invalidate()
    }
}