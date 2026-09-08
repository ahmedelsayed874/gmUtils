package gmutils.security

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object CryptoManager {
    private const val KEY_SIZE = 256
    private const val ITERATION_COUNT = 120_000 // High iteration count resists brute-force
    private const val SALT_SIZE_BYTES = 16
    private const val IV_SIZE_BYTES = 12
    private const val TAG_SIZE_BITS = 128

    // Derive AES key from password and unique salt
    private fun deriveKey(password: CharArray, salt: ByteArray): SecretKey {
        val spec = PBEKeySpec(password, salt, ITERATION_COUNT, KEY_SIZE)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val keyBytes = factory.generateSecret(spec).encoded
        return SecretKeySpec(keyBytes, "AES")
    }

    // Encrypt data: returns ByteArray structured as [Salt (16B) | IV (12B) | Ciphertext]
    fun encrypt(data: ByteArray, password: CharArray): ByteArray {
        val salt = ByteArray(SALT_SIZE_BYTES).apply { SecureRandom().nextBytes(this) }
        val iv = ByteArray(IV_SIZE_BYTES).apply { SecureRandom().nextBytes(this) }
        val key = deriveKey(password, salt)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(TAG_SIZE_BITS, iv))
        val ciphertext = cipher.doFinal(data)

        return salt + iv + ciphertext
    }

    // Decrypt data from the combined payload
    fun decrypt(combinedPayload: ByteArray, password: CharArray): ByteArray {
        require(combinedPayload.size > SALT_SIZE_BYTES + IV_SIZE_BYTES) { "Invalid payload length." }

        val salt = combinedPayload.copyOfRange(0, SALT_SIZE_BYTES)
        val iv = combinedPayload.copyOfRange(SALT_SIZE_BYTES, SALT_SIZE_BYTES + IV_SIZE_BYTES)
        val ciphertext = combinedPayload.copyOfRange(SALT_SIZE_BYTES + IV_SIZE_BYTES, combinedPayload.size)

        val key = deriveKey(password, salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_SIZE_BITS, iv))

        return cipher.doFinal(ciphertext)
    }
}