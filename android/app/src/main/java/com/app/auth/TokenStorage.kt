package com.app.auth

import android.content.Context
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class TokenStorage(
    context: Context
) {

    private val preferences = context.getSharedPreferences(
        "auth_tokens",
        Context.MODE_PRIVATE
    )

    private val keyAlias = "auth_tokens_key"

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private fun getOrCreateKey(): SecretKey {
        val existingKey = keyStore.getKey(keyAlias, null)

        if (existingKey != null) {
            return existingKey as SecretKey
        }

        val keyGenerator = KeyGenerator.getInstance(
            "AES",
            "AndroidKeyStore"
        )

        keyGenerator.init(
            android.security.keystore.KeyGenParameterSpec.Builder(
                keyAlias,
                android.security.keystore.KeyProperties.PURPOSE_ENCRYPT or
                        android.security.keystore.KeyProperties.PURPOSE_DECRYPT
            )
                .setKeySize(256)
                .setBlockModes(
                    android.security.keystore.KeyProperties.BLOCK_MODE_GCM
                )
                .setEncryptionPaddings(
                    android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE
                )
                .build(),
            java.security.SecureRandom()
        )

        return keyGenerator.generateKey()
    }

    private fun encrypt(value: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")

        cipher.init(
            Cipher.ENCRYPT_MODE,
            getOrCreateKey()
        )

        val encrypted = cipher.doFinal(value.toByteArray())

        val iv = Base64.encodeToString(
            cipher.iv,
            Base64.NO_WRAP
        )

        val data = Base64.encodeToString(
            encrypted,
            Base64.NO_WRAP
        )

        return "$iv:$data"
    }

    private fun decrypt(value: String): String {
        val parts = value.split(":")

        val iv = Base64.decode(
            parts[0],
            Base64.NO_WRAP
        )

        val encrypted = Base64.decode(
            parts[1],
            Base64.NO_WRAP
        )

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")

        cipher.init(
            Cipher.DECRYPT_MODE,
            getOrCreateKey(),
            GCMParameterSpec(128, iv)
        )

        return String(
            cipher.doFinal(encrypted)
        )
    }

    fun save(
        accessToken: String,
        refreshToken: String,
        expiresIn: Long
    ) {
        val expiresAt = System.currentTimeMillis() + (expiresIn * 1000)

        preferences.edit()
            .putString(
                "access_token",
                encrypt(accessToken)
            )
            .putString(
                "refresh_token",
                encrypt(refreshToken)
            )
            .putLong(
                "access_token_expires_at",
                expiresAt
            )
            .apply()
    }

    fun getAccessToken(): String? {
        return preferences
            .getString("access_token", null)
            ?.let { decrypt(it) }
    }

    fun isAccessTokenValid(): Boolean {
        val expiresAt = preferences.getLong(
            "access_token_expires_at",
            0L
        )

        return expiresAt > System.currentTimeMillis()
    }

    fun getRefreshToken(): String? {
        return preferences
            .getString("refresh_token", null)
            ?.let { decrypt(it) }
    }

    fun clear() {
        preferences.edit()
            .clear()
            .apply()
    }

    fun saveFcmToken(token: String) {
        preferences.edit()
            .putString("fcm_token", token)
            .apply()
    }

    fun getFcmToken(): String? {
        return preferences
            .getString("fcm_token", null)
    }
}
