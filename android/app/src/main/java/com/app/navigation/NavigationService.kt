package com.app.navigation

import android.content.Context
import android.location.Location
import android.util.Log

import com.app.gps.GpsLocationBus
import com.mapbox.common.location.DeviceLocationProviderFactory
import com.mapbox.navigation.base.options.LocationOptions
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import com.mapbox.bindgen.ExpectedFactory

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
    }
}