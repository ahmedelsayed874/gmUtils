package com.blogspot.gm4s1.gmutils.utils;


import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RatingBar;

import androidx.annotation.RequiresPermission;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer with (Java/Kotlin, Swift)
 * Have experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public class Utils {

    public static Utils createInstance() {
        return new Utils();
    }

    //----------------------------------------------------------------------------------------------

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
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("URLEncoder.encode() failed for " + url);
        }
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
        return (a == b) || (a != null && a.equals(b));
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

    public String colorHex(int color) {
        return "#FF" + String.format("%06X", 0xFFFFFF & color);
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

    public String convertIntegerToHex(int number) {
        return String.format("%08X", number);
    }

    //------------------------------------------------------------------------------------------

    /**
     * @return "android.resource://[package]/[res type]/[res id]"
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
}
