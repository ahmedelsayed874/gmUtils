package gmutils.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import gmutils.DateOp;
import gmutils.listeners.ResultCallback;
import gmutils.security.Security;

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
public class AccountStorage {

    public interface IAccount {
        String _id();

        String _token();
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

        public EncryptionUtil(@NotNull Security.EncryptDecryptInterface encryptDecrypt) {
            this.encryptDecryptInterface = encryptDecrypt;
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

    private static final String KEY_USER = "KEY_USER";
    private static final String KEY_USER_NAME = "KEY_USER_NAME";
    private static final String KEY_PASSWORD = "KEY_PASSWORD";
    private static final String KEY_DATE = "KEY_DATE";

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

    public AccountStorage(SharedPreferences sharedPreferences, Security.EncryptDecryptInterface encryptDecrypt) {
        this.sharedPreferences = sharedPreferences;
        this.encryptionUtil = new EncryptionUtil(encryptDecrypt);
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

    public AccountStorage saveAccount(IAccount account, String userName, String password) {
        return saveAccount(account, userName, password, null);
    }

    public AccountStorage saveAccount(IAccount account, String userName, String password, ResultCallback<Boolean> feedback) {
        try {
            IAccount acc = ACCOUNT;
            ACCOUNT = account;

            Gson gson = new Gson();
            String data = gson.toJson(account);

            data = tryEncrypt(data);
            userName = tryEncrypt(userName);
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
                    .putString(KEY_USER_NAME, userName)
                    .putString(KEY_PASSWORD, password)
                    .putString(KEY_DATE, date)
                    .apply();

            callListener(acc, account);

            if (feedback != null) feedback.invoke(true);

        } catch (Exception e) {
            e.printStackTrace();
            if (feedback != null) feedback.invoke(false);
        }

        return this;
    }

    //----------------------------------------------------------------------------------------------

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

    public String getUserName() {
        String un = sharedPreferences.getString(KEY_USER_NAME, "");
        if (un == null) un = "";

        un = tryDecrypt(un);

        return un;
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

    //----------------------------------------------------------------------------------------------

    public void remove() {
        sharedPreferences.edit().clear().apply();

        IAccount acc = ACCOUNT;
        ACCOUNT = null;

        callListener(acc, null);
    }

    //----------------------------------------------------------------------------------------------

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
