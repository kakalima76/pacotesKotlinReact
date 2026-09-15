package com.app.auth

data class AuthResult(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)