package com.app.auth

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.app.notification.api.NotificationApi
import android.util.Log

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

                Log.d("AUTH_FLOW", "AuthService.login() retornou sucesso")

                tokenStorage.save(
                    accessToken = result.accessToken,
                    refreshToken = result.refreshToken,
                    expiresIn = result.expiresIn
                )

                Log.d("AUTH_FLOW", "Tokens salvos no TokenStorage")

                NotificationApi(tokenStorage)
                    .registerFcmToken(
                        onSuccess = {
                            Log.d(
                                "AUTH_FLOW",
                                "registerFcmToken() sucesso"
                            )
                        },
                        onError = {error ->
                            Log.e(
                                "AUTH_FLOW",
                                "registerFcmToken() erro: $error"
                            )
                        }
                    )

                Log.d(
                    "AUTH_FLOW",
                    "NotificationApi.registerFcmToken() chamado"
                )

                val response = Arguments.createMap().apply {
                    putString("accessToken", result.accessToken)
                }

                promise.resolve(response)
            },
            onError = { error ->

                Log.e(
                    "AUTH_FLOW",
                    "AuthService.login() erro: $error"
                )

                promise.reject(
                    "AUTH_ERROR",
                    error
                )
            }
        )
    }

    @ReactMethod
    fun getValidAccessToken(promise: Promise) {

        getValidAccessTokenInternal(
            onSuccess = { accessToken ->

                promise.resolve(accessToken)
            },
            onError = {

                promise.reject(
                    "AUTH_REQUIRED",
                    "Nenhum refresh token disponível"
                )
            }
        )
    }

    @ReactMethod
    fun getAuthenticatedUser(promise: Promise) {

        getValidAccessTokenInternal(
            onSuccess = { accessToken ->

                try {
                    val payload = accessToken
                        .split(".")
                        .getOrNull(1)
                        ?: throw IllegalArgumentException("Token inválido")

                    val decoded = String(
                        android.util.Base64.decode(
                            payload,
                            android.util.Base64.URL_SAFE or
                                    android.util.Base64.NO_WRAP
                        ),
                        Charsets.UTF_8
                    )

                    val json = org.json.JSONObject(decoded)

                    val user = Arguments.createMap().apply {
                        putString("id", json.optString("sub"))
                        putString(
                            "username",
                            json.optString("preferred_username")
                        )
                        putString(
                            "name",
                            json.optString("name")
                        )
                        putString(
                            "email",
                            json.optString("email")
                        )
                    }

                    promise.resolve(user)

                } catch (e: Exception) {

                    promise.reject(
                        "AUTH_USER_ERROR",
                        "Não foi possível obter os dados do usuário"
                    )
                }
            },
            onError = {

                promise.reject(
                    "AUTH_REQUIRED",
                    "Usuário não autenticado"
                )
            }
        )
    }

    private fun getValidAccessTokenInternal(
        onSuccess: (String) -> Unit,
        onError: () -> Unit
    ) {

        if (tokenStorage.isAccessTokenValid()) {

            val accessToken = tokenStorage.getAccessToken()

            if (accessToken != null) {
                onSuccess(accessToken)
                return
            }
        }

        val refreshToken = tokenStorage.getRefreshToken()

        if (refreshToken == null) {
            onError()
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

            onSuccess(result.accessToken)
        }
    }
}