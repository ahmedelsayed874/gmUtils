package gmutils.security

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.DimenRes
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitArray
import com.journeyapps.barcodescanner.BarcodeEncoder
import dev.samstevens.totp.code.CodeGenerator
import dev.samstevens.totp.code.DefaultCodeGenerator
import dev.samstevens.totp.code.DefaultCodeVerifier
import dev.samstevens.totp.code.HashingAlgorithm
import dev.samstevens.totp.exceptions.TimeProviderException
import dev.samstevens.totp.qr.QrData
import dev.samstevens.totp.secret.DefaultSecretGenerator
import dev.samstevens.totp.secret.SecretGenerator
import dev.samstevens.totp.time.TimeProvider
import java.lang.Math.floor

/**
 * to use this class:
 * implementation 'dev.samstevens.totp:totp:1.6'
 * implementation 'com.journeyapps:zxing-android-embedded:3.2.0@aar'
 */

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer (Java/Kotlin, Swift) also Flutter (Dart)
 * Have precedent experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - Java swing
 * - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
class TOTPGenerator {
    class Configuration {
        var HMAC_ALGORITHM = HashingAlgorithm.SHA1
        var DIGIT_LENGTH = 8
        var PERIOD = 30
        val BARCODE_FORMAT = BarcodeFormat.CODE_128
    }

    companion object {
        val CONFIGS = Configuration()
    }

    class SystemTimeProvider : TimeProvider {
        @Throws(TimeProviderException::class)
        override fun getTime(): Long {
//            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                return Instant.now().epochSecond
//            } else {
            return System.currentTimeMillis() / 1000
//            }

        }
    }

    fun generateSharedSecret(): String {
        val secretGenerator: SecretGenerator = DefaultSecretGenerator() //default: 32-char
        val secret = secretGenerator.generate()
        return secret
    }

    fun generateCode(secret: String): String {
        val timeProvider: TimeProvider = SystemTimeProvider()
        val time = timeProvider.time

        // Get the current number of seconds since the epoch and
        // calculate the number of time periods passed.
        val currentBucket = floor(time / CONFIGS.PERIOD.toDouble()).toLong()

        val codeGenerator: CodeGenerator = DefaultCodeGenerator(
            CONFIGS.HMAC_ALGORITHM,
            CONFIGS.DIGIT_LENGTH
        )

        var code = codeGenerator.generate(secret, currentBucket)

        return code
    }

    fun getRemainSeconds(): Int {
        val time = SystemTimeProvider().time
        val intervalsCount = (time / CONFIGS.PERIOD.toDouble()).toInt()
        val nextIntervalStart = intervalsCount.toLong() * CONFIGS.PERIOD + CONFIGS.PERIOD
        val differenceSecond = (nextIntervalStart - time).toInt()

        return differenceSecond
    }

    //----------------------------------------------------------------------------------------------

    fun generateQRCode(
        textForEncoding: String,
        width: Int
    ): Bitmap {
        val qrGen = QRGen()
        return qrGen.generate(textForEncoding, BarcodeFormat.QR_CODE, width, width)
    }

    fun generateQRCode(
        secret: String,
        userName: String,
        appName: String,
        width: Int
    ): Bitmap {
        /**
         * generate URI/message to encode into the QR image, in the format specified here:
         * https://github.com/google/google-authenticator/wiki/Key-Uri-Format
         * otpauth://TYPE/LABEL?PARAMETERS
         *
         * otpauth://totp/Example:alice@google.com?secret=JBSWY3DPEHPK3PXP&issuer=Example
         * otpauth://totp/ACME%20Co:john.doe@email.com?secret=HXDMVJECJJWSRB3HWIZR4IFUGFTMXBOZ&issuer=ACME%20Co&algorithm=SHA1&digits=6&period=30
         */
        val data = QrData.Builder()
            .secret(secret)
            .label(userName) //"example@example.com")
            .issuer(appName)  //"AppName")
            .algorithm(CONFIGS.HMAC_ALGORITHM) // More on this below
            .digits(CONFIGS.DIGIT_LENGTH)
            .period(CONFIGS.PERIOD)
            .build()

        return generateQRCode(textForEncoding = data.uri, width = width)
    }

    fun generateQRCode(
        secret: String,
        userName: String,
        appName: String,
        @DimenRes widthDp: Int,
        context: Context
    ): Bitmap {
        val width = context.resources.getDimensionPixelSize(widthDp)

        return generateQRCode(
            secret = secret,
            userName = userName,
            appName = appName,
            width = width
        )
    }

    fun generateQRCode(
        textForEncoding: String,
        @DimenRes widthDp: Int,
        context: Context
    ): Bitmap {
        val qrGen = QRGen()

        val width = context.resources.getDimensionPixelSize(widthDp)

        return qrGen.generate(textForEncoding, BarcodeFormat.QR_CODE, width, width)
    }

    //----------------------------------------------------------------------------------------------

    fun generateBarcode(
        textForEncoding: String,
        imageWidth: Int,
        imageHeight: Int
    ): Bitmap {
        val qrGen = QRGen()
        return qrGen.generate(textForEncoding, CONFIGS.BARCODE_FORMAT, imageWidth, imageHeight)
    }

    fun generateBarcode(
        secret: String,
        userName: String,
        appName: String,
        imageWidth: Int,
        imageHeight: Int
    ): Bitmap {
        /**
         * generate URI/message to encode into the QR image, in the format specified here:
         * https://github.com/google/google-authenticator/wiki/Key-Uri-Format
         * otpauth://TYPE/LABEL?PARAMETERS
         *
         * otpauth://totp/Example:alice@google.com?secret=JBSWY3DPEHPK3PXP&issuer=Example
         * otpauth://totp/ACME%20Co:john.doe@email.com?secret=HXDMVJECJJWSRB3HWIZR4IFUGFTMXBOZ&issuer=ACME%20Co&algorithm=SHA1&digits=6&period=30
         */
        val data = QrData.Builder()
            .secret(secret)
            .label(userName) //"example@example.com")
            .issuer(appName)  //"AppName")
            .algorithm(CONFIGS.HMAC_ALGORITHM) // More on this below
            .digits(CONFIGS.DIGIT_LENGTH)
            .period(CONFIGS.PERIOD)
            .build()

        return generateBarcode(textForEncoding = data.uri, imageWidth = imageWidth, imageHeight = imageHeight)
    }

    fun generateBarcode(
        secret: String,
        userName: String,
        appName: String,
        @DimenRes imageWidthDp: Int,
        @DimenRes imageHeightDp: Int,
        context: Context
    ): Bitmap {
        val imageWidth = context.resources.getDimensionPixelSize(imageWidthDp)
        val imageHeight = context.resources.getDimensionPixelSize(imageHeightDp)

        return generateBarcode(
            secret = secret,
            userName = userName,
            appName = appName,
            imageWidth = imageWidth,
            imageHeight = imageHeight
        )
    }

    fun generateBarcode(
        textForEncoding: String,
        @DimenRes imageWidthDp: Int,
        @DimenRes imageHeightDp: Int,
        context: Context
    ): Bitmap {
        val qrGen = QRGen()

        val imageWidth = context.resources.getDimensionPixelSize(imageWidthDp)
        val imageHeight = context.resources.getDimensionPixelSize(imageHeightDp)

        return qrGen.generate(textForEncoding, CONFIGS.BARCODE_FORMAT, imageWidth, imageHeight)
    }

    //----------------------------------------------------------------------------------------------

    fun verifyingCode(secret: String, code: String): Boolean {
        val timeProvider: TimeProvider = SystemTimeProvider()
        val codeGenerator: CodeGenerator = DefaultCodeGenerator(
            CONFIGS.HMAC_ALGORITHM,
            CONFIGS.DIGIT_LENGTH
        )

        val verifier = DefaultCodeVerifier(codeGenerator, timeProvider);
        verifier.setTimePeriod(30) // sets the time period for codes to be valid for to 30 seconds
        verifier.setAllowedTimePeriodDiscrepancy(2) // allow codes valid for 2 time periods before/after to pass as valid

        val successful = verifier.isValidCode(
            secret, // secret = the shared secret for the user
            code    // code = the code submitted by the user
        )

        return successful
    }

}

class QRGen {
    private val writer = MultiFormatWriter()
    private var qrBitmap: Bitmap? = null
    private var barcodeBitmap: Bitmap? = null

    fun generate(data: QrData, type: BarcodeFormat, imageWidth: Int, imageHeight: Int): Bitmap {
        return generate(data.uri, type, imageWidth, imageHeight)
    }

    fun generate(data: String, type: BarcodeFormat, imageWidth: Int, imageHeight: Int): Bitmap {

        if (type == BarcodeFormat.QR_CODE) {

            val bitMatrix = writer.encode(
                data,
                type,
                imageWidth,
                imageHeight
            )
            qrBitmap = Bitmap.createBitmap(
                bitMatrix.width,
                bitMatrix.height,
                Bitmap.Config.ARGB_8888
            )

            val BLACK = (0xFF000000).toInt()
            val WHITE = (0xFFFFFFFF).toInt()
            var row = BitArray(bitMatrix.width)

            for (y in 0 until bitMatrix.height) {
                row = bitMatrix.getRow(y, row)

                for (x in 0 until bitMatrix.width) {
                    qrBitmap!!.setPixel(x, y, if (row[x]) BLACK else WHITE)
                }
            }
            return qrBitmap as Bitmap

        } else {

            // Barcode
            val bitMatrix = writer.encode(
                data,
                type,
                imageWidth,
                imageHeight
            )

            val barcodeEncoder = BarcodeEncoder()
            barcodeBitmap = barcodeEncoder.createBitmap(bitMatrix)
            return barcodeBitmap as Bitmap
        }
    }

}