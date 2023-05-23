package gmutils.security;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CryptographicUtilTest {

    @Test
    void encDec() {
        CryptographicUtil crpto = new CryptographicUtil("1234567890123456");
        String ciper = crpto.encrypt("ahmed");
        String plain = crpto.decrypt(ciper);
        assertEquals("test", "ahmed", plain);
    }
}
