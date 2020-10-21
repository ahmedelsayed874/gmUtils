package com.blogspot.gm4s1.gmutils.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.blogspot.gm4s1.gmutils.DateOp;
import com.blogspot.gm4s1.gmutils.Security;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

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
public class AccountPreferences {

    public interface IAccount {
        String getphoto();
        String getfirstname();
        String getlastname();
        String getemail();
        String getphonenumber();
        String getBearerToken();
    }

    private static class EncryptionUtil {
        private Security.EncryptDecryptInterface encryptDecryptInterface = null;

        EncryptionUtil(Context context) {
            try {
                //encryptDecryptInterface = Security.getInstance(context)
                encryptDecryptInterface = Security.getSimpleInstance(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String encrypt(String data) {
            if (encryptDecryptInterface != null) {
                try {
                    data = encryptDecryptInterface.encrypt(data);
                } catch (Exception e) {
                }
            }

            return data;
        }

        public String decrypt(String decrypted) {
            if (encryptDecryptInterface != null) {
                try {
                    decrypted = encryptDecryptInterface.decrypt(decrypted);
                } catch (Exception e) {
                }
            }

            return decrypted;
        }

    }

    public interface Listener {
        void onUserIAccountChanged(IAccount account);
    }

    //----------------------------------------------------------------------------------------------

    private static String PREF_NAME = AccountPreferences.class.getName();
    private static String KEY_USER = "KEY_USER";
    private static String KEY_PASSWORD = "KEY_PASSWORD";
    private static String KEY_DATE = "KEY_DATE";

    public static IAccount ACCOUNT;


    //------------------------------------------------------------------------------------------------------------------

    public static AccountPreferences getInstance() {
        Context appContext = PreferencesManager.getAppContext();
        return new AccountPreferences(appContext);
    }

    //------------------------------------------------------------------------------------------------------------------

    private static Map<Integer, Listener> sListener = null;

    public static void addListener(int listenerId, Listener listener) {
        if (sListener == null) {
            sListener = new HashMap<>();
        }
        sListener.put(listenerId, listener);
    }

    public static boolean removeListener(int listenerId) {
        if (sListener != null) {
            Listener listener = sListener.remove(listenerId);
            if (listener != null) {
                if (sListener.isEmpty()) {
                    sListener = null;
                }
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    public static void removeAllListener() {
        if (sListener != null) {
            for (int cls : sListener.keySet()) {
                Listener listener = sListener.remove(cls);
                if (listener != null) {
                    if (sListener.isEmpty()) {
                        sListener = null;
                    }
                }
            }
        }
    }

    //----------------------------------------------------------------------------------------------

    private SharedPreferences sharedPreferences;
    private EncryptionUtil encryptionUtil;

    private AccountPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        encryptionUtil = new EncryptionUtil(context.getApplicationContext());
    }

    private String tryEncrypt(String data) {
        data = encryptionUtil.encrypt(data);
        return data;
    }

    private String tryDecrypt(String decrypted) {
        decrypted = encryptionUtil.decrypt(decrypted);
        return decrypted;
    }

    //----------------------------------------------------------------------------------------------

    public boolean saveUser(IAccount user, String password) {
        try {
            ACCOUNT = user;

            Gson gson = new Gson();
            String data = gson.toJson(user);

            data = tryEncrypt(data);
            password = tryEncrypt(password);
            String date = tryEncrypt(
                    DateOp.getInstance().formatDate(
                            DateOp.PATTERN_dd_MM_yyyy_HH_mm_ss,
                            false
                    )
            );

            sharedPreferences
                    .edit()
                    .putString(KEY_USER, data)
                    .putString(KEY_PASSWORD, password)
                    .putString(KEY_DATE, date)
                    .apply();

            callListener(user);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void savePassword(String password) {
        password = tryEncrypt(password);

        sharedPreferences
                .edit()
                .putString(KEY_PASSWORD, password)
                .apply();
    }

    public <T extends IAccount> T getAccount(Class<T> accountClass) {
        if (ACCOUNT == null) {
            try {
                String data = sharedPreferences.getString(KEY_USER, "");

                data = tryDecrypt(data);

                ACCOUNT = new Gson().fromJson(data, accountClass);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        try {
            return (T) ACCOUNT;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getPassword() {
        String password = sharedPreferences.getString(KEY_PASSWORD, "");
        if (password == null) password = "";

        password = tryDecrypt(password);

        return password;
    }

    public String getSavedDate() {
        String date = sharedPreferences.getString(KEY_DATE, "");

        if (date != null) date = tryDecrypt(date);

        return date;
    }

    public void logOut() {
        sharedPreferences.edit().clear().apply();
        ACCOUNT = null;

        callListener(null);
    }

    private void callListener(IAccount account) {
        if (sListener != null) {
            for (int cls : sListener.keySet()) {
                Listener listener = sListener.get(cls);
                if (listener != null)
                    listener.onUserIAccountChanged(account);
            }
        }
    }

}
