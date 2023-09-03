package gmutils;

import android.util.Patterns;
import android.widget.EditText;

import gmutils.utils.TextHelper;

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
        public int defaultPhoneLength;

        public Phone() {
            defaultPhoneLength = "1022663988".length();
        }

        public boolean check(EditText text) {
            return check(text.getText().toString());
        }

        public boolean check(String text) {
            if (text.length() < defaultPhoneLength) return false;
            return Patterns.PHONE.matcher(text).matches();
        }
    }

    public static class Password {
        public static int MIN_LENGTH = 6;

        public boolean check(EditText text) {
            return check(text.getText().toString());
        }

        public boolean check(String pw) {
//            while (pw.contains("  ")) {
//                pw = pw.replace("  ", " ");
//            }
            return pw.length() >= MIN_LENGTH;
        }

        public boolean check(EditText text1, EditText text2) {
            return check(text1.getText().toString(), text2.getText().toString());
        }

        public boolean check(String pw1, String pw2) {
            return check(pw1) && pw1.equals(pw2);
        }

        /**
         * password characteristics::
         * * 		At least 8 characters—the more characters, the better
         * * 		A mixture of both uppercase and lowercase letters
         * * 		A mixture of letters and numbers
         * * 		Inclusion of at least one special character, e.g., ! @ # ? ] 
         * Note: do not use < or > in your password, as both can cause problems in Web browsers
         *
         * @return 0 : 1
         */
        public float checkStrong(String pw) {
            if (pw == null) return 0;

            int smallCharCount = 0;
            int capitalCharCount = 0;
            int numberCount = 0;
            int specialCharCount = 0;

            if (pw.length() >= MIN_LENGTH) {
                String specialChars = " §±!@#$%^&*()_-+=[{]}\\|'\";:/?>.<,`~";

                for (char c : pw.toCharArray()) {
                    if (c >= 'a' && c <= 'z') {
                        smallCharCount++;
                    } else if (c >= 'A' && c <= 'Z') {
                        capitalCharCount++;
                    } else if (c >= '0' && c <= '9') {
                        numberCount++;
                    } else if (specialChars.contains(String.valueOf(c))) {
                        specialCharCount++;
                    } else {
                        if (c >= 'ء' && c <= 'ي') {
                            smallCharCount++;
                            capitalCharCount++;
                        } else if (c >= '٠' && c <= '٩') {
                            numberCount++;
                        }
                    }
                }

                final float constant = 5f;
                float strong = 1 / constant;
                if (smallCharCount > 0) strong += 1 / constant;
                if (capitalCharCount > 0) strong += 1 / constant;
                if (numberCount > 0) strong += 1 / constant;
                if (specialCharCount > 0) strong += 1 / constant;

                return strong;
            }

            return 0;
        }
    }

}
