package gmutils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;

import java.util.Locale;

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
public class ContextWrapper extends android.content.ContextWrapper {
    public ContextWrapper(Context base) {
        super(base);
    }

    public static ContextWrapper wrap(Context context, String language) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return changeLocale(context, language);
        else
            return changeLocaleLegacy(context, language);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static ContextWrapper changeLocale(Context context, String language) {
        if (language == null) return new ContextWrapper(context);

        Configuration config = context.getResources().getConfiguration();
//        Locale sysLocale = config.getLocales().get(0);

//        boolean isSameLanguage = sysLocale.getLanguage().toLowerCase().contains(language.toLowerCase());
//        if (!isSameLanguage) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        config.setLocale(locale);
        config.setLayoutDirection(locale);
//        }

        context = context.createConfigurationContext(config);

        return new ContextWrapper(context);
    }

    @SuppressWarnings("deprecation")
    public static ContextWrapper changeLocaleLegacy(Context context, String language) {
        if (language == null) return new ContextWrapper(context);

        Configuration config = context.getResources().getConfiguration();
//        Locale sysLocale = config.locale;

//        boolean isSameLanguage = sysLocale.getLanguage().toLowerCase().contains(language.toLowerCase());
//        if (!isSameLanguage) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        config.locale = locale;
        config.setLayoutDirection(locale);
//        }

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        context.getResources().updateConfiguration(config, displayMetrics);

        return new ContextWrapper(context);
    }


}