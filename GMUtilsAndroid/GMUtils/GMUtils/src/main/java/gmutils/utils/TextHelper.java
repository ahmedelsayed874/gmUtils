package gmutils.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.TextView;

import androidx.annotation.RawRes;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

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
public class TextHelper {

    public static TextHelper createInstance() {
        return new TextHelper();
    }

    //----------------------------------------------------------------------------------------------

    public boolean isStringStartedByEnglishAlphabet(String string) {
        if (TextUtils.isEmpty(string)) return true;
        char firstChar = string.charAt(0);
        char a = 'a', z = 'z', A = 'A', Z = 'z';

        return (firstChar >= a && firstChar <= z) || (firstChar >= A && firstChar <= Z);
    }

    public String readRawResourceContent(Context context, @RawRes int rawRes) {
        InputStream inputStream = context.getResources().openRawResource(rawRes);
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        StringBuilder buffer = new StringBuilder();
        try {
            int line;

            while ((line = bis.read()) != -1) {
                buffer.append((char) line);
            }

            //buffer.replace(0, 3, "");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return buffer.toString();
    }

    public Spanned parseHtmlText(String html) {
        if (html == null) html = "";
        html = html.replace("\n", "<br />");

        Spanned spanned;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            spanned = Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT);
        } else {
            spanned = Html.fromHtml(html);
        }

        return spanned;
    }

    public void displayHtmlText(TextView tv, int text, boolean enableCopping) {
        String string = tv.getContext().getString(text);
        Spanned htmlText = parseHtmlText(string);

        tv.setText(htmlText);
        tv.setTextIsSelectable(enableCopping);
    }


    public String encodeTextByBase64(String plainText) {
        return Base64.encodeToString(plainText.getBytes(), Base64.NO_WRAP);
    }

    public String decodeTextOfBase64(String encodedText) {
        return new String(Base64.decode(encodedText, Base64.DEFAULT));
    }


    public String[] split(String text, String regex) {
        try {
            return text.split(regex);
        } catch (Exception e) {
            return new String[]{""};
        }
    }

    public float parseFloat(String floatNumber) {
        return parseFloat(floatNumber, 0);
    }

    public float parseFloat(String floatNumber, float defValue) {
        try {
            if (floatNumber != null) {
                while (floatNumber.contains(" ")) {
                    floatNumber = floatNumber.replace(" ", "");
                }
                floatNumber = floatNumber.replace(",", "");
            }

            return Float.parseFloat(floatNumber);
        } catch (Exception e) {
            return defValue;
        }
    }

    public String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {//b= 0110 1100
            int halfbyte = (b >>> 4) & 0x0F;//hb=0000 0110
            char c;

            for (int r = 0; r < 2; r++) {
                if ((0 <= halfbyte) && (halfbyte <= 9)) //b=[0:9]
                    c = (char) ('0' + halfbyte);
                else                                    //b=[a,b,c,d,e,f]
                    c = (char) ('A' + (halfbyte - 10));

                buf.append(c);
                halfbyte = b & 0x0F;//hb= 0000 1100
            }

            buf.append('-');
        }

        buf.replace(buf.length() - 1, buf.length(), "");

        return buf.toString();
    }

    public String convertToHex(int number) {
        return "#" + String.format("%X", number);
    }

    public String convertToHex(int number, int minLength) {
        return "#" + String.format("%0" + minLength + "X", number);
    }

    public String convertColorRGBToHex(int color) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        String finalHex = "";

        for (int v : Arrays.asList(a, r, g, b)) {
            String x = convertToHex(v).replace("#", "");
            if (x.length() < 2) x = "0" + x;
            else if (x.length() > 2) x = x.substring(0, 2);

            finalHex += x;
        }

        return "#" + finalHex;
    }

    public String convertArabicNumberToEnglish(String text) {
        if (TextUtils.isEmpty(text)) return "";

        return text
                .replaceAll("٠", "0")
                .replaceAll("١", "1")
                .replaceAll("٢", "2")
                .replaceAll("٣", "3")
                .replaceAll("٤", "4")
                .replaceAll("٥", "5")
                .replaceAll("٦", "6")
                .replaceAll("٧", "7")
                .replaceAll("٨", "8")
                .replaceAll("٩", "9");
    }

    public String convertEnglishNumberToArabic(String text) {
        if (TextUtils.isEmpty(text)) return "";

        return text
                .replaceAll("0", "٠")
                .replaceAll("1", "١")
                .replaceAll("2", "٢")
                .replaceAll("3", "٣")
                .replaceAll("4", "٤")
                .replaceAll("5", "٥")
                .replaceAll("6", "٦")
                .replaceAll("7", "٧")
                .replaceAll("8", "٨")
                .replaceAll("9", "٩");
    }


    public String removeExtraSpaces(String text) {
        if (text != null) {
            text = text.trim();
            while (text.contains("  ")) {
                text = text.replace("  ", " ");
            }
            return text;
        } else {
            return "";
        }
    }

    //----------------------------------------------------------------------------------------------

    public String capitalizeVariableName(String text) {
        StringBuilder newText = new StringBuilder();
        String part = "";

        for (char it : text.toCharArray()) {
            if (part.isEmpty()) {
                if (it >= 'a' && it <= 'z') {
                    part += ("" + it).toUpperCase();

                } else if (it == '_' || it == '.' || it == ' ') {
                    part += "";

                } else {
                    part += it;
                }
            } else {
                if (it >= '0' && it <= '9') {
                    try {
                        Integer.parseInt(part);
                        part += it;
                    } catch (Exception e) {
                        if (part.isEmpty()) {
                            part = "" + it;
                        } else {
                            newText.append(part).append(" ");
                            part = "" + it;
                        }
                    }

                } else {
                    char it2 = it;

                    try {
                        Integer.parseInt(part);
                        newText.append(part).append(" ");
                        it2 = ("" + it2).toUpperCase().charAt(0);
                        part = "";
                    } catch (Exception e) {
                    }

                    if (it2 >= 'a' && it2 <= 'z') {
                        part += it2;

                    } else if (it2 == '_' || it2 == '.' || it2 == ' ') {
                        if (part.length() > 0) newText.append(part).append(" ");
                        part = "";

                    } else if (it2 >= 'A' && it2 <= 'Z') {
                        if (part.length() > 0) newText.append(part).append(" ");
                        part = "" + it2;

                    } else {
                        part += it2;
                    }
                }
            }
        }

        if (part.length() > 0) {
            newText.append(part).append(" ");
        }

        if (newText.length() > 0) {
            newText = new StringBuilder(newText.substring(0, newText.length() - 1));
        }

        return newText.toString();
    }

}
