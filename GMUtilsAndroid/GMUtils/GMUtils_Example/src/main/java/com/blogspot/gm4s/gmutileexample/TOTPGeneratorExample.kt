package com.blogspot.gm4s.gmutileexample

import android.graphics.Bitmap
import gmutils.security.TOTPGenerator

class TOTPGeneratorExample {

    fun generateQR(encodingText: String, width: Int): Bitmap {
        //to change totp configurations
        /*TOTPGenerator.CONFIGS.HMAC_ALGORITHM = HashingAlgorithm.SHA1
        TOTPGenerator.CONFIGS.DIGIT_LENGTH = 8
        TOTPGenerator.CONFIGS.PERIOD = 30*/

        val totp = TOTPGenerator()

        val qrCode = totp.generateQRCode(
            textForEncoding = encodingText,
            width = width
        )

        return qrCode
    }

    fun generateBarcode(encodingText: String, width: Int, height: Int): Bitmap {
        //to change totp configurations
        /*TOTPGenerator.CONFIGS.HMAC_ALGORITHM = HashingAlgorithm.SHA1
        TOTPGenerator.CONFIGS.DIGIT_LENGTH = 8
        TOTPGenerator.CONFIGS.PERIOD = 30
        TOTPGenerator.CONFIGS.BARCODE_FORMAT = BarcodeFormat.CODE_128*/

        val totp = TOTPGenerator()

        val barCode = totp.generateBarcode(
            textForEncoding = encodingText,
            imageWidth = width,
            imageHeight = height
        )

        return barCode
    }


}