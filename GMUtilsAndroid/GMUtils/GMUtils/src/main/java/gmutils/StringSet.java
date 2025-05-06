package gmutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import gmutils.storage.SettingsStorage;

public class StringSet {
    public static final String ENGLISH_LANG_CODE = "en";
    public static final String ARABIC_LANG_CODE = "ar";

    private final Map<String, CharSequence> strings;
    private final String defaultLangCode;

    public StringSet(CharSequence defaultString) {
        this(defaultString, null, null);
    }

    public StringSet(CharSequence english, CharSequence arabic) {
        this(english, arabic, null);
    }

    public StringSet(CharSequence english, CharSequence arabic, String defaultLangCode) {
        this(new HashMap<>(), defaultLangCode);
        this.strings.put(ENGLISH_LANG_CODE, english);
        if (arabic != null) this.strings.put(ARABIC_LANG_CODE, arabic);
    }

    public StringSet(Map<String, CharSequence> strings) {
        this(strings, null);
    }

    public StringSet(Map<String, CharSequence> strings, String defaultLangCode) {
        this.strings = strings;

        if (TextUtils.isEmpty(defaultLangCode)) {
            String lngCode;
            try {
                lngCode = SettingsStorage.Language.locale().getLanguage().toLowerCase(Locale.ENGLISH);
                int i = lngCode.indexOf("_");
                if (i < 0) i = lngCode.indexOf("-");
                if (i > 0) lngCode = lngCode.substring(0, i);
            } catch (Exception e) {
                lngCode = ENGLISH_LANG_CODE;
            }
            this.defaultLangCode = lngCode;
        } else {
            this.defaultLangCode = defaultLangCode;
        }
    }

    //----------------------------------------------------------------------------------------------

    public CharSequence getDefault() {
        return strings.get(defaultLangCode);
    }

    public CharSequence getEnglish() {
        return strings.get(ENGLISH_LANG_CODE);
    }

    public CharSequence getArabic() {
        return strings.get(ARABIC_LANG_CODE);
    }

    //----------------------------------------------------------------------------------------------

    public CharSequence get() {
        return strings.get(defaultLangCode);
    }

    public CharSequence get(String langCode) {
        return strings.get(langCode);
    }

    public List<String> getLangCodes() {
        return new ArrayList<>(strings.keySet());
    }

    //----------------------------------------------------------------------------------------------

    public void set(String langCode, CharSequence string) {
        strings.put(langCode, string);
    }

    //----------------------------------------------------------------------------------------------

    public boolean isEmpty() {
        if (!strings.isEmpty()) {
            for (String k : strings.keySet()) {
                if (!TextUtils.isEmpty(strings.get(k))) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    @Override
    public String toString() {
        String kv = "";
        for (String s : strings.keySet()) {
            if (!kv.isEmpty()) kv += "\n";
            kv += "\t" + s + ": " + strings.get(s);
        }
        return "StringSet{\n" + kv + "\n}";
    }
}

class TextUtils {
    static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }
}