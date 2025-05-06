package gmutils;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Locale;

public class StringSetTest {

    @Test
    public void getDefault() {
        Locale locale = Locale.getDefault();
        String lngCode = locale.getLanguage().toLowerCase(Locale.ENGLISH);
        int i = lngCode.indexOf("-");
        if (i < 0) i = lngCode.indexOf("_");
        if (i > 0) lngCode = lngCode.substring(0, i);
        assertEquals("en", lngCode);
    }
}