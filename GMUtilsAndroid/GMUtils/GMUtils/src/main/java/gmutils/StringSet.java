package gmutils;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringSet {
    public static final String ENGLISH_LANG_CODE = "en";
    public static final String ARABIC_LANG_CODE = "ar";

    private final Map<String, CharSequence> strings;

    public StringSet(CharSequence defaultString) {
        this(defaultString, null);
    }

    public StringSet(CharSequence english, CharSequence arabic) {
        this(new HashMap<>());
        this.strings.put(ENGLISH_LANG_CODE, english);
        if (arabic != null) this.strings.put(ARABIC_LANG_CODE, arabic);
    }

    public StringSet(Map<String, CharSequence> others) {
        this.strings = others;
    }

    //----------------------------------------------------------------------------------------------

    public CharSequence getDefault() {
        return strings.get(ENGLISH_LANG_CODE);
    }

    public CharSequence getEnglish() {
        return strings.get(ENGLISH_LANG_CODE);
    }

    public CharSequence getArabic() {
        return strings.get(ARABIC_LANG_CODE);
    }

    //----------------------------------------------------------------------------------------------

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
        if (strings.size() > 0) {
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
