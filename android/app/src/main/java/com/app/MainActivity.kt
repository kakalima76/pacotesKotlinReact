package com.app

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled
import com.facebook.react.defaults.DefaultReactActivityDelegate
import com.app.gps.GpsService
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp

class MainActivity : ReactActivity() {

    private var waitingForBackgroundPermission = false
    private var waitingForOverlayPermission = false
    private var gpsServiceStarted = false

    private fun requestLocationPermission() {

        val fineGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )

        } else {

            onLocationPermissionGranted()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {

            val fineGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            val coarseGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (fineGranted || coarseGranted) {
                onLocationPermissionGranted()
            }
        }
    }

    private fun onLocationPermissionGranted() {

        requestBackgroundLocationPermission()

        if (!gpsServiceStarted) {

            gpsServiceStarted = true

            val intent = Intent(
                this,
                GpsService::class.java
            )

            ContextCompat.startForegroundService(
                this,
                intent
            )
        }
    }

    private fun hasBackgroundLocationPermission(): Boolean {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        } else {

            true
        }
    }

    private fun requestBackgroundLocationPermission() {

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            !hasBackgroundLocationPermission()
        ) {

            if (isFinishing || isDestroyed) return

            AlertDialog.Builder(this)
                .setTitle("Localização em segundo plano")
                .setMessage(
                    "Vá para a aba de PERMISSÕES, clique em LOCALIZAÇÃO " +
                            "e AUTORIZE a permissão para \"Permitir o tempo todo\" " +
                            "para continuar usando o aplicativo."
                )
                .setNegativeButton("Agora não") { _, _ ->

                    waitingForBackgroundPermission = false
                }
                .setPositiveButton("Abrir configurações") { _, _ ->

                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:$packageName")
                    )

                    waitingForBackgroundPermission = true

                    startActivity(intent)
                }
                .show()
        }
    }

    private fun hasOverlayPermission(): Boolean {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
    }

    private fun requestOverlayPermission() {

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            !hasOverlayPermission()
        ) {

            if (isFinishing || isDestroyed) return

            waitingForOverlayPermission = true

            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )

            startActivity(intent)
        }
    }

    private fun showOverlayPermissionRequiredMessage() {

        if (isFinishing || isDestroyed) return

        AlertDialog.Builder(this)
            .setTitle("Permissão necessária")
            .setMessage(
                "A permissão \"Aparecer sobre outros aplicativos\" " +
                        "é necessária para receber novas entregas " +
                        "sobre outros aplicativos."
            )
            .setPositiveButton("Fechar aplicativo") { _, _ ->

                finishAndRemoveTask()
            }
            .setCancelable(false)
            .show()
    }

    private fun showBackgroundPermissionRequiredMessage() {

        if (isFinishing || isDestroyed) return

        AlertDialog.Builder(this)
            .setTitle("Permissão necessária")
            .setMessage(
                "A permissão de localização em segundo plano é necessária " +
                        "para utilizar este aplicativo."
            )
            .setPositiveButton("Fechar aplicativo") { _, _ ->

                finishAndRemoveTask()
            }
            .setCancelable(false)
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        MapboxNavigationApp.attach(this)

        requestLocationPermission()
        requestOverlayPermission()
    }

    override fun onResume() {

        super.onResume()

        if (waitingForBackgroundPermission) {

            waitingForBackgroundPermission = false

            if (!hasBackgroundLocationPermission()) {
                showBackgroundPermissionRequiredMessage()
            }
        }

        if (waitingForOverlayPermission) {

            waitingForOverlayPermission = false

            if (!hasOverlayPermission()) {
                showOverlayPermissionRequiredMessage()
            }
        }
    }

    override fun getMainComponentName(): String = "app"

    override fun createReactActivityDelegate(): ReactActivityDelegate =
        DefaultReactActivityDelegate(
            this,
            mainComponentName,
            fabricEnabled
        )

    companion object {

        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
}