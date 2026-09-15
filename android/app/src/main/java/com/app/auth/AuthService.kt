package com.app.auth

import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import org.json.JSONObject

class AuthService {

    fun login(
        username: String,
        password: String,
        onResult: (AuthResult) -> Unit,
        onError: (String) -> Unit
    ) {

        Thread {

            val url = URL(
                "http://10.0.2.2:8080/realms/app/protocol/openid-connect/token"
            )

            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "POST"
            connection.doOutput = true

            connection.setRequestProperty(
                "Content-Type",
                "application/x-www-form-urlencoded"
            )

            val body =
                "username=${URLEncoder.encode(username, "UTF-8")}" +
                        "&password=${URLEncoder.encode(password, "UTF-8")}" +
                        "&client_id=${URLEncoder.encode("my-app", "UTF-8")}" +
                        "&grant_type=password"

            connection.outputStream.use { outputStream ->
                outputStream.write(body.toByteArray())
            }

            val responseCode = connection.responseCode

            val response = if (responseCode >= 400) {
                connection.errorStream
                    ?.bufferedReader()
                    ?.use { it.readText() }
            } else {
                connection.inputStream
                    .bufferedReader()
                    .use { it.readText() }
            }

            val json = JSONObject(response!!)

            if (responseCode >= 400) {
                onError(
                    json.optString(
                        "error_description",
                        "Falha na autenticação"
                    )
                )
                return@Thread
            }

            val accessToken = json.getString("access_token")
            val refreshToken = json.getString("refresh_token")
            val expiresIn = json.getLong("expires_in")

            val result = AuthResult(
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiresIn = expiresIn
            )

            onResult(result)

        }.start()
    }

    fun refresh(
        refreshToken: String,
        onResult: (AuthResult) -> Unit
    ) {

        Thread {

            val url = URL(
                "http://10.0.2.2:8080/realms/app/protocol/openid-connect/token"
            )

            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "POST"
            connection.doOutput = true

            connection.setRequestProperty(
                "Content-Type",
                "application/x-www-form-urlencoded"
            )

            val body =
                "refresh_token=${URLEncoder.encode(refreshToken, "UTF-8")}" +
                        "&client_id=${URLEncoder.encode("my-app", "UTF-8")}" +
                        "&grant_type=refresh_token"

            connection.outputStream.use {
                it.write(body.toByteArray())
            }

            val responseCode = connection.responseCode

            val response = if (responseCode >= 400) {
                connection.errorStream
                    ?.bufferedReader()
                    ?.use { it.readText() }
            } else {
                connection.inputStream
                    .bufferedReader()
                    .use { it.readText() }
            }

            val json = JSONObject(response!!)

            if (responseCode >= 400) {
                return@Thread
            }

            val accessToken = json.getString("access_token")

            val newRefreshToken = json.optString(
                "refresh_token",
                refreshToken
            )

            val expiresIn = json.getLong("expires_in")

            val result = AuthResult(
                accessToken = accessToken,
                refreshToken = newRefreshToken,
                expiresIn = expiresIn
            )

            onResult(result)

            connection.disconnect()
        }.start()
    }
}