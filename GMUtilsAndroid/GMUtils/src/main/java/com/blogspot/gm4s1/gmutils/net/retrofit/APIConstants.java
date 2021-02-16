package com.blogspot.gm4s1.gmutils.net.retrofit;

import com.blogspot.gm4s1.gmutils.storage.AccountStorage;
import com.blogspot.gm4s1.gmutils.storage.SettingsStorage;

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
public final class APIConstants {

    public static final String AUTHORIZATION = "Authorization";

    public static String TOKEN() {
        try {
            return "Bearer " + AccountStorage.ACCOUNT.get_token();
        } catch (Exception e) {
            return "";
        }
    }

    //----------------------------------------------------------------------------------------------

    public static String LANG_EN_CODE = "en";
    public static String LANG_AR_CODE = "ar";

    public static String LANGUAGE() {
        if (SettingsStorage.Language.usingEnglish())
            return LANG_EN_CODE;
        else
            return LANG_AR_CODE;
    }
}