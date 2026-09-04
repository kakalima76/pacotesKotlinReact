package com.app

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class PersonsFunctions(
    reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String {
        return "PersonsFunctions"
    }

    @ReactMethod
    fun somar(a: Int, b: Int, promise: Promise) {
        promise.resolve(a + b)
    }
}