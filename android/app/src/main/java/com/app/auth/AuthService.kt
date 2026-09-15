package com.app.auth

import java.net.HttpURLConnection
import java.net.URL

class AuthService {

    fun login(username: String, password: String) {

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

            val body ="username=$username&password=$password&client_id=my-app&grant_type=password"

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

        }.start()
    }

}