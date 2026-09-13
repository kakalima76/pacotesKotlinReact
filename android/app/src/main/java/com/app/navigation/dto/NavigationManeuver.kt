package com.app.navigation.dto

data class NavigationManeuver(
    val id: String,

    val text: String,
    val type: String?,
    val modifier: String?,

    val secondaryText: String?,
    val secondaryType: String?,
    val secondaryModifier: String?,

    val subText: String?,
    val subType: String?,
    val subModifier: String?,

    val distanceRemaining: Double?,
    val totalDistance: Double,
    val latitude: Double,
    val longitude: Double,

    val nextRoadName: String?  // ← ALTERAÇÃO: novo campo

)