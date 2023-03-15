package gmutils.security

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import gmutils.app.BaseApplication
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


class CryptographicUtil {

    private fun getCipher(mode: Int) : Cipher {
        val keygen = KeyGenerator.getInstance("AES")
        keygen.init(256)

        val key: SecretKey = keygen.generateKey()

        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(mode, key)

        return cipher
    }

    fun encrypt(plaintext: String) : ByteArray {
        val cipher = getCipher(Cipher.ENCRYPT_MODE)
        //val iv: ByteArray = cipher.iv
        return cipher.doFinal(plaintext.toByteArray())
    }

    fun decrypt(ciphertext: ByteArray) : ByteArray {
        val cipher = getCipher(Cipher.DECRYPT_MODE)
        return cipher.doFinal(ciphertext)
    }

}

@RequiresApi(Build.VERSION_CODES.M)
class CryptographicUtil23(keyName: String? = null) {
    private val KEY_NAME = keyName ?: BaseApplication.current().packageName

    init {
        generateSecretKey(
            KeyGenParameterSpec.Builder(
                KEY_NAME,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(true)
                .build()
        )
    }

    private fun generateSecretKey(keyGenParameterSpec: KeyGenParameterSpec) {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )
        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")

        // Before the keystore can be accessed, it must be loaded.
        keyStore.load(null)
        return keyStore.getKey(KEY_NAME, null) as SecretKey
    }

    private fun getCipher(): Cipher {
        return Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7
        )
    }


    fun encrypt(data: String): ByteArray {
        val cipher = getCipher()
        val secretKey = getSecretKey()

        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        return cipher.doFinal(
            data.toByteArray(Charset.defaultCharset())
        )
    }

    fun decrypt(cipherData: ByteArray): ByteArray {
        val cipher = getCipher()
        val secretKey = getSecretKey()

        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        return cipher.doFinal(cipherData)
    }

    //==============================================================================================

    fun encryptFile(context: Context, destinationFile: File, fileContent: String) {
        // Although you can define your own key generation parameter specification, it's
        // recommended that you use the value specified here.
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        val encryptedFile = EncryptedFile.Builder(
            destinationFile,
            context,
            mainKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        val fileContent = fileContent.toByteArray(StandardCharsets.UTF_8)

        encryptedFile.openFileOutput().apply {
            write(fileContent)
            flush()
            close()
        }
    }

    fun decryptFileContent(context: Context, file: File): ByteArray {
        // Although you can define your own key generation parameter specification, it's
        // recommended that you use the value specified here.
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        val encryptedFile = EncryptedFile.Builder(
            file,
            context,
            mainKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        val inputStream = encryptedFile.openFileInput()
        val byteArrayOutputStream = ByteArrayOutputStream()
        var nextByte: Int = inputStream.read()
        while (nextByte != -1) {
            byteArrayOutputStream.write(nextByte)
            nextByte = inputStream.read()
        }

        return byteArrayOutputStream.toByteArray()
    }


}
