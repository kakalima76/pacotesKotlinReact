package com.app.navigation.dto

data class NavigationManeuver(
    val id: String,
    val text: String,
    val type: String?,
    val modifier: String?,
    val distanceRemaining: Double?,
    val totalDistance: Double,
    val latitude: Double,
    val longitude: Double
)