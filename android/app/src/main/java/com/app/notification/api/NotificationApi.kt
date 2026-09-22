package com.app.notification.api

import android.util.Log
import com.app.BuildConfig
import com.app.auth.TokenStorage
import com.google.firebase.messaging.FirebaseMessaging
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject


class NotificationApi(
    private val tokenStorage: TokenStorage
) {

    fun registerFcmToken(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        Log.d(
            "AUTH_FLOW",
            "NotificationApi.registerFcmToken() chamado"
        )

        val accessToken = tokenStorage.getAccessToken()
        var fcmToken = tokenStorage.getFcmToken()

        Log.d(
            "AUTH_FLOW",
            "accessToken existe: ${accessToken != null}"
        )

        Log.d(
            "AUTH_FLOW",
            "fcmToken existe: ${fcmToken != null}"
        )

        if (accessToken == null) {
            onError("Token de autenticação não encontrado")
            return
        }

        if (fcmToken == null) {

            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->

                    if (!task.isSuccessful) {
                        onError("Não foi possível obter o token FCM")
                        return@addOnCompleteListener
                    }

                    fcmToken = task.result

                    tokenStorage.saveFcmToken(fcmToken!!)

                    enviarFcm(
                        accessToken = accessToken,
                        fcmToken = fcmToken!!,
                        onSuccess = onSuccess,
                        onError = onError
                    )
                }

            return
        }

        enviarFcm(
            accessToken = accessToken,
            fcmToken = fcmToken,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    private fun enviarFcm(
        accessToken: String,
        fcmToken: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        Thread {

            try {

                Log.d(
                    "AUTH_FLOW",
                    "Iniciando POST /delivery/fcm"
                )

                val url = URL(
                    "${BuildConfig.API_BASE_URL}/delivery/fcm"
                )

                val connection =
                    url.openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.doOutput = true

                connection.setRequestProperty(
                    "Authorization",
                    "Bearer $accessToken"
                )

                connection.setRequestProperty(
                    "Content-Type",
                    "application/json"
                )

                val body = JSONObject()
                    .put("fcmToken", fcmToken)
                    .put(
                        "timestamp",
                        System.currentTimeMillis()
                    )
                    .toString()

                connection.outputStream.use { outputStream ->
                    outputStream.write(body.toByteArray())
                }

                val responseCode = connection.responseCode

                Log.d(
                    "AUTH_FLOW",
                    "POST /delivery/fcm respondeu HTTP $responseCode"
                )

                if (responseCode in 200..299) {
                    onSuccess()
                } else {
                    onError(
                        "Falha ao registrar FCM: HTTP $responseCode"
                    )
                }

                connection.disconnect()

            } catch (e: Exception) {

                Log.e(
                    "AUTH_FLOW",
                    "Erro em enviarFcm(): ${e.message}",
                    e
                )

                onError(
                    e.message ?: "Erro ao registrar FCM"
                )
            }

        }.start()
    }
}