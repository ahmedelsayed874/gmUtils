package gmutils;

/*
    implementation 'androidx.biometric:biometric:1.0.0'
 */

//import androidx.biometric.BiometricManager;
//import androidx.biometric.BiometricPrompt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.Executor;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/*
    https://blog.mindorks.com/authentication-using-fingerprint-in-android-tutorial
    https://developer.android.com/training/sign-in/biometric-auth#java
*/

import androidx.annotation.RequiresApi;

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
@RequiresApi(api = Build.VERSION_CODES.M)
public class BiometricAuthentication {
    private static final String TAG = "BiometricAuthentication";

    public static void startSecuritySettings(Context context) {
        context.startActivity(new Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS));

    }

    private Cipher cipher;
    private FingerprintManager.CryptoObject cryptoObject;

    //==============================================================================================

    public boolean isFeatureAvailable(Context context, AvailabilityListener availabilityListener) {
        /*todo enable this code
        BiometricManager biometricManager = BiometricManager.from(context);

        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                if (availabilityListener != null)
                    availabilityListener.onFingerprintAvailableResult(true, "");
                return true;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                if (availabilityListener != null)
                    availabilityListener.onFingerprintAvailableResult(false, "NO HARDWARE");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                if (availabilityListener != null)
                    availabilityListener.onFingerprintAvailableResult(false, "UNAVAILABLE");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                if (availabilityListener != null)
                    availabilityListener.onFingerprintAvailableResult(false, "NONE ENROLLED");
                break;
        }

        return false;

         */
        throw new RuntimeException("Enable this function");
    }

    @RequiresPermission(value = Manifest.permission.USE_FINGERPRINT)
    public void displayFingerprintPrompt(FragmentActivity activity, String title, String subtitle, final String textToEncrypt, final AuthenticateListener authenticateListener) {
        /*Executor executor = ContextCompat.getMainExecutor(activity);
        BiometricPrompt biometricPrompt = new BiometricPrompt(
                activity,
                executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);

                        String encryptedText = "";

                        try {
                            byte[] encryptedInfo = result
                                    .getCryptoObject()
                                    .getCipher()
                                    .doFinal(textToEncrypt.getBytes(Charset.defaultCharset()));

                            encryptedText = new String(encryptedInfo);

                        } catch (BadPaddingException e) {
                            e.printStackTrace();
                        } catch (IllegalBlockSizeException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (authenticateListener != null)
                            authenticateListener.onFingerprintAuthenticated(encryptedText);
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        if (authenticateListener != null)
                            authenticateListener.onFingerprintNotAuthenticated("FAILED");
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        if (authenticateListener != null)
                            authenticateListener.onFingerprintNotAuthenticated(errString == null ? "ERROR" : errString.toString());
                    }
                });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)//)"Biometric login for my app")
                .setSubtitle(subtitle)//"Log in using your biometric credential")
                .setNegativeButtonText(SettingsPreferences.Language.usingEnglish() ? "Cancel" : "إلغاء")
                .build();

        generateKey();
        if (initCipher()) {
            cryptoObject = new FingerprintManager.CryptoObject(cipher);

            biometricPrompt.authenticate(
                    promptInfo,
                    new BiometricPrompt.CryptoObject(cipher));
        }*/
    }

    //==============================================================================================

    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private final String ENC_KEY_NAME = "enc_key";
    private FingerprintManager fingerprintManager;


    @RequiresPermission(value = Manifest.permission.USE_FINGERPRINT)
    public void init(Context context, String textToEncrypt, AvailabilityListener availabilityListener, AuthenticateListener authenticateListener) {
        if (checkLockScreen(context, availabilityListener)) {

            generateKey();

            if (initCipher()) {
                cryptoObject = new FingerprintManager.CryptoObject(cipher);

                FingerprintHelper helper = new FingerprintHelper(textToEncrypt, authenticateListener);

                if (fingerprintManager != null && cryptoObject != null) {
                    helper.startAuth(fingerprintManager, cryptoObject);
                }
            }
        }
    }

    @RequiresPermission(value = Manifest.permission.USE_FINGERPRINT)
    private boolean checkLockScreen(Context context, AvailabilityListener availabilityListener) {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);

        if (!keyguardManager.isKeyguardSecure()) {
            if (availabilityListener != null)
                availabilityListener.onFingerprintAvailableResult(false, "Lock screen security not enabled");
            return false;
        }

        if (!fingerprintManager.hasEnrolledFingerprints()) {
            if (availabilityListener != null)
                availabilityListener.onFingerprintAvailableResult(false, "No fingerprint registered");
            return false;
        }

        if (availabilityListener != null)
            availabilityListener.onFingerprintAvailableResult(true, "");

        return true;
    }

    private void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);

        } catch (NoSuchProviderException e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }

        try {
            keyStore.load(null);
            keyGenerator.init(
                    new KeyGenParameterSpec.Builder(
                            ENC_KEY_NAME,
                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                            .setUserAuthenticationRequired(true)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                            .build());
            keyGenerator.generateKey();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);

        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);

        } catch (java.security.cert.CertificateException e) {
            throw new RuntimeException(e);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean initCipher() {
        try {
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to get Cipher", e);

        } catch (NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(ENC_KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;

        } catch (KeyPermanentlyInvalidatedException e) {
            return false;

        } catch (KeyStoreException e) {
            throw new RuntimeException("Failed to init Cipher", e);

        } catch (CertificateException e) {
            throw new RuntimeException("Failed to init Cipher", e);

        } catch (UnrecoverableKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);

        } catch (IOException e) {
            throw new RuntimeException("Failed to init Cipher", e);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to init Cipher", e);

        } catch (InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    @SuppressLint("ByteOrderMark")
    static class FingerprintHelper extends FingerprintManager.AuthenticationCallback {
        private final String textToEncrypt;
        private final AuthenticateListener authenticateListener;

        private FingerprintHelper(String textToEncrypt, AuthenticateListener listener) {
            this.textToEncrypt = textToEncrypt;
            this.authenticateListener = listener;
        }

        @RequiresPermission(value = Manifest.permission.USE_FINGERPRINT)
        void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {

            manager.authenticate(cryptoObject, new CancellationSignal(), 0, this, null);
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            Log.d(TAG, "onAuthenticationSucceeded: " + "Authentication succeeded.");

            String encrypted = "";

            try {
                byte[] encryptedInfo = result
                        .getCryptoObject()
                        .getCipher()
                        .doFinal(textToEncrypt.getBytes(Charset.defaultCharset()));

                encrypted = new String(encryptedInfo);
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (authenticateListener != null)
                authenticateListener.onFingerprintAuthenticated(encrypted);
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            if (authenticateListener != null)
                authenticateListener.onFingerprintNotAuthenticated("FAILED");
        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
            if (authenticateListener != null)
                authenticateListener.onFingerprintNotAuthenticated(errString == null ? "ERROR" : errString.toString());
        }

    }

    //----------------------------------------------------------------------------------------------

    public interface AvailabilityListener {
        void onFingerprintAvailableResult(boolean available, String reason);
    }

    public interface AuthenticateListener {

        void onFingerprintAuthenticated(String encryptedResult);

        void onFingerprintNotAuthenticated(String reason);
    }
}
