package gmutils.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

public class EncryptionHelper {

    private static final String RSA_MODE = "RSA/ECB/PKCS1Padding";
    private static final String AES_MODE_M = "AES/GCM/NoPadding";

    private static final String KEY_ALIAS = "KEY";
    private static final String AndroidKeyStore = "AndroidKeyStore";
    public static final String SHARED_PREFENCE_NAME = "SAVED_TO_SHARED";
    public static final String ENCRYPTED_KEY = "ENCRYPTED_KEY";
    public static final String PUBLIC_IV = "PUBLIC_IV";


    private KeyStore keyStore;
    private static EncryptionHelper EncryptionHelper;

    public static EncryptionHelper getInstance(Context ctx) {
        if (EncryptionHelper == null) {
            try {
                EncryptionHelper = new EncryptionHelper(ctx);
            } catch (NoSuchPaddingException | NoSuchProviderException | NoSuchAlgorithmException |
                     InvalidAlgorithmParameterException |
                     KeyStoreException | CertificateException | IOException e) {
                e.printStackTrace();
            }
        }
        return EncryptionHelper;
    }

    public EncryptionHelper(Context ctx) throws NoSuchPaddingException, NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, KeyStoreException, CertificateException, IOException {
        this.generateEncryptKey(ctx);
        this.generateRandomIV(ctx);
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            try {
                this.generateAESKey(ctx);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void generateEncryptKey(Context ctx) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, KeyStoreException, CertificateException, IOException {

        keyStore = KeyStore.getInstance(AndroidKeyStore);
        keyStore.load(null);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (!keyStore.containsAlias(KEY_ALIAS)) {
                KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, AndroidKeyStore);
                keyGenerator.init(
                        new KeyGenParameterSpec.Builder(KEY_ALIAS,
                                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                                .setRandomizedEncryptionRequired(false)
                                .build());
                keyGenerator.generateKey();
            }
        } else {
            if (!keyStore.containsAlias(KEY_ALIAS)) {
                // Generate a key pair for encryption
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 30);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(ctx)
                        .setAlias(KEY_ALIAS)
                        .setSubject(new X500Principal("CN=" + KEY_ALIAS))
                        .setSerialNumber(BigInteger.TEN)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, AndroidKeyStore);
                kpg.initialize(spec);
                kpg.generateKeyPair();
            }
        }

    }

    private byte[] rsaEncrypt(byte[] secret) throws Exception {
        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(KEY_ALIAS, null);
        // Encrypt the text
        Cipher inputCipher = Cipher.getInstance(RSA_MODE, "AndroidOpenSSL");
        inputCipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.getCertificate().getPublicKey());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, inputCipher);
        cipherOutputStream.write(secret);
        cipherOutputStream.close();

        return outputStream.toByteArray();
    }

    private byte[] rsaDecrypt(byte[] encrypted) throws Exception {
        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(KEY_ALIAS, null);
        Cipher output = Cipher.getInstance(RSA_MODE, "AndroidOpenSSL");
        output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());
        CipherInputStream cipherInputStream = new CipherInputStream(
                new ByteArrayInputStream(encrypted), output);
        ArrayList<Byte> values = new ArrayList<>();
        int nextByte;
        while ((nextByte = cipherInputStream.read()) != -1) {
            values.add((byte) nextByte);
        }

        byte[] bytes = new byte[values.size()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = values.get(i).byteValue();
        }
        return bytes;
    }

    private void generateAESKey(Context context) throws Exception {
        SharedPreferences pref = context.getSharedPreferences(SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        String enryptedKeyB64 = pref.getString(ENCRYPTED_KEY, null);
        if (enryptedKeyB64 == null) {
            byte[] key = new byte[16];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(key);
            byte[] encryptedKey = rsaEncrypt(key);
            enryptedKeyB64 = Base64.encodeToString(encryptedKey, Base64.DEFAULT);
            SharedPreferences.Editor edit = pref.edit();
            edit.putString(ENCRYPTED_KEY, enryptedKeyB64);
            edit.apply();
        }
    }

    private Key getAESKeyFromKS() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, KeyStoreException, CertificateException, IOException, UnrecoverableKeyException {
        keyStore = KeyStore.getInstance(AndroidKeyStore);
        keyStore.load(null);
        SecretKey key = (SecretKey) keyStore.getKey(KEY_ALIAS, null);
        return key;
    }

    private Key getSecretKey(Context context) throws Exception {
        SharedPreferences pref = context.getSharedPreferences(SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        String enryptedKeyB64 = pref.getString(ENCRYPTED_KEY, null);

        byte[] encryptedKey = Base64.decode(enryptedKeyB64, Base64.DEFAULT);
        byte[] key = rsaDecrypt(encryptedKey);
        return new SecretKeySpec(key, "AES");
    }

    public void generateRandomIV(Context ctx) {
        SharedPreferences pref = ctx.getSharedPreferences(SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        String publicIV = pref.getString(PUBLIC_IV, null);

        if (publicIV == null) {
            SecureRandom random = new SecureRandom();
            byte[] generated = random.generateSeed(12);
            String generatedIVstr = Base64.encodeToString(generated, Base64.DEFAULT);
            SharedPreferences.Editor edit = pref.edit();
            edit.putString(PUBLIC_IV, generatedIVstr);
            edit.apply();
        }
    }

    public String encrypt(Context context, String input) throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        Cipher c;
        SharedPreferences pref = context.getSharedPreferences(SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        String publicIV = pref.getString(PUBLIC_IV, null);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            c = Cipher.getInstance(AES_MODE_M);
            try {
                c.init(Cipher.ENCRYPT_MODE, getAESKeyFromKS(), new GCMParameterSpec(128, Base64.decode(publicIV, Base64.DEFAULT)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            c = Cipher.getInstance(AES_MODE_M);
            try {
                c.init(Cipher.ENCRYPT_MODE, getSecretKey(context), new GCMParameterSpec(128, Base64.decode(publicIV, Base64.DEFAULT)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        byte[] encodedBytes = c.doFinal(input.getBytes("UTF-8"));
        return Base64.encodeToString(encodedBytes, Base64.DEFAULT);
    }

    public String decrypt(Context context, String encrypted) throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        Cipher c;
        SharedPreferences pref = context.getSharedPreferences(SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        String publicIV = pref.getString(PUBLIC_IV, null);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            c = Cipher.getInstance(AES_MODE_M);
            try {
                c.init(Cipher.DECRYPT_MODE, getAESKeyFromKS(), new GCMParameterSpec(128, Base64.decode(publicIV, Base64.DEFAULT)));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            c = Cipher.getInstance(AES_MODE_M);
            try {
                c.init(Cipher.DECRYPT_MODE, getSecretKey(context), new GCMParameterSpec(128, Base64.decode(publicIV, Base64.DEFAULT)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        byte[] decodedValue = Base64.decode(encrypted.getBytes("UTF-8"), Base64.DEFAULT);
        byte[] decryptedVal = c.doFinal(decodedValue);
        return new String(decryptedVal);
    }



}