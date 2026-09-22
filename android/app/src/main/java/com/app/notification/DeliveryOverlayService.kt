package com.app.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.app.R
import android.media.MediaPlayer

class DeliveryOverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()

        Log.d("FCM_TEST", "DeliveryOverlayService.onCreate()")

        criarCanalNotificacao()

        val notification = criarNotificacaoForeground()

        startForeground(
            NOTIFICATION_ID,
            notification
        )
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        Log.d("FCM_TEST", "DeliveryOverlayService.onStartCommand()")

        if (intent == null) {
            stopSelf()
            return START_NOT_STICKY
        }

        val entrega = NovaEntregaDTO(
            estabelecimento = intent
                .getStringExtra("estabelecimento")
                .orEmpty(),

            quantidade = intent
                .getIntExtra("quantidade", 0),

            distancia = intent
                .getStringExtra("distancia")
                .orEmpty(),

            valor = intent
                .getStringExtra("valor")
                .orEmpty()
        )

        mostrarOverlay(entrega)

        return START_NOT_STICKY
    }

    private fun mostrarOverlay(entrega: NovaEntregaDTO) {

        Log.d(
            "FCM_TEST",
            "DeliveryOverlayService.mostrarOverlay()"
        )

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            !Settings.canDrawOverlays(this)
        ) {
            Log.e(
                "FCM_TEST",
                "Permissão SYSTEM_ALERT_WINDOW não concedida"
            )

            stopSelf()
            return
        }

        removerOverlay()

        val view = LayoutInflater
            .from(this)
            .inflate(
                R.layout.activity_notification,
                null
            )

        preencherTela(
            view,
            entrega
        )

        configurarBotoes(view)

        val tipoJanela =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }

        val params = WindowManager.LayoutParams(
            (resources.displayMetrics.widthPixels * 0.90).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT,
            tipoJanela,
            WindowManager.LayoutParams.FLAG_DIM_BEHIND or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.CENTER
        params.dimAmount = 0.15f

        windowManager =
            getSystemService(Context.WINDOW_SERVICE) as WindowManager

        try {

            windowManager?.addView(
                view,
                params
            )

            overlayView = view

            Log.d(
                "FCM_TEST",
                "Overlay adicionado com sucesso"
            )

            tocarSomNotificacao()

        } catch (e: Exception) {

            Log.e(
                "FCM_TEST",
                "Erro ao adicionar overlay",
                e
            )

            stopSelf()
        }
    }

    private fun preencherTela(
        view: View,
        entrega: NovaEntregaDTO
    ) {

        view.findViewById<TextView>(
            R.id.establishmentName
        ).text = entrega.estabelecimento

        view.findViewById<TextView>(
            R.id.deliveryQuantity
        ).text = "${entrega.quantidade} entregas"

        view.findViewById<TextView>(
            R.id.distance
        ).text = entrega.distancia

        view.findViewById<TextView>(
            R.id.deliveryValue
        ).text = entrega.valor
    }

    private fun configurarBotoes(view: View) {

        view.findViewById<Button>(
            R.id.acceptButton
        ).setOnClickListener {

            Log.d(
                "FCM_TEST",
                "ACEITAR pressionado"
            )

            fecharOverlay()
        }

        view.findViewById<Button>(
            R.id.refuseButton
        ).setOnClickListener {

            Log.d(
                "FCM_TEST",
                "RECUSAR pressionado"
            )

            fecharOverlay()
        }
    }

    private fun fecharOverlay() {

        removerOverlay()

        stopSelf()
    }

    private fun removerOverlay() {

        val view = overlayView ?: return

        try {

            windowManager?.removeView(view)

            Log.d(
                "FCM_TEST",
                "Overlay removido"
            )

        } catch (e: Exception) {

            Log.e(
                "FCM_TEST",
                "Erro ao remover overlay",
                e
            )
        }

        overlayView = null
    }

    private fun criarCanalNotificacao() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Novas entregas",
                NotificationManager.IMPORTANCE_LOW
            )

            val manager =
                getSystemService(
                    NotificationManager::class.java
                )

            manager.createNotificationChannel(channel)
        }
    }

    private fun criarNotificacaoForeground(): Notification {

        return NotificationCompat
            .Builder(
                this,
                CHANNEL_ID
            )
            .setSmallIcon(
                R.mipmap.ic_launcher
            )
            .setContentTitle(
                "Nova entrega"
            )
            .setContentText(
                "Nova entrega disponível"
            )
            .setPriority(
                NotificationCompat.PRIORITY_LOW
            )
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {

        Log.d(
            "FCM_TEST",
            "DeliveryOverlayService.onDestroy()"
        )

        mediaPlayer?.release()
        mediaPlayer = null

        removerOverlay()

        super.onDestroy()
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = null

    private fun tocarSomNotificacao() {

        mediaPlayer?.release()

        mediaPlayer = MediaPlayer.create(
            this,
            R.raw.som
        )

        mediaPlayer?.setOnCompletionListener { player ->

            player.release()

            if (mediaPlayer === player) {
                mediaPlayer = null
            }
        }

        mediaPlayer?.start()
    }

    companion object {

        private const val CHANNEL_ID =
            "delivery_overlay"

        private const val NOTIFICATION_ID =
            2001
    }
}