package com.app.auth

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class AuthModule(
    reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {

    private val tokenStorage = TokenStorage(reactContext)

    override fun getName(): String {
        return "Auth"
    }

    @ReactMethod
    fun login(
        username: String,
        password: String,
        promise: Promise
    ) {

        AuthService().login(
            username = username,
            password = password,
            onResult = { result ->

                tokenStorage.save(
                    accessToken = result.accessToken,
                    refreshToken = result.refreshToken,
                    expiresIn = result.expiresIn
                )

                val response = Arguments.createMap().apply {
                    putString("accessToken", result.accessToken)
                }

                promise.resolve(response)
            },
            onError = { error ->

                promise.reject(
                    "AUTH_ERROR",
                    error
                )
            }
        )
    }

    @ReactMethod
    fun getValidAccessToken(promise: Promise) {

        if (tokenStorage.isAccessTokenValid()) {
            promise.resolve(
                tokenStorage.getAccessToken()
            )
            return
        }

        val refreshToken = tokenStorage.getRefreshToken()

        if (refreshToken == null) {
            promise.reject(
                "AUTH_REQUIRED",
                "Nenhum refresh token disponível"
            )
            return
        }

        AuthService().refresh(
            refreshToken
        ) { result ->

            tokenStorage.save(
                accessToken = result.accessToken,
                refreshToken = result.refreshToken,
                expiresIn = result.expiresIn
            )

            promise.resolve(
                result.accessToken
            )
        }
    }
}
