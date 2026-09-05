package com.app.gps

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

class GpsService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var locationRequest: LocationRequest

    private val locationCallback = object : LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult) {

            for (location in locationResult.locations) {

                Log.d(
                    "GPS_SERVICE",
                    "Latitude: ${location.latitude}"
                )

                Log.d(
                    "GPS_SERVICE",
                    "Longitude: ${location.longitude}"
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            mainLooper
        )

        Log.d(
            "GPS_SERVICE",
            "Atualizações de localização iniciadas"
        )
    }

    private fun stopLocationUpdates() {

        fusedLocationClient.removeLocationUpdates(
            locationCallback
        )

        Log.d(
            "GPS_SERVICE",
            "Atualizações de localização interrompidas"
        )
    }

    override fun onCreate() {
        super.onCreate()

        Log.d("GPS_SERVICE", "onCreate()")

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000L
        )
            .setMinUpdateIntervalMillis(2000L)
            .build()

        createNotificationChannel()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        Log.d("GPS_SERVICE", "onStartCommand()")

        val notification = NotificationCompat.Builder(
            this,
            "GPS_CHANNEL"
        )
            .setContentTitle("Localização ativa")
            .setContentText("O GPS está funcionando")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .build()

        startForeground(1, notification)

        Log.d("GPS_SERVICE", "startForeground() executado")

        startLocationUpdates()
        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location ->

            if (location != null) {

                Log.d(
                    "GPS_SERVICE",
                    "Latitude: ${location.latitude}"
                )

                Log.d(
                    "GPS_SERVICE",
                    "Longitude: ${location.longitude}"
                )

            } else {

                Log.d(
                    "GPS_SERVICE",
                    "Localização não disponível"
                )
            }
        }
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                "GPS_CHANNEL",
                "Localização",
                NotificationManager.IMPORTANCE_LOW
            )

            val notificationManager =
                getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {

        stopLocationUpdates()

        Log.d(
            "GPS_SERVICE",
            "onDestroy()"
        )

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}