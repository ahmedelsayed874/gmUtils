package gmutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringSet {
    public static final String ENGLISH_LANG_CODE = "en";
    public static final String ARABIC_LANG_CODE = "ar";

    Map<String, String> strings;

    public StringSet(String defaultString) {
        this(defaultString, null);
    }

    public StringSet(String english, String arabic) {
        this(new HashMap<>());
        this.strings.put(ENGLISH_LANG_CODE, english);
        if (arabic != null) this.strings.put(ARABIC_LANG_CODE, arabic);
    }

    public StringSet(Map<String, String> others) {
        this.strings = others;
    }

    //

    public String getDefault() {
        return strings.get(ENGLISH_LANG_CODE);
    }

    public String getEnglish() {
        return strings.get(ENGLISH_LANG_CODE);
    }

    public String getArabic() {
        return strings.get(ARABIC_LANG_CODE);
    }

    public String get(String langCode) {
        return strings.get(langCode);
    }

    public List<String> getLangCodes() {
        return new ArrayList<>(strings.keySet());
    }

    //

    public void set(String langCode, String string) {
        strings.put(langCode, string);
    }
}
