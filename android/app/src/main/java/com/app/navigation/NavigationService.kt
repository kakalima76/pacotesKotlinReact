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
import com.app.navigation.dto.NavigationManeuver
import com.app.navigation.map.NavigationMapViewManager
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.base.route.NavigationRouterCallback
import com.mapbox.navigation.base.route.RouterFailure
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Point
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.directions.session.RoutesUpdatedResult
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.base.trip.model.RouteProgress
import com.mapbox.navigation.core.formatter.MapboxDistanceFormatter
import com.mapbox.navigation.base.formatter.DistanceFormatterOptions
import com.mapbox.navigation.tripdata.maneuver.api.MapboxManeuverApi
import com.mapbox.navigation.base.trip.model.RouteProgressState
import com.mapbox.bindgen.Expected
import com.mapbox.navigation.tripdata.maneuver.model.Maneuver
import com.mapbox.navigation.tripdata.maneuver.model.ManeuverError
import com.app.navigation.dto.NavigationTripProgress


class NavigationService(
    private val context: Context,
    private val onManeuversChanged: (List<NavigationManeuver>) -> Unit,
    private val onTripProgressChanged: (NavigationTripProgress) -> Unit
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

            NavigationMapViewManager.updateNavigationLocation(
                locationMatcherResult.enhancedLocation,
                locationMatcherResult.keyPoints
            )
        }
    }

    fun setRoute(
        originLatitude: Double,
        originLongitude: Double,
        destinationLatitude: Double,
        destinationLongitude: Double
    ) {

        val navigation = this.navigation

        if (navigation == null) {
            Log.e(
                "NavigationService",
                "Navigation não inicializado"
            )
            return
        }

        val origin = Point.fromLngLat(
            originLongitude,
            originLatitude
        )

        val destination = Point.fromLngLat(
            destinationLongitude,
            destinationLatitude
        )

        val routeOptions =
            RouteOptions.builder()
                .coordinatesList(
                    listOf(
                        origin,
                        destination
                    )
                )
                .profile("driving")
                .steps(true)
                .bannerInstructions(true)
                .build()

        navigation.requestRoutes(
            routeOptions,
            object : NavigationRouterCallback {

                override fun onRoutesReady(
                    routes: List<NavigationRoute>,
                    routerOrigin: String
                ) {

                    Log.d(
                        "Rota",
                        "ROTA CALCULADA: ${routes.size}"
                    )

                    Log.d(
                        "Rota",
                        "BANNER INSTRUCTIONS: " +
                                "${routes.firstOrNull()?.directionsRoute?.legs()?.firstOrNull()?.steps()?.size}"
                    )

                    navigation.setNavigationRoutes(
                        routes
                    )

                    navigation.unregisterLocationObserver(locationObserver)
                    navigation.unregisterRoutesObserver(routesObserver)
                    navigation.unregisterRouteProgressObserver(routeProgressObserver)

                    navigation.registerLocationObserver(locationObserver)
                    navigation.registerRoutesObserver(routesObserver)
                    navigation.registerRouteProgressObserver(routeProgressObserver)

                    Log.d(
                        "NavigationService",
                        "LocationObserver e RoutesObserver e registerRouteProgressObserver registrados"
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
                    }

                    NavigationMapViewManager.updateRoute(
                        routes
                    )

                    Log.d(
                        "NavigationService",
                        "ROTA DEFINIDA NO NAVIGATION"
                    )
                }

                override fun onFailure(
                    reasons: List<RouterFailure>,
                    routeOptions: RouteOptions
                ) {
                    Log.e(
                        "NavigationRoute",
                        "FALHA COMPLETA: $reasons"
                    )

                    reasons.forEach { reason ->
                        Log.e(
                            "NavigationRoute",
                            "type=${reason.type}"
                        )

                        Log.e(
                            "NavigationRoute",
                            "message=${reason.message}"
                        )

                        Log.e(
                            "NavigationRoute",
                            "url=${reason.url}"
                        )
                    }
                }

                override fun onCanceled(
                    routeOptions: RouteOptions,
                    routerOrigin: String
                ) {

                    Log.d(
                        "NavigationService",
                        "CÁLCULO DA ROTA CANCELADO"
                    )
                }
            }
        )
    }

    private val routesObserver = object : RoutesObserver {

        override fun onRoutesChanged(
            result: RoutesUpdatedResult
        ) {
            Log.d(
                "Rota",
                "ROTAS ATUALIZADAS: ${result.navigationRoutes.size}"
            )

            NavigationMapViewManager.updateRoute(
                result.navigationRoutes
            )
        }
    }

    private val routeProgressObserver =
        object : RouteProgressObserver {

            override fun onRouteProgressChanged(
                routeProgress: RouteProgress
            ) {

                Log.d(
                    "Rota",
                    "PROGRESSO: ${routeProgress.currentState}"
                )

                val tripProgress = NavigationTripProgress(
                    distanceRemaining = routeProgress.distanceRemaining.toDouble(),
                    durationRemaining = routeProgress.durationRemaining
                )

                Log.d(
                    "Rota",
                    "DISTÂNCIA RESTANTE: ${tripProgress.distanceRemaining}"
                )

                Log.d(
                    "Rota",
                    "TEMPO RESTANTE: ${tripProgress.durationRemaining}"
                )

                onTripProgressChanged(tripProgress)


                if (routeProgress.currentState == RouteProgressState.COMPLETE) {

                    Log.d(
                        "Rota",
                        "ROTA CONCLUÍDA"
                    )

                    navigation?.unregisterRouteProgressObserver(
                        this
                    )

                    navigation?.stopTripSession()

                    return
                }

                val maneuvers =
                    maneuverApi.getManeuvers(
                        routeProgress
                    )

                maneuvers.fold(
                    { error ->
                        Log.e(
                            "Rota",
                            "ERRO MANOBRA: ${error.errorMessage}"
                        )
                    },
                    {
                        maneuvers.onValue { maneuverList ->

                            Log.d(
                                "Rota",
                                "MANOBRAS: ${maneuverList.size}"
                            )

                            val navigationManeuvers =
                                maneuverList.map { maneuver ->

                                    NavigationManeuver(
                                        id = maneuver.primary.id,
                                        text = maneuver.primary.text,
                                        type = maneuver.primary.type,
                                        modifier = maneuver.primary.modifier,
                                        distanceRemaining = maneuver.stepDistance.distanceRemaining,
                                        totalDistance = maneuver.stepDistance.totalDistance,
                                        latitude = maneuver.maneuverPoint.latitude(),
                                        longitude = maneuver.maneuverPoint.longitude()
                                    )
                                }
                            onManeuversChanged(navigationManeuvers)
                        }
                    }
                )
            }
        }


    private val maneuverApi by lazy {
        MapboxManeuverApi(
            MapboxDistanceFormatter(
                DistanceFormatterOptions.Builder(
                    context
                ).build()
            )
        )
    }

    private fun extractManeuvers(
        maneuvers: Expected<ManeuverError, List<Maneuver>>
    ): List<NavigationManeuver> {

        return maneuvers.fold(
            { error ->
                Log.e(
                    "NavigationService",
                    "Erro ao extrair manobras: ${error.errorMessage}"
                )

                emptyList()
            },
            { maneuverList ->

                maneuverList.map { maneuver ->

                    NavigationManeuver(
                        id = maneuver.primary.id,
                        text = maneuver.primary.text,
                        type = maneuver.primary.type,
                        modifier = maneuver.primary.modifier,
                        distanceRemaining =
                            maneuver.stepDistance.distanceRemaining,
                        totalDistance =
                            maneuver.stepDistance.totalDistance,
                        latitude =
                            maneuver.maneuverPoint.latitude(),
                        longitude =
                            maneuver.maneuverPoint.longitude()
                    )
                }
            }
        )
    }




}