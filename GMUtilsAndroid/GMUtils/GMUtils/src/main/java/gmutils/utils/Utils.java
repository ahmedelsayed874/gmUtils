package gmutils.utils;


import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresPermission;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;

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
public class Utils {

    public static Utils createInstance() {
        return new Utils();
    }

    //----------------------------------------------------------------------------------------------

    public boolean isDeveloperOptionsModeEnabled(ContentResolver contentResolver) {
        int x = Settings.Secure.getInt(
                contentResolver,
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
                0
        );

        return x != 0;
    }

    public long generateRandomId() {
        Calendar refDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        refDate.set(2020, 5, 1, 0, 0, 0);
        double refSecond = refDate.getTimeInMillis();// 1000f;

        Calendar curDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        double curSecond = curDate.getTimeInMillis();// 1000f;

        long secondDiff = (long) (curSecond - refSecond);
        return secondDiff;
    }

    public String urlEncode(String url) {
        return URLEncoder.encode(url, StandardCharsets.UTF_8);
    }

    public String getSingingKeyHash(Context context, String packageName) {
        /*try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update("17:43:4C:38:E7:0B:0E:D6:8F:2E:FF:44:9A:46:F2:E8:DE:66:CC:2C".getBytes());
            String s = Base64.encodeToString(md.digest(), Base64.DEFAULT);
            Log.d("KeyHash:", s);
        } catch (Exception e) {

        }*/

        try {
            PackageInfo info = context.getPackageManager()
                    .getPackageInfo(
                            packageName,
                            PackageManager.GET_SIGNATURES
                    );

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String s = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.d("KeyHash:", s);

                return s;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }

    public boolean checkEquality(Object a, Object b) {
        return Objects.equals(a, b);
    }

    @RequiresPermission(value = Manifest.permission.VIBRATE)
    public void vibrateDevice(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(200);
        }
    }

    public boolean copyText(Context context, String text) {
        try {
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setPrimaryClip(ClipData.newPlainText(context.getPackageName(), text));
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    //----------------------------------------------------------------------------------------------

    /**
     * example:
     * if number is 5.3 and threshold = 0.1 then result is 6
     * if number is 5.3 and threshold = 0.4 then result is 5
     *
     * @param number
     * @param threshold
     * @return
     */
    public int roundNumber(double number, float threshold) {
        int integerNumber = (int) number;
        float dicimalNumber = (float) (number - integerNumber);

        if (dicimalNumber >= threshold) integerNumber++;

        return integerNumber;
    }

    public String formatToMoneyPattern(double number) {
        NumberFormat f = NumberFormat.getInstance();
        f.setGroupingUsed(true);
        f.setMaximumFractionDigits(2);
        f.setMinimumFractionDigits(2);
        return f.format(number);
    }

    //------------------------------------------------------------------------------------------

    /**
     * @return "android.resource://[package]/[res type]/[res id]"
     * alter: "android.resource://" + packageName + "/" + resId
     */
    public Uri getResourceUri(Context context, int resourceId) {
        Resources resources = context.getResources();
        Uri uri = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(resourceId))
                .appendPath(resources.getResourceTypeName(resourceId))
                .appendPath(resources.getResourceEntryName(resourceId))
                .build();

        return uri;
    }

    //------------------------------------------------------------------------------------------

    public int calculatePixelInDp(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (density * dp);
    }

    public int calculatePixelInCm(Context context, double cm) {
        /*
            dpi is the pixel density or dots per inch.
            96 dpi means there are 96 pixels per inch.
            1 inch is equal to 2.54 centimeters.

            1 inch = 2.54 cm
            dpi = 96 px / in
            96 px / 2.54 cm

            Therefore one pixel is equal to
            1 px = 2.54 cm / 96
            1 px = 0.026458333 cm
         */

        int density = context.getResources().getDisplayMetrics().densityDpi; // dots per inch and 1-inch = 2.54 centimeters
        double oneCmPx = density / 2.54;

        double px = cm * oneCmPx;
        return roundNumber(px, 0.5F);
    }

    public String convertColorRGBToHex(int color) {
        return TextHelper.createInstance().convertColorRGBToHex(color);
    }

    //------------------------------------------------------------------------------------------

    public static boolean isAppInDebugMode(Context context) {
        context = context.getApplicationContext();

        ApplicationInfo appInfo = context.getApplicationInfo();
        boolean isDebuggable = (appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;

        return isDebuggable;
    }

    //------------------------------------------------------------------------------------------

    public float getScreenBrightness(Activity activity) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        return attributes.screenBrightness;
    }

    /**
     * @param value from = -1, to = 1
     */
    public void setScreenBrightness(Activity activity, /*@FloatRange(from = -1, to = 1)*/ float value) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.screenBrightness = value;
        window.setAttributes(attributes);
    }

    public int getDeviceBrightness(Context context) {
        int brightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
        return brightness;
    }

    /**
     * must request permission in manifest
     * <uses-permission
     *       android:name="android.permission.WRITE_SETTINGS"
     *       tools:ignore="ProtectedPermissions" />
     * @param value from = 0, to = 100
     */
    @RequiresPermission("android.permission.WRITE_SETTINGS")
    public void setDeviceBrightness(Context context, /*@IntRange(from = 0, to = 100)*/ int value) {
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, value);
    }

    public void preventScreenFromRecording(Window window) {
        window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
        );
    }
}
