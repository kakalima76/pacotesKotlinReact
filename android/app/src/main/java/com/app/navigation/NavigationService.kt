package com.app.navigation

import android.content.Context
import android.location.Location
import android.util.Log

import com.app.gps.GpsLocationBus
import com.mapbox.bindgen.ExpectedFactory
import com.mapbox.common.location.DeviceLocationProviderFactory
import com.mapbox.navigation.base.options.LocationOptions
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class NavigationService(
    private val context: Context
) {

    /**
     * Nosso provider recebe as posições vindas do GpsService.
     *
     * IMPORTANTE:
     * Esta mesma instância será entregue ao Mapbox.
     */
    private val gpsLocationProvider = GpsLocationProvider()

    private var navigation: MapboxNavigation? = null

    /**
     * Recebe as posições do nosso GPS nativo.
     */
    private val gpsListener: (Location) -> Unit = { location ->

        Log.d(
            "NavigationService",
            "gpsListener EXECUTADO"
        )

        Log.d(
            "NavigationService",
            "Localização recebida: ${location.latitude}, ${location.longitude}"
        )

        gpsLocationProvider.updateLocation(location)
    }

    fun initialize() {

        Log.d(
            "NavigationService",
            "initialize() chamado"
        )

        if (!MapboxNavigationApp.isSetup()) {

            Log.d(
                "NavigationService",
                "Inicializando Mapbox Navigation"
            )

            /*
             * Configura nosso GpsLocationProvider como
             * Location Provider do Mapbox Navigation.
             */
            val locationOptions =
                LocationOptions.Builder()
                    .locationProviderFactory(
                        DeviceLocationProviderFactory { _ ->
                            ExpectedFactory.createValue(
                                gpsLocationProvider
                            )
                        },
                        LocationOptions.LocationProviderType.MIXED
                    )
                    .build()

            val navigationOptions =
                NavigationOptions.Builder(context)
                    .locationOptions(locationOptions)
                    .build()

            MapboxNavigationApp.setup(
                navigationOptions
            )

            Log.d(
                "NavigationService",
                "Mapbox Navigation inicializado"
            )
        }

        navigation = MapboxNavigationApp.current()

        /*
         * A partir daqui, toda localização produzida pelo
         * GpsService será enviada para o nosso provider.
         */
        GpsLocationBus.subscribe(gpsListener)

        Log.d(
            "NavigationService",
            "Navigation current: $navigation"
        )
    }

    fun start() {

        Log.d(
            "NavigationService",
            "start() chamado"
        )

        val navigation = this.navigation

        if (navigation == null) {

            Log.e(
                "NavigationService",
                "Navigation não inicializado"
            )

            return
        }

        Log.d(
            "NavigationService",
            "Navigation pronto para uso"
        )

        navigation.registerLocationObserver(locationObserver)

        Log.d(
            "NavigationService",
            "LocationObserver registrado"
        )

        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            navigation.startTripSession()

            Log.d(
                "NavigationService",
                "Trip Session iniciada"
            )
        } else {
            Log.e(
                "NavigationService",
                "Permissão de localização não concedida"
            )
        }
    }

    private val locationObserver = object : LocationObserver {

        override fun onNewRawLocation(
            rawLocation: com.mapbox.common.location.Location
        ) {
            Log.d(
                "NavigationService",
                "MAPBOX recebeu localização RAW: " +
                        "${rawLocation.latitude}, ${rawLocation.longitude}"
            )
        }

        override fun onNewLocationMatcherResult(
            locationMatcherResult: LocationMatcherResult
        ) {
            Log.d(
                "NavigationService",
                "MAPBOX recebeu localização MATCHED"
            )
        }
    }
}