package com.app.navigation

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReactContextBaseJavaModule
import android.os.Handler
import android.os.Looper
import com.facebook.react.bridge.Arguments
import com.facebook.react.modules.core.DeviceEventManagerModule

class NavigationModule(
    reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {



    private val navigationService =
        NavigationService(
            reactContext,

            { maneuvers ->

                val array = Arguments.createArray()

                maneuvers.forEach { maneuver ->

                    val map = Arguments.createMap()

                    map.putString("id", maneuver.id)
                    map.putString("text", maneuver.text)
                    map.putString("type", maneuver.type)
                    map.putString("modifier", maneuver.modifier)

                    map.putDouble(
                        "distanceRemaining",
                        maneuver.distanceRemaining ?: 0.0
                    )

                    map.putDouble(
                        "totalDistance",
                        maneuver.totalDistance
                    )

                    map.putDouble(
                        "latitude",
                        maneuver.latitude
                    )

                    map.putDouble(
                        "longitude",
                        maneuver.longitude
                    )

                    array.pushMap(map)
                }

                reactApplicationContext
                    .getJSModule(
                        DeviceEventManagerModule.RCTDeviceEventEmitter::class.java
                    )
                    .emit(
                        "navigationManeuvers",
                        array
                    )
            },

            { tripProgress ->

                val map = Arguments.createMap()

                map.putDouble(
                    "distanceRemaining",
                    tripProgress.distanceRemaining
                )

                map.putDouble(
                    "durationRemaining",
                    tripProgress.durationRemaining
                )

                reactApplicationContext
                    .getJSModule(
                        DeviceEventManagerModule.RCTDeviceEventEmitter::class.java
                    )
                    .emit(
                        "navigationTripProgress",
                        map
                    )
            }
        )


    @ReactMethod()
    fun initializeNavigation() {
        Handler(Looper.getMainLooper()).post {
            navigationService.initialize()
        }
    }

    override fun getName(): String {
        return "Navigation"
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