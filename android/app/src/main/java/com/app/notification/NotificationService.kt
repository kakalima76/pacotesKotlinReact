package com.app.notification

import android.content.Intent
import android.util.Log
import com.app.auth.TokenStorage
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificationService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {

        Log.d(
            "FCM_TEST",
            "onNewToken() chamado"
        )

        TokenStorage(this).saveFcmToken(token)
    }

    override fun onMessageReceived(
        remoteMessage: RemoteMessage
    ) {

        Log.d(
            "FCM_TEST",
            "onMessageReceived() chamado"
        )

        Log.d(
            "FCM_TEST",
            "messageId: ${remoteMessage.messageId}"
        )

        Log.d(
            "FCM_TEST",
            "priority: ${remoteMessage.priority}"
        )

        Log.d(
            "FCM_TEST",
            "data: ${remoteMessage.data}"
        )

        val dados = remoteMessage.data

        val estabelecimento =
            dados["estabelecimento"].orEmpty()

        val quantidade =
            dados["quantidade"]
                ?.toIntOrNull()
                ?: 0

        val distancia =
            dados["distancia"].orEmpty()

        val valor =
            dados["valor"].orEmpty()

        val intent = Intent(
            this,
            DeliveryOverlayService::class.java
        ).apply {

            putExtra(
                "estabelecimento",
                estabelecimento
            )

            putExtra(
                "quantidade",
                quantidade
            )

            putExtra(
                "distancia",
                distancia
            )

            putExtra(
                "valor",
                valor
            )
        }

        try {

            startForegroundService(intent)

            Log.d(
                "FCM_TEST",
                "DeliveryOverlayService iniciado"
            )

        } catch (e: Exception) {

            Log.e(
                "FCM_TEST",
                "Erro ao iniciar DeliveryOverlayService",
                e
            )
        }
    }
}
