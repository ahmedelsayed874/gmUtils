package com.blogspot.gm4s1.gmutils.net.retrofit.zcore;

import com.blogspot.gm4s1.gmutils.DateOp;
import com.blogspot.gm4s1.gmutils.preferences.AccountPreferences;
import com.blogspot.gm4s1.gmutils.preferences.SettingsPreferences;

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
public class APIsContract {
    public static boolean inTestMode() {
        return System.currentTimeMillis() <= TEST_DEADLINE.getTimeInMillis();
    }

    public static DateOp TEST_DEADLINE = DateOp.getInstance("10-09-2020 00:00:00", DateOp.PATTERN_dd_MM_yyyy_HH_mm_ss);

    public static String testDomainURL = "";
    public static String productionDomainURL = "";
    public static String apiPath = "";

    public static final String AUTHORIZATION = "Authorization";
    public static String LANG_EN_CODE = "en";
    public static String LANG_AR_CODE = "ar";

    public static String domainURL() {
        if (inTestMode())
            return testDomainURL;
        else
            return productionDomainURL;
    }

    public static String baseURL() {
        return domainURL() + apiPath;
    }

    public static String TOKEN() {
        try {
            return "Bearer " + AccountPreferences.ACCOUNT.getBearerToken();
        } catch (Exception e){
            return "";
        }
    }

    public static String LANGUAGE() {
        if (SettingsPreferences.Language.usingEnglish())
            return LANG_EN_CODE;
        else
            return LANG_AR_CODE;
    }
}