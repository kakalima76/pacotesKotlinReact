package com.app.gps

import android.content.Intent
import androidx.core.content.ContextCompat
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class GpsFunctionsModule(
    reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String {
        return "GpsFunctions"
    }

    @ReactMethod
    fun startGps() {

        val intent = Intent(
            reactApplicationContext,
            GpsService::class.java
        )

        ContextCompat.startForegroundService(
            reactApplicationContext,
            intent
        )
    }
}