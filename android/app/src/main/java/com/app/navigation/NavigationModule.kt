package com.app.navigation

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReactContextBaseJavaModule
import android.os.Handler
import android.os.Looper

class NavigationModule(
    reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {

    private val navigationService = NavigationService(reactContext)

    override fun getName(): String {
        return "Navigation"
    }

    @ReactMethod
    fun startNavigation() {
        Handler(Looper.getMainLooper()).post {
            navigationService.initialize()
            navigationService.start()
        }
    }

    @ReactMethod
    fun setRoute(
        originLatitude: Double,
        originLongitude: Double,
        destinationLatitude: Double,
        destinationLongitude: Double
    ) {
        Handler(Looper.getMainLooper()).post {
            navigationService.setRoute(
                originLatitude,
                originLongitude,
                destinationLatitude,
                destinationLongitude
            )
        }
    }
}