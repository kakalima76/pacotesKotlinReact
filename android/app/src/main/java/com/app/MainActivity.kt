package com.app

import android.Manifest
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
import android.app.AlertDialog

class MainActivity : ReactActivity() {

    private var waitingForBackgroundPermission = false



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
                100
            )
        } else {
            requestBackgroundLocationPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100) {
            val fineGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            val coarseGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (fineGranted || coarseGranted) {
                requestBackgroundLocationPermission()
            }
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
            AlertDialog.Builder(this)
                .setTitle("Localização em segundo plano")
                .setMessage(
                    "Vá para a aba de PERMISSOES, clique em LOCALIZAÇAO " +
                            "e AUTORIZE a permissão para TODO O TEMPO" +
                            " para continuar usando o aplicativo."
                )
                .setNegativeButton("Agora não", null)
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
        requestLocationPermission()
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

}