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
import com.app.auth.AuthService


class MainActivity : ReactActivity() {
    private var waitingForBackgroundPermission = false
    private var gpsServiceStarted = false  // ← ALTERAÇÃO: evita iniciar o service duas vezes

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
            // ← ALTERAÇÃO: permissão já concedida, pode iniciar o service
            onLocationPermissionGranted()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
                // ← ALTERAÇÃO: permissão concedida agora, inicia o service
                onLocationPermissionGranted()
            }
            // ← ALTERAÇÃO: se negou, não inicia o service (evita crash)
        }
    }

    // ← ALTERAÇÃO: método centralizado que inicia o service uma única vez
    private fun onLocationPermissionGranted() {
        requestBackgroundLocationPermission()
        if (!gpsServiceStarted) {
            gpsServiceStarted = true
            val intent = Intent(this, GpsService::class.java)
            ContextCompat.startForegroundService(this, intent)
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !hasBackgroundLocationPermission()) {
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
        super.onCreate(savedInstanceState)  // ← ALTERAÇÃO: passar savedInstanceState, não null
        MapboxNavigationApp.attach(this)
        requestLocationPermission()
        val authService = AuthService()


    }

    override fun onResume() {
        super.onResume()
        if (waitingForBackgroundPermission) {
            waitingForBackgroundPermission = false
            if (!hasBackgroundLocationPermission()) {
                showBackgroundPermissionRequiredMessage()
            }
        }
    }

    override fun getMainComponentName(): String = "app"
    override fun createReactActivityDelegate(): ReactActivityDelegate =
        DefaultReactActivityDelegate(this, mainComponentName, fabricEnabled)

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
}