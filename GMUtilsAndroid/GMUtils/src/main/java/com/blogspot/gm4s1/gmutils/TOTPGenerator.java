package com.blogspot.gm4s1.gmutils;

/**
 * this class wrote with Kotlin language
 * I changed file extension to java to make all module support java only
 * to use this class:
 * implementation 'com.github.aabhasr1:OtpView:v1.1.2'
 * then uncomment code
 */

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer with (Java/Kotlin, Swift)
 * Have experience with:
 *      - (C/C++, C#) languages
 *      - .NET environment
 *      - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public class TOTPGenerator {}

//import android.content.Context
//        import android.graphics.Bitmap
//        import android.graphics.BitmapFactory
//        import android.os.Build
//        import android.util.DisplayMetrics
//        import android.util.Log
//        import androidx.annotation.DimenRes
//        import com.google.zxing.BarcodeFormat
//        import com.google.zxing.MultiFormatWriter
//        import com.google.zxing.client.j2se.MatrixToImageWriter
//        import com.google.zxing.common.BitArray
//        import com.google.zxing.qrcode.QRCodeWriter
//        import com.journeyapps.barcodescanner.BarcodeEncoder
//        import dev.samstevens.totp.code.CodeGenerator
//        import dev.samstevens.totp.code.DefaultCodeGenerator
//        import dev.samstevens.totp.code.DefaultCodeVerifier
//        import dev.samstevens.totp.code.HashingAlgorithm
//        import dev.samstevens.totp.exceptions.QrGenerationException
//        import dev.samstevens.totp.exceptions.TimeProviderException
//        import dev.samstevens.totp.qr.QrData
//        import dev.samstevens.totp.qr.QrGenerator
//        import dev.samstevens.totp.qr.ZxingPngQrGenerator
//        import dev.samstevens.totp.secret.DefaultSecretGenerator
//        import dev.samstevens.totp.secret.SecretGenerator
//        import dev.samstevens.totp.time.SystemTimeProvider
//        import dev.samstevens.totp.time.TimeProvider
//        import utils.R
//        import utils.preferences.SettingsPreferences
//        import utils.utils.AppLog
//        import utils.utils.TextHelper
//        import java.io.ByteArrayOutputStream
//        import java.time.Instant
//        import kotlin.math.floor
//
//class TOTPGenerator {
//    companion object {
//        val HMAC_ALGORITHM = HashingAlgorithm.SHA1
//        const val DIGIT_LENGTH = 8
//        const val PERIOD = 30
//    }
//
//    fun generateSharedSecret(): String {
//        val secretGenerator: SecretGenerator = DefaultSecretGenerator() //default: 32-char
//        val secret = secretGenerator.generate()
//        return secret
//    }
//
//    class SystemTimeProvider : TimeProvider {
//        @Throws(TimeProviderException::class)
//        override fun getTime(): Long {
////            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                return Instant.now().epochSecond
////            } else {
//            return System.currentTimeMillis() / 1000
////            }
//
//        }
//    }
//
//    fun generateCode(secret: String): String {
//        val timeProvider: TimeProvider = SystemTimeProvider()
//        val time = timeProvider.time
//        // Get the current number of seconds since the epoch and
//        // calculate the number of time periods passed.
//        val currentBucket = floor(time / PERIOD.toDouble()).toLong()
//
////        Log.d(
////            "***TOTP",
////            "Time/Period: ${String.format("%f", (time / PERIOD.toDouble()))} | " +
////                    "Current Bucket: $currentBucket | " +
////                    "RemainSec: ${getRemainSeconds()}\n\n"
////        )
//
//        val codeGenerator: CodeGenerator = DefaultCodeGenerator(
//                HMAC_ALGORITHM,
//                DIGIT_LENGTH
//        )
//
//        var code = codeGenerator.generate(secret, currentBucket)
//
////        if (SettingsPreferences.Language.usingArabic()) {
////            code = TextHelper.convertArabicNumberToEnglish(code)
////        }
//
//        return code
//    }
//
//    fun getRemainSeconds(): Int {
//        val time = SystemTimeProvider().time
//        val intervalsCount = (time / PERIOD.toDouble()).toInt()
//        val nextIntervalStart = intervalsCount.toLong() * PERIOD + PERIOD
//        val differenceSecond = (nextIntervalStart - time).toInt()
//
//        return differenceSecond
//    }
//
//    fun generateQRCode(
//            secret: String,
//            userName: String,
//            appName: String,
//            QRType: BarcodeFormat,
//            imageWidth: Int,
//            imageHeight: Int
//    ): Bitmap {
//        /**
//         * generate URI/message to encode into the QR image, in the format specified here:
//         * https://github.com/google/google-authenticator/wiki/Key-Uri-Format
//         * otpauth://TYPE/LABEL?PARAMETERS
//         *
//         * otpauth://totp/Example:alice@google.com?secret=JBSWY3DPEHPK3PXP&issuer=Example
//         * otpauth://totp/ACME%20Co:john.doe@email.com?secret=HXDMVJECJJWSRB3HWIZR4IFUGFTMXBOZ&issuer=ACME%20Co&algorithm=SHA1&digits=6&period=30
//         */
//        val data = QrData.Builder()
//                .secret(secret)
//                .label(userName) //"example@example.com")
//                .issuer(appName)  //"AppName")
//                .algorithm(HMAC_ALGORITHM) // More on this below
//                .digits(DIGIT_LENGTH)
//                .period(PERIOD)
//                .build()
//
//        return generateQRCode(textForEncoding = data.uri, QRType = QRType, imageWidth = imageWidth, imageHeight = imageHeight)
//    }
//
//    fun generateQRCode(
//            textForEncoding: String,
//            QRType: BarcodeFormat,
//            imageWidth: Int,
//            imageHeight: Int
//    ): Bitmap {
//        val qrGen = QRGen()
//        return qrGen.generate(textForEncoding, QRType, imageWidth, imageHeight)
//    }
//
//    fun generateQRCode(
//            secret: String,
//            userName: String,
//            appName: String,
//            QRType: BarcodeFormat,
//            @DimenRes imageWidthDp: Int,
//            @DimenRes imageHeightDp: Int,
//            context: Context
//    ): Bitmap {
//        val imageWidth = context.resources.getDimensionPixelSize(imageWidthDp)
//        val imageHeight = context.resources.getDimensionPixelSize(imageHeightDp)
//
//        return generateQRCode(
//                secret = secret,
//                userName = userName,
//                appName = appName,
//                QRType = QRType,
//                imageWidth = imageWidth,
//                imageHeight = imageHeight
//        )
//    }
//
//    fun generateQRCode(
//            textForEncoding: String,
//            QRType: BarcodeFormat,
//            @DimenRes imageWidthDp: Int,
//            @DimenRes imageHeightDp: Int,
//            context: Context
//    ): Bitmap {
//        val qrGen = QRGen()
//
//        val imageWidth = context.resources.getDimensionPixelSize(imageWidthDp)
//        val imageHeight = context.resources.getDimensionPixelSize(imageHeightDp)
//
//        return qrGen.generate(textForEncoding, QRType, imageWidth, imageHeight)
//    }
//
//    fun verifyingCode(secret: String, code: String): Boolean {
//        val timeProvider: TimeProvider = SystemTimeProvider()
//        val codeGenerator: CodeGenerator = DefaultCodeGenerator(
//                HMAC_ALGORITHM,
//                DIGIT_LENGTH
//        )
//
//        val verifier = DefaultCodeVerifier(codeGenerator, timeProvider);
//        verifier.setTimePeriod(30) // sets the time period for codes to be valid for to 30 seconds
//        verifier.setAllowedTimePeriodDiscrepancy(2) // allow codes valid for 2 time periods before/after to pass as valid
//
//        val successful = verifier.isValidCode(
//                secret, // secret = the shared secret for the user
//                code    // code = the code submitted by the user
//        )
//
//        return successful
//    }
//
//}
//
//class QRGen {
//    private val writer = MultiFormatWriter()
//    private var qrBitmap: Bitmap? = null
//    private var barcodeBitmap: Bitmap? = null
//
//    fun generate(data: QrData, type: BarcodeFormat, imageWidth: Int, imageHeight: Int): Bitmap {
//        return generate(data.uri, type, imageWidth, imageHeight)
//    }
//
//    fun generate(data: String, type: BarcodeFormat, imageWidth: Int, imageHeight: Int): Bitmap {
//
//        if (type == BarcodeFormat.QR_CODE) {
//
//            val bitMatrix = writer.encode(
//                    data,
//                    type,
//                    imageWidth,
//                    imageHeight
//            )
//
//            qrBitmap = Bitmap.createBitmap(
//                    bitMatrix.width,
//                    bitMatrix.height,
//                    Bitmap.Config.ARGB_8888
//            )
//
//            val BLACK = (0xFF000000).toInt()
//            val WHITE = (0xFFFFFFFF).toInt()
//            var row = BitArray(bitMatrix.width)
//
//            for (y in 0 until bitMatrix.height) {
//                row = bitMatrix.getRow(y, row)
//
//                for (x in 0 until bitMatrix.width) {
//                    qrBitmap!!.setPixel(x, y, if (row[x]) BLACK else WHITE)
//                }
//            }
//            return qrBitmap as Bitmap
//
//        } else {
//            // Barcode
//            val bitMatrix = writer.encode(
//                    data,
//                    type,
//                    imageWidth,
//                    imageHeight
//            )
//
//            val barcodeEncoder = BarcodeEncoder()
//            barcodeBitmap = barcodeEncoder.createBitmap(bitMatrix)
//            return barcodeBitmap as Bitmap
//
//        }
//    }
//
//}