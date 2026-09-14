package com.app.navigation

import android.content.Context
import android.location.Location
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
    private val gpsLocationProvider = GpsLocationProvider()
    private var navigation: MapboxNavigation? = null

    private val gpsListener: (Location) -> Unit = { location ->
        gpsLocationProvider.updateLocation(location)
    }

    fun initialize() {
        if (!MapboxNavigationApp.isSetup()) {
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
        }
        navigation = MapboxNavigationApp.current()
        GpsLocationBus.subscribe(gpsListener)
    }

    private val locationObserver = object : LocationObserver {
        override fun onNewRawLocation(
            rawLocation: com.mapbox.common.location.Location
        ) {
        }
        override fun onNewLocationMatcherResult(
            locationMatcherResult: LocationMatcherResult
        ) {
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
                    navigation.setNavigationRoutes(
                        routes
                    )
                    navigation.unregisterLocationObserver(locationObserver)
                    navigation.unregisterRoutesObserver(routesObserver)
                    navigation.unregisterRouteProgressObserver(routeProgressObserver)
                    navigation.registerLocationObserver(locationObserver)
                    navigation.registerRoutesObserver(routesObserver)
                    navigation.registerRouteProgressObserver(routeProgressObserver)
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
                }
                override fun onFailure(
                    reasons: List<RouterFailure>,
                    routeOptions: RouteOptions
                ) {
                }
                override fun onCanceled(
                    routeOptions: RouteOptions,
                    routerOrigin: String
                ) {
                }
            }
        )
    }

    private val routesObserver = object : RoutesObserver {
        override fun onRoutesChanged(
            result: RoutesUpdatedResult
        ) {
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



                val tripProgress = NavigationTripProgress(
                    distanceRemaining = routeProgress.distanceRemaining.toDouble(),
                    durationRemaining = routeProgress.durationRemaining
                )


                onTripProgressChanged(tripProgress)

                if (routeProgress.currentState == RouteProgressState.COMPLETE) {
                    navigation?.unregisterRouteProgressObserver(this)
                    navigation?.stopTripSession()
                    NavigationMapViewManager.updateRoute(emptyList())
                    NavigationMapViewManager.setNavigationActive(false)
                    NavigationMapViewManager.clearRouteArrows()  // ← ALTERAÇÃO: limpa setas
                    return
                }

                NavigationMapViewManager.updateRouteArrows(routeProgress)  // ← ALTERAÇÃO: atualiza setas


                val maneuvers =
                    maneuverApi.getManeuvers(
                        routeProgress
                    )
                maneuvers.onValue { maneuverList ->

                    // ← ALTERAÇÃO: descobrir o próximo logradouro
                    val upcomingStep = routeProgress.currentLegProgress?.upcomingStep
                    val nextRoadName = upcomingStep?.name()

                    val navigationManeuvers =
                        maneuverList.map { maneuver ->
                            NavigationManeuver(
                                id = maneuver.primary.id,
                                text = maneuver.primary.text,
                                type = maneuver.primary.type,
                                modifier = maneuver.primary.modifier,
                                secondaryText = maneuver.secondary?.text,
                                secondaryType = maneuver.secondary?.type,
                                secondaryModifier = maneuver.secondary?.modifier,
                                subText = maneuver.sub?.text,
                                subType = maneuver.sub?.type,
                                subModifier = maneuver.sub?.modifier,
                                distanceRemaining = maneuver.stepDistance.distanceRemaining,
                                totalDistance = maneuver.stepDistance.totalDistance,
                                latitude = maneuver.maneuverPoint.latitude(),
                                longitude = maneuver.maneuverPoint.longitude(),
                                nextRoadName = nextRoadName  // ← ALTERAÇÃO
                            )
                        }
                    onManeuversChanged(navigationManeuvers)
                }
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


}