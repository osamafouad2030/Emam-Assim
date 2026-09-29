package com.example.data

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object SecurityUtils {
    private const val ITERATIONS = 12000
    private const val KEY_LENGTH = 256
    private const val HMAC_SECRET = "Mishkah_EdTech_Secure_Session_Key_2026"

    fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return Base64.encodeToString(salt, Base64.NO_WRAP)
    }

    fun hashPassword(password: String, saltBase64: String): String {
        val salt = Base64.decode(saltBase64, Base64.NO_WRAP)
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val hash = factory.generateSecret(spec).encoded
        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }

    fun verifyPassword(password: String, saltBase64: String, expectedHash: String): Boolean {
        val computed = hashPassword(password, saltBase64)
        return computed == expectedHash
    }

    fun generateSessionToken(userId: Int, email: String, role: String): String {
        val issuedAt = System.currentTimeMillis()
        val payload = "$userId|$email|$role|$issuedAt"
        val encodedPayload = Base64.encodeToString(payload.toByteArray(), Base64.NO_WRAP)
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(HMAC_SECRET.toByteArray(), "HmacSHA256"))
        val signature = Base64.encodeToString(mac.doFinal(encodedPayload.toByteArray()), Base64.NO_WRAP)
        return "$encodedPayload.$signature"
    }
}
