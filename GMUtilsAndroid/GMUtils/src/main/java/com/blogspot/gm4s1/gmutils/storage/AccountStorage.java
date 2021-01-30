package com.blogspot.gm4s1.gmutils.storage;

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
public class AccountStorage {

    public interface IAccount {
        String get_id();
        String get_identifier(); //user name | email | phone number
        default String get_full_name() {
            return get_first_name() + " " + get_last_name();
        }
        String get_first_name();
        String get_last_name();
        String get_email();
        String get_mobile_number();
        String get_photo();
        String get_token();
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
        void onUserIAccountChanged(IAccount oldAccount, IAccount newAccount);
    }

    //----------------------------------------------------------------------------------------------

    private static String KEY_USER = "KEY_USER";
    private static String KEY_PASSWORD = "KEY_PASSWORD";
    private static String KEY_DATE = "KEY_DATE";

    public static IAccount ACCOUNT;


    //------------------------------------------------------------------------------------------------------------------

    public static AccountStorage getInstance() {
        Context appContext = StorageManager.getAppContext();
        return new AccountStorage(appContext);
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

    private final SharedPreferences sharedPreferences;
    private final EncryptionUtil encryptionUtil;

    private AccountStorage(Context context) {
        String prefName = context.getPackageName() + "_" + getClass().getSimpleName().toUpperCase();
        sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
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

    public boolean saveAccount(IAccount account, String password) {
        try {
            IAccount acc = ACCOUNT;
            ACCOUNT = account;

            Gson gson = new Gson();
            String data = gson.toJson(account);

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

            callListener(acc, account);

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

        IAccount acc = ACCOUNT;
        ACCOUNT = null;

        callListener(acc, null);
    }

    private void callListener(IAccount oldAccount, IAccount newAccount) {
        if (sListener != null) {
            for (int cls : sListener.keySet()) {
                Listener listener = sListener.get(cls);
                if (listener != null)
                    listener.onUserIAccountChanged(oldAccount, newAccount);
            }
        }
    }

}
