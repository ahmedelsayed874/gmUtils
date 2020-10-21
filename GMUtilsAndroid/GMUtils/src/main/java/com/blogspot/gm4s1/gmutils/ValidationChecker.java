package com.blogspot.gm4s1.gmutils;

import android.util.Patterns;
import android.widget.EditText;

import com.blogspot.gm4s1.gmutils.Utils;

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
public class ValidationChecker {
    private ValidationChecker() {
    }

    public static ValidationChecker createInstance() {
        return new ValidationChecker();
    }

    public Name name() {
        return new Name();
    }

    public Email email() {
        return new Email();
    }

    public Phone phone() {
        return new Phone();
    }

    public Password password() {
        return new Password();
    }

    //----------------------------------------------------------------------------------------------

    public static class Name {
        public static int MIN_LENGTH = 3;

        public boolean check(EditText text, boolean lengthOnly) {
            String name = text.getText().toString();
            name = TextHelper.createInstance().removeExtraSpaces(name);
            text.setText(name);
            return check(name, lengthOnly);
        }


        public boolean check(String name, boolean lengthOnly) {
            return check(name, lengthOnly, true);
        }

        public boolean check(String name, boolean lengthOnly, boolean removeExtraSpaces) {
            if (name == null) return false;
            if (removeExtraSpaces) name = TextHelper.createInstance().removeExtraSpaces(name);
            else name = name.trim();
            int length = name.length();

            if (lengthOnly) return name.length() >= MIN_LENGTH;

            boolean good = true;

            for (int i = 0; i < length; i++) {
                char c = name.charAt(i);

                if (!(c >= 'a' && c <= 'z')) {
                    if (!(c >= 'A' && c <= 'Z')) {
                        if (!((c == '-') || (c == ' '))) {
                            if (!(c >= 'ء' && c <= 'ي')) {
                                good = false;
                                break;
                            }
                        }
                    }
                }
            }

            return good;
        }
    }

    public static class Email {
        public boolean check(EditText text) {
            return check(text.getText().toString());
        }

        public boolean check(String email) {
            int li = email.lastIndexOf('.');
            if (li != -1) {
                int c = email.length() - li - 1;
                if (c < 2) return false;
            }
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    public static class Phone {
        public boolean check(EditText text) {
            return check(text.getText().toString());
        }

        public boolean check(String text) {
            if (text.length() < "01022663988".length()) return false;
            return Patterns.PHONE.matcher(text).matches();
        }
    }

    public static class Password {
        public static int MIN_LENGTH = 6;

        public boolean check(EditText text) {
            return check(text.getText().toString());
        }

        public boolean check(String pw) {
            return pw.length() >= MIN_LENGTH;
        }

        public boolean check(EditText text1, EditText text2) {
            return check(text1.getText().toString(), text2.getText().toString());
        }

        public boolean check(String pw1, String pw2) {
            return check(pw1) && pw1.equals(pw2);
        }
    }

}
