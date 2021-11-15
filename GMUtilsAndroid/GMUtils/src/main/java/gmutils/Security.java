package gmutils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

import gmutils.utils.TextHelper;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer (Java/Kotlin, Swift) also Flutter (Dart)
 * Have precedent experience with:
 *      - (C/C++, C#) languages
 *      - .NET environment
 *      - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public class Security {

    public interface EncryptDecryptInterface {
        String encrypt(String input);

        String decrypt(String encrypted);
    }

    //----------------------------------------------------------------------------------------------

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static EncryptDecryptInterface getInstance(Context context) throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return new EncDecAndroidM();
        }

        return new EncDecPreAndroidM(context);

    }

    public static EncryptDecryptInterface getSimpleInstance(Context context) {
        return new SimpleEncDec(context);
    }

    public static EncryptDecryptInterface getSimpleInstance(int key) {
        return new SimpleEncDec(key);
    }

    //----------------------------------------------------------------------------------------------

    private static class EncDecAndroidM implements EncryptDecryptInterface {
        private static final String AndroidKeyStore = "AndroidKeyStore";
        private static final String AES_MODE = "AES/GCM/NoPadding";
        private static final String KEY_ALIAS = "!@#$%^&*()_+";

        private KeyStore keyStore;

        @RequiresApi(api = Build.VERSION_CODES.M)
        EncDecAndroidM()
                throws CertificateException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException,
                InvalidAlgorithmParameterException, IOException {
            generatingKey();
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        private void generatingKey()
                throws KeyStoreException, CertificateException, NoSuchAlgorithmException,
                IOException, InvalidAlgorithmParameterException, NoSuchProviderException {

            keyStore = KeyStore.getInstance(AndroidKeyStore);
            keyStore.load(null);

            if (!keyStore.containsAlias(KEY_ALIAS)) {
                KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec
                        .Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setRandomizedEncryptionRequired(false)
                        .build();

                KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, AndroidKeyStore);
                keyGenerator.init(keyGenParameterSpec);
                keyGenerator.generateKey();
            }
        }

        private Key getSecretKey() throws Exception {
            Key key = keyStore.getKey(KEY_ALIAS, null);
            return key;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public String encrypt(String input) {
            try {
                byte[] inputBytes = input.getBytes(StandardCharsets.US_ASCII);

                byte[] encodedBytes = doCipher(Cipher.ENCRYPT_MODE, inputBytes);
                String encryptedBase64Encoded = Base64.encodeToString(encodedBytes, Base64.DEFAULT);
                return encryptedBase64Encoded;
            } catch (Exception e) {
                Logger.print(e);
                return input;
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public String decrypt(String encrypted) {
            try {
                byte[] encryptedBytes = encrypted.getBytes(StandardCharsets.US_ASCII);

                byte[] decodedBytes = doCipher(Cipher.DECRYPT_MODE, encryptedBytes);
                String decodedText = new String(decodedBytes);
                return decodedText;
            } catch (Exception e) {
                Logger.print(e);
                return encrypted;
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        private byte[] doCipher(int mode, byte[] input) throws Exception {
            byte[] iv = "!@$)9*&$$(#&%%($#@KR(RFK(%".getBytes();
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv, 0, 12);

            Cipher c = Cipher.getInstance(AES_MODE);
            c.init(mode, getSecretKey(), gcmParameterSpec);
            byte[] bytes = c.doFinal(input);

            return bytes;
        }

    }

    private static class EncDecPreAndroidM implements EncryptDecryptInterface {

        private static final String AndroidKeyStore = "AndroidKeyStore";
        private static final String RSA_MODE = "RSA/ECB/PKCS1Padding";
        private static final String AES_MODE = "AES/ECB/PKCS7Padding";
        private static final String KEY_ALIAS = "pwk";

        private static final String SHARED_PREFENCE_NAME = "enc-dec-pref";
        private static final String ENCRYPTED_KEY = "enc-dec-key";

        private KeyStore keyStore;
        private final SharedPreferences pref;

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        EncDecPreAndroidM(Context context) throws Exception {
            pref = context.getSharedPreferences(SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
            generatingKey(context);
            generateAndStoreTheAESKey();
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        private void generatingKey(Context context)
                throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException,
                NoSuchProviderException, InvalidAlgorithmParameterException {
            keyStore = KeyStore.getInstance(AndroidKeyStore);
            keyStore.load(null);

            // Generate the RSA key pairs
            if (!keyStore.containsAlias(KEY_ALIAS)) {
                // Generate a key pair for encryption
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 30);

                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                        .setAlias(KEY_ALIAS)
                        .setSubject(new X500Principal("CN=" + KEY_ALIAS))
                        .setSerialNumber(BigInteger.TEN)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();

                KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", AndroidKeyStore);/*KeyProperties.KEY_ALGORITHM_RSA*/
                kpg.initialize(spec);
                kpg.generateKeyPair();

            }
        }

        private void generateAndStoreTheAESKey() throws Exception {
            String enryptedKeyB64 = pref.getString(ENCRYPTED_KEY, null);

            if (enryptedKeyB64 == null) {
                byte[] key = new byte[16];
                new SecureRandom().nextBytes(key);

                enryptedKeyB64 = rsaEncrypt(key);

                pref.edit()
                        .putString(ENCRYPTED_KEY, enryptedKeyB64)
                        .apply();
            }
        }

        /**
         * RSA Encryption Routines
         */
        private String rsaEncrypt(byte[] secret) throws Exception {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(KEY_ALIAS, null);
            PrivateKey privateKey = privateKeyEntry.getPrivateKey();
            PublicKey publicKey = privateKeyEntry.getCertificate().getPublicKey();

            Cipher inputCipher = Cipher.getInstance(RSA_MODE, "AndroidOpenSSL");
            inputCipher.init(Cipher.ENCRYPT_MODE, publicKey);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, inputCipher);
            cipherOutputStream.write(secret);
            cipherOutputStream.flush();
            cipherOutputStream.close();

            byte[] encryptedKey = outputStream.toByteArray();
            String enryptedKeyB64 = Base64.encodeToString(encryptedKey, Base64.DEFAULT);
            return enryptedKeyB64;
        }

        /**
         * RSA Decryption Routines
         */
        private byte[] rsaDecrypt(byte[] encrypted) throws Exception {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(KEY_ALIAS, null);
            PrivateKey privateKey = privateKeyEntry.getPrivateKey();
            PublicKey publicKey = privateKeyEntry.getCertificate().getPublicKey();

            Cipher output = Cipher.getInstance(RSA_MODE, "AndroidOpenSSL");
            output.init(Cipher.DECRYPT_MODE, privateKey);

            ByteArrayInputStream inputStream = new ByteArrayInputStream(encrypted);

            CipherInputStream cipherInputStream = new CipherInputStream(inputStream, output);
            ArrayList<Byte> values = new ArrayList<>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte) nextByte);
            }

            byte[] bytes = new byte[values.size()];
            for (int i = 0; i < bytes.length; i++) bytes[i] = values.get(i);
            return bytes;
        }


        private Key getSecretKey() throws Exception {
            String enryptedKeyB64 = pref.getString(ENCRYPTED_KEY, null);

            // need to check null, omitted here
            byte[] encryptedKey = Base64.decode(enryptedKeyB64, Base64.DEFAULT);
            byte[] key = rsaDecrypt(encryptedKey);
            return new SecretKeySpec(key, "AES");
        }


        @Override
        public String encrypt(String input) {
            try {
                byte[] inputBytes = input.getBytes();

                byte[] encodedBytes = doCipher(Cipher.ENCRYPT_MODE, inputBytes);
                String encryptedBase64Encoded = Base64.encodeToString(encodedBytes, Base64.DEFAULT);
                return encryptedBase64Encoded;
            } catch (Exception e) {
                Logger.print(e);
                return input;
            }
        }

        @Override
        public String decrypt(String encrypted) {
            try {
                byte[] encryptedBytes = encrypted.getBytes();

                byte[] decodedBytes = doCipher(Cipher.DECRYPT_MODE, encryptedBytes);
                return new String(decodedBytes);
            } catch (Exception e) {
                Logger.print(e);
                return encrypted;
            }
        }

        private byte[] doCipher(int mode, byte[] input) throws Exception {
            Cipher c = Cipher.getInstance(AES_MODE, "BC");
            c.init(mode, getSecretKey());
            byte[] bytes = c.doFinal(input);
            return bytes;
        }

    }

    private static class SimpleEncDec implements EncryptDecryptInterface {
        /**
         * val instance = Security.getSimpleInstance()
         * val plainData = "ABC"
         * val encrypt = instance.encrypt(this, plainData)
         * Logger.print(plainData + "encrypted to:\n" + encrypt)
         * val decrypt = instance.decrypt(this, encrypt)
         * <p>
         * if (plainData == decrypt) {
         * Logger.print("EncDec: Succeeded")
         * } else {
         * Logger.print("EncDec: Failed")
         * }
         */
        private final String karlst = "§1234567890-=][poiuytrewqasdfghjkl;'\\`zxcvbnm,...../±!@@@@#$%^&*()_+QWERTYUIOP{}ASDFGHJKL:\"|~ZXCVBNM<>?";
        private final int key;
        private final int factor1 = 9;
        private final int factor2 = 17;

        public SimpleEncDec(Context context) {
            key = context.getPackageName().hashCode();
        }

        public SimpleEncDec(int key) {
            this.key = key;
        }

        @Override
        public String encrypt(String input) {
            final int dataLength = input.length();
            char[] chars = new char[dataLength * 2];
            char c;
            char[] encChar;

            for (int i = 0; i < dataLength; i++) {
                c = input.charAt(i);
                encChar = encChar(c);

                chars[i * 2] = encChar[0];
                chars[i * 2 + 1] = encChar[1];
            }

            input = new String(chars);
            input = stuffData(input);

            return input;
        }

        @Override
        public String decrypt(String encrypted) {
            encrypted = pickupEncryptedData(encrypted);
            char[] chars = new char[encrypted.length() / 2];

            for (int i = 0; i < chars.length; i++) {
                char c1 = encrypted.charAt(i * 2);
                char c2 = encrypted.charAt(i * 2 + 1);

                chars[i] = decChar(c1, c2);
            }

            String decrypted = new String(chars);

            return decrypted;
        }


        //------------------------------------------------------------------------------------------

        private char[] encChar(char c) {
            int cInt = c;
            int xorRes = cInt ^ key;
            char encChar = (char) xorRes;

            char[] chars = divideChar(encChar);
            return chars;
        }

        private char[] divideChar(char ch) {
            char[] chars = new char[2];
            chars[0] = (char) (ch & 0x00ff);
            chars[1] = (char) ((ch >> 8) & 0x00ff);
            return chars;
        }

        private char decChar(char c1, char c2) {
            char c = (char) (c1 | (c2 << 8));

            int cInt = c;
            int xorRes = cInt ^ key;
            char originChar = (char) xorRes;

            return originChar;
        }

        private String stuffData(String data) {
            StringBuilder result = new StringBuilder();
            int length1 = data.length() * factor1;
            int length2 = data.length() * factor2;
            int length3 = ("" + key).length();
            Random r = new Random(0);

            for (int i = 0; i < length1; i++) {
                appendRandomChar(result, r);
            }

            result.append(data);

            for (int i = 0; i < length2 + length3; i++) {
                appendRandomChar(result, r);
            }

            return result.toString();
        }

        private void appendRandomChar(StringBuilder result, Random r) {
            int rn = r.nextInt(karlst.length() - 1);
            char s = karlst.charAt(rn);
            char[] encS = encChar(s);
            /*char[] encS;
            try {
                if (karlst.length() % rn == 1){
                    encS = encChar(s);
                } else {
                    encS = divideChar(s);
                }
            } catch (Exception e) {
                encS = encChar(s);
            }*/
            result.append(encS[0]);
            result.append(encS[1]);
        }

        private String pickupEncryptedData(String encrypted) {
            int length = ("" + key).length();
            encrypted = encrypted.substring(0, encrypted.length() - length);

            int dataLength = encrypted.length() / (((factor1 + factor2) * 2) + 1);
            int firstIndex = dataLength * factor1 * 2;
            int lastIndex = firstIndex + dataLength;

            encrypted = encrypted.substring(firstIndex, lastIndex);
            return encrypted;
        }
    }

    //----------------------------------------------------------------------------------------------

    public static String hashText(String plainText) {
        return hashText(plainText, "UTF-16LE");
    }

    public static String hashText(String plainText, String charset) {

        try {
            byte[] textBytes = plainText.getBytes(charset);

            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(textBytes, 0, textBytes.length);
            byte[] sha1hash = md.digest();

            return TextHelper.createInstance().convertToHex(sha1hash);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "";
    }


}
