package gmutils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import gmutils.listeners.ResultCallback;
import gmutils.storage.SettingsStorage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;


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
public class CountryPhoneCodes {
    private static CountryPhoneCodes mInstance;
    private final ArrayList<CountryCode> mCountryCodes = new ArrayList<>();

    public static CountryPhoneCodes getInstance() {
        if (mInstance == null) mInstance = new CountryPhoneCodes();
        return mInstance;
    }

    public CountryPhoneCodes() {
        mCountryCodes.add(new CountryCode("Afghanistan", "", 93, "AF"));
        mCountryCodes.add(new CountryCode("Albania", "", 355, "AL"));
        mCountryCodes.add(new CountryCode("Algeria", "", 213, "DZ"));
        mCountryCodes.add(new CountryCode("Aland Islands", "", 358, "AX"));
        mCountryCodes.add(new CountryCode("American Samoa", "", 1, "AS"));
        mCountryCodes.add(new CountryCode("Anguilla", "", 1, "AI"));
        mCountryCodes.add(new CountryCode("Andorra", "", 376, "AD"));
        mCountryCodes.add(new CountryCode("Angola", "", 244, "AO"));
        mCountryCodes.add(new CountryCode("Antarctica", "", 672, "AQ"));
        mCountryCodes.add(new CountryCode("Antigua and barbuda", "", 1268, "AG"));
        mCountryCodes.add(new CountryCode("Argentina", "", 54, "AR"));
        mCountryCodes.add(new CountryCode("Armenia", "", 374, "AM"));
        //mCountryCodes.add(new CountryCode("Aruba", "", 297, "AA"));
        mCountryCodes.add(new CountryCode("Aruba", "", 297, "AW"));
        mCountryCodes.add(new CountryCode("Australia", "", 61, "AU"));
        mCountryCodes.add(new CountryCode("Austria", "", 43, "AT"));
        mCountryCodes.add(new CountryCode("Azerbaijan", "", 994, "AZ"));

        mCountryCodes.add(new CountryCode("Bahrain", "", 973, "BH"));
        mCountryCodes.add(new CountryCode("Bangladesh", "", 880, "BD"));
        mCountryCodes.add(new CountryCode("Barbados", "", 1, "BB"));
        mCountryCodes.add(new CountryCode("Belarus", "", 375, "BY"));
        mCountryCodes.add(new CountryCode("Belgium", "", 32, "BE"));
        mCountryCodes.add(new CountryCode("Belize", "", 501, "BZ"));
        mCountryCodes.add(new CountryCode("Benin", "", 229, "BJ"));
        mCountryCodes.add(new CountryCode("Bermuda", "", 1, "BM"));
        mCountryCodes.add(new CountryCode("Bhutan", "", 975, "BT"));
        mCountryCodes.add(new CountryCode("Bolivia", "", 591, "BO"));
        mCountryCodes.add(new CountryCode("Bonaire, Saint Eustatius and Saba", "", 599, "BQ"));
        mCountryCodes.add(new CountryCode("Bosnia and herzegovina", "", 387, "BA"));
        mCountryCodes.add(new CountryCode("Botswana", "", 267, "BW"));
        mCountryCodes.add(new CountryCode("Bouvet Island", "", 599, "BV"));
        mCountryCodes.add(new CountryCode("Brazil", "", 55, "BR"));
        mCountryCodes.add(new CountryCode("British Indian Ocean Territory", "", 246, "IO"));
        mCountryCodes.add(new CountryCode("Brunei", "", 673, "BN"));
        mCountryCodes.add(new CountryCode("Bulgaria", "", 359, "BG"));
        mCountryCodes.add(new CountryCode("Burkina faso", "", 226, "BF"));
        mCountryCodes.add(new CountryCode("Burma/Myanmar", "", 95, "MM"));
        mCountryCodes.add(new CountryCode("Burundi", "", 257, "BI"));

        mCountryCodes.add(new CountryCode("Cambodia", "", 855, "KH"));
        mCountryCodes.add(new CountryCode("Cameroon", "", 237, "CM"));
        mCountryCodes.add(new CountryCode("Canada", "", 1, "CA"));
        mCountryCodes.add(new CountryCode("Cape verde", "", 238, "CV"));
        mCountryCodes.add(new CountryCode("Cayman Islands", "", 1, "KY"));
        mCountryCodes.add(new CountryCode("Central african republic", "", 236, "CF"));
        mCountryCodes.add(new CountryCode("Chad", "", 235, "TD"));
        mCountryCodes.add(new CountryCode("Chile", "", 56, "CL"));
        mCountryCodes.add(new CountryCode("China", "", 86, "CN"));
        mCountryCodes.add(new CountryCode("Christmas Island", "", 61, "CX"));
        mCountryCodes.add(new CountryCode("Cocos (Keeling) Islands", "", 891, "CC"));
        mCountryCodes.add(new CountryCode("Colombia", "", 855, "CO"));
        mCountryCodes.add(new CountryCode("Comores", "", 269, "KM"));
        mCountryCodes.add(new CountryCode("Congo", "", 242, "CG"));
        mCountryCodes.add(new CountryCode("Congo zaire", "", 243, "CD"));
        mCountryCodes.add(new CountryCode("Cook Islands", "", 682, "CK"));
        mCountryCodes.add(new CountryCode("Costa rica", "", 506, "CR"));
        mCountryCodes.add(new CountryCode("Cote d ivoire", "", 225, "CI"));
        mCountryCodes.add(new CountryCode("Croatia", "", 385, "HR"));
        mCountryCodes.add(new CountryCode("Cuba", "", 53, "CU"));
        mCountryCodes.add(new CountryCode("CuraÃ§ao", "", 599, "CW"));
        mCountryCodes.add(new CountryCode("Cyprus", "", 357, "CY"));
        mCountryCodes.add(new CountryCode("Czech republic", "", 420, "CZ"));

        mCountryCodes.add(new CountryCode("Denmark", "", 45, "DK"));
        mCountryCodes.add(new CountryCode("Djibouti", "", 253, "DJ"));
        mCountryCodes.add(new CountryCode("Dominica", "", 1767, "DM"));
        mCountryCodes.add(new CountryCode("Dominican republic", "", 1, "DO"));

        mCountryCodes.add(new CountryCode("East Timor", "", 670, "TL"));
        mCountryCodes.add(new CountryCode("Ecuador", "", 593, "EC"));
        mCountryCodes.add(new CountryCode("Egypt", "", 20, "EG"));
        mCountryCodes.add(new CountryCode("El salvador", "", 503, "SV"));
        mCountryCodes.add(new CountryCode("Equatorial guinea", "", 240, "GQ"));
        mCountryCodes.add(new CountryCode("Eritrea", "", 291, "ER"));
        mCountryCodes.add(new CountryCode("Estonia", "", 372, "EE"));
        mCountryCodes.add(new CountryCode("Ethiopia", "", 251, "ET"));

        mCountryCodes.add(new CountryCode("Falkland Islands (Malvinas)", "", 500, "FK"));
        mCountryCodes.add(new CountryCode("Faroe Islands", "", 298, "FO"));
        mCountryCodes.add(new CountryCode("Fiji", "", 679, "FJ"));
        mCountryCodes.add(new CountryCode("Finland", "", 358, "FI"));
        mCountryCodes.add(new CountryCode("France", "", 33, "FR"));
        mCountryCodes.add(new CountryCode("French Guiana", "", 594, "GF"));
        mCountryCodes.add(new CountryCode("French Polynesia", "", 689, "PF"));
/////////mCountryCodes.add(new CountryCode("France (Guadeloupe)", "", , "GP"));
        /////////mCountryCodes.add(new CountryCode("France (Martinique)", "", , "MQ"));
        /////////mCountryCodes.add(new CountryCode("France (RÃ©union)", "", , "RE"));
        /////////mCountryCodes.add(new CountryCode("French Southern Territories", "", , "TF"));

        mCountryCodes.add(new CountryCode("Gabon", "", 241, "GA"));
        mCountryCodes.add(new CountryCode("Gambia", "", 220, "GM"));
        mCountryCodes.add(new CountryCode("Georgia", "", 995, "GE"));
        mCountryCodes.add(new CountryCode("Germany", "", 49, "DE"));
        mCountryCodes.add(new CountryCode("Ghana", "", 233, "GH"));
        mCountryCodes.add(new CountryCode("Gibraltar", "", 350, "GI"));
        mCountryCodes.add(new CountryCode("Greece", "", 30, "GR"));
        mCountryCodes.add(new CountryCode("Greenland", "", 299, "GL"));
        mCountryCodes.add(new CountryCode("Grenada", "", 1473, "GD"));
        mCountryCodes.add(new CountryCode("Guam", "", 1, "GU"));
        mCountryCodes.add(new CountryCode("Guatemala", "", 502, "GT"));
        mCountryCodes.add(new CountryCode("Guernsey", "", 44, "GG"));
        mCountryCodes.add(new CountryCode("Guinea", "", 224, "GN"));
        mCountryCodes.add(new CountryCode("Guinea-bissau", "", 245, "GW"));
        mCountryCodes.add(new CountryCode("Guyana", "", 592, "GY"));

        mCountryCodes.add(new CountryCode("Haiti", "", 509, "HT"));
        /////////mCountryCodes.add(new CountryCode("Heard Island and McDonald Islands", "", , "HM"));
        mCountryCodes.add(new CountryCode("Honduras", "", 504, "HN"));
        mCountryCodes.add(new CountryCode("Hong Kong", "", 852, "HK"));
        mCountryCodes.add(new CountryCode("Hungary", "", 36, "HU"));

        mCountryCodes.add(new CountryCode("Iceland", "", 354, "IS"));
        mCountryCodes.add(new CountryCode("India", "", 91, "IN"));
        mCountryCodes.add(new CountryCode("Indonesia", "", 62, "ID"));
        mCountryCodes.add(new CountryCode("Iran", "", 98, "IR"));
        mCountryCodes.add(new CountryCode("Iraq", "", 964, "IQ"));
        mCountryCodes.add(new CountryCode("Ireland", "", 353, "IE"));
        mCountryCodes.add(new CountryCode("Isle of Man", "", 44, "IM"));
        mCountryCodes.add(new CountryCode("Italy", "", 39, "IT"));

        mCountryCodes.add(new CountryCode("Jamaica", "", 1876, "JM"));
        mCountryCodes.add(new CountryCode("Japan", "", 81, "JP"));
        mCountryCodes.add(new CountryCode("Jersey", "", 44, "JE"));
        mCountryCodes.add(new CountryCode("Jordan", "", 962, "JO"));

        mCountryCodes.add(new CountryCode("Kazakhstan", "", 7, "KZ"));
        mCountryCodes.add(new CountryCode("Kenya", "", 254, "KE"));
        mCountryCodes.add(new CountryCode("Kiribati", "", 686, "KI"));
        mCountryCodes.add(new CountryCode("Kuwait", "", 965, "KW"));
        mCountryCodes.add(new CountryCode("Kyrgyzstan", "", 996, "KG"));

        mCountryCodes.add(new CountryCode("Laos", "", 856, "LA"));
        mCountryCodes.add(new CountryCode("Latvia", "", 371, "LV"));
        mCountryCodes.add(new CountryCode("Lebanon", "", 961, "LB"));
        mCountryCodes.add(new CountryCode("Lesotho", "", 266, "LS"));
        mCountryCodes.add(new CountryCode("Liberia", "", 231, "LR"));
        mCountryCodes.add(new CountryCode("Libya", "", 218, "LY"));
        mCountryCodes.add(new CountryCode("Liechtenstein", "", 423, "LI"));
        mCountryCodes.add(new CountryCode("Lithuania", "", 370, "LT"));
        mCountryCodes.add(new CountryCode("Luxembourg", "", 352, "LU"));

        mCountryCodes.add(new CountryCode("Macao", "", 853, "MO"));
        mCountryCodes.add(new CountryCode("Macedonia", "", 389, "MK"));
        mCountryCodes.add(new CountryCode("Madagascar", "", 261, "MG"));
        mCountryCodes.add(new CountryCode("Malawi", "", 265, "MW"));
        mCountryCodes.add(new CountryCode("Malaysia", "", 60, "MY"));
        mCountryCodes.add(new CountryCode("Maldives", "", 960, "MV"));
        mCountryCodes.add(new CountryCode("Mali", "", 223, "ML"));
        mCountryCodes.add(new CountryCode("Malta", "", 356, "MT"));
        mCountryCodes.add(new CountryCode("Marshall islands", "", 692, "MH"));
        mCountryCodes.add(new CountryCode("Mauritania", "", 222, "Mr"));
        mCountryCodes.add(new CountryCode("Mauritius", "", 230, "MU"));
        mCountryCodes.add(new CountryCode("Mayotte", "", 262, "YT"));
        mCountryCodes.add(new CountryCode("Mexico", "", 52, "MX"));
        mCountryCodes.add(new CountryCode("Micronesia", "", 691, "FM"));
        mCountryCodes.add(new CountryCode("Moldova", "", 373, "MD"));
        mCountryCodes.add(new CountryCode("Monaco", "", 377, "MC"));
        mCountryCodes.add(new CountryCode("Mongolia", "", 976, "MN"));
        mCountryCodes.add(new CountryCode("Morocco", "", 212, "MA"));
        mCountryCodes.add(new CountryCode("Mozambique", "", 258, "MZ"));
        mCountryCodes.add(new CountryCode("Montserrat", "", 1, "Ms"));


        mCountryCodes.add(new CountryCode("Namibia", "", 264, "NA"));
        mCountryCodes.add(new CountryCode("Nauru", "", 674, "NR"));
        mCountryCodes.add(new CountryCode("Nepal", "", 977, "NP"));
        mCountryCodes.add(new CountryCode("Netherlands", "", 31, "NL"));
        mCountryCodes.add(new CountryCode("New Caledonia", "", 687, "NC"));
        mCountryCodes.add(new CountryCode("Newzealand", "", 64, "NZ"));
        mCountryCodes.add(new CountryCode("Nicaragua", "", 505, "NI"));
        mCountryCodes.add(new CountryCode("Niger", "", 227, "NE"));
        mCountryCodes.add(new CountryCode("Nigeria", "", 234, "NG"));
        mCountryCodes.add(new CountryCode("Niue", "", 683, "NU"));
        mCountryCodes.add(new CountryCode("Norfolk Island", "", 672, "NF"));
        mCountryCodes.add(new CountryCode("Northern Mariana Islands", "", 1, "MP"));
        mCountryCodes.add(new CountryCode("North korea", "", 850, "KP"));
        mCountryCodes.add(new CountryCode("Norway", "", 47, "NO"));

        mCountryCodes.add(new CountryCode("Oman", "", 968, "OM"));

        mCountryCodes.add(new CountryCode("Pakistan", "", 92, "PK"));
        mCountryCodes.add(new CountryCode("Palau", "", 680, "PW"));
        mCountryCodes.add(new CountryCode("Palestinian Territory, Occupied", "", 970, "PS"));
        mCountryCodes.add(new CountryCode("Panama", "", 507, "PA"));
        mCountryCodes.add(new CountryCode("Papua new guinea", "", 675, "PG"));
        mCountryCodes.add(new CountryCode("Paraguay", "", 595, "PY"));
        mCountryCodes.add(new CountryCode("Peru", "", 51, "PE"));
        mCountryCodes.add(new CountryCode("Philippines", "", 63, "PH"));
        mCountryCodes.add(new CountryCode("Pitcairn", "", 870, "PN"));
        mCountryCodes.add(new CountryCode("Poland", "", 48, "PL"));
        mCountryCodes.add(new CountryCode("Portugal", "", 351, "PT"));
        mCountryCodes.add(new CountryCode("Puerto Rico", "", 1, "PR"));

        mCountryCodes.add(new CountryCode("Qatar", "", 974, "QA"));

        mCountryCodes.add(new CountryCode("Romania", "", 40, "RO"));
        mCountryCodes.add(new CountryCode("Russia", "", 7, "RU"));
        mCountryCodes.add(new CountryCode("Rwanda", "", 250, "RW"));

        mCountryCodes.add(new CountryCode("Saint BarthÃ©lemy", "", 590, "BL"));
        /////////mCountryCodes.add(new CountryCode("Saint Helena, Ascension and Tristan da Cunha", "", , "SH"));
        mCountryCodes.add(new CountryCode("Saint kitts and nevis", "", 1, "KN"));
        mCountryCodes.add(new CountryCode("Saint lucia", "", 1, "LC"));
        mCountryCodes.add(new CountryCode("Saint Martin (French part)", "", 590, "MF"));
        mCountryCodes.add(new CountryCode("Saint Pierre and Miquelon", "", 508, "PM"));
        mCountryCodes.add(new CountryCode("Saint vincent and the grenadines", "", 1, "VC"));
        mCountryCodes.add(new CountryCode("Samoa", "", 685, "WS"));
        mCountryCodes.add(new CountryCode("San marino", "", 378, "SM"));
        mCountryCodes.add(new CountryCode("Sao tome and principe", "", 239, "ST"));
        mCountryCodes.add(new CountryCode("Saudi arabia", "", 966, "SA"));
        mCountryCodes.add(new CountryCode("Senegal", "", 221, "SN"));
        mCountryCodes.add(new CountryCode("Serbia", "", 381, "RS"));
        mCountryCodes.add(new CountryCode("Seychelles", "", 248, "SC"));
        mCountryCodes.add(new CountryCode("Sierra leone", "", 232, "SL"));
        mCountryCodes.add(new CountryCode("Singapore", "", 65, "SG"));
        mCountryCodes.add(new CountryCode("Sint Maarten (Dutch part)", "", 1, "SX"));
        mCountryCodes.add(new CountryCode("Slovakia", "", 421, "SK"));
        mCountryCodes.add(new CountryCode("Slovenia", "", 386, "SI"));
        mCountryCodes.add(new CountryCode("Solomon islands", "", 677, "SB"));
        mCountryCodes.add(new CountryCode("Somalia", "", 252, "SO"));
        mCountryCodes.add(new CountryCode("South africa", "", 27, "ZA"));
        mCountryCodes.add(new CountryCode("South korea", "", 82, "KR"));
        /////////mCountryCodes.add(new CountryCode("South Georgia and the South Sandwich Islands", "", , "GS"));
        mCountryCodes.add(new CountryCode("Spain", "", 34, "ES"));
        mCountryCodes.add(new CountryCode("Sri lanka", "", 94, "LK"));
        mCountryCodes.add(new CountryCode("Sudan", "", 249, "SD"));
        mCountryCodes.add(new CountryCode("Suriname", "", 597, "SR"));
        mCountryCodes.add(new CountryCode("Svalbard and Jan Mayen", "", 47, "SJ"));
        mCountryCodes.add(new CountryCode("Swaziland", "", 268, "SZ"));
        mCountryCodes.add(new CountryCode("Sweden", "", 46, "SE"));
        mCountryCodes.add(new CountryCode("Switzerland", "", 41, "CH"));
        mCountryCodes.add(new CountryCode("Syria", "", 963, "SY"));

        mCountryCodes.add(new CountryCode("Taiwan", "", 886, "TW"));
        mCountryCodes.add(new CountryCode("Tajikistan", "", 992, "TJ"));
        mCountryCodes.add(new CountryCode("Tanzania", "", 255, "TZ"));
        mCountryCodes.add(new CountryCode("Thailand", "", 66, "TH"));
        mCountryCodes.add(new CountryCode("The bahamas", "", 1, "BS"));
        mCountryCodes.add(new CountryCode("Togo", "", 228, "TG"));
        mCountryCodes.add(new CountryCode("Tokelau", "", 690, "TK"));
        mCountryCodes.add(new CountryCode("Tonga", "", 676, "TO"));
        mCountryCodes.add(new CountryCode("Trinidad and tobago", "", 1, "TT"));
        mCountryCodes.add(new CountryCode("Tunisia", "", 216, "TN"));
        mCountryCodes.add(new CountryCode("Turkey", "", 90, "TR"));
        mCountryCodes.add(new CountryCode("Turkmenistan", "", 993, "TM"));
        mCountryCodes.add(new CountryCode("Turks and Caicos Islands", "", 1, "TC"));
        mCountryCodes.add(new CountryCode("Tuvalu", "", 688, "TV"));


        mCountryCodes.add(new CountryCode("Uganda", "", 256, "UG"));
        mCountryCodes.add(new CountryCode("Ukraine", "", 380, "UA"));
        mCountryCodes.add(new CountryCode("United arab emirates", "", 971, "AE"));
        mCountryCodes.add(new CountryCode("United kingdom", "", 44, "GB"));
        mCountryCodes.add(new CountryCode("United states", "", 1, "US"));
        /////////mCountryCodes.add(new CountryCode("United States minor outlying islands", "", , "UM"));
        mCountryCodes.add(new CountryCode("Uruguay", "", 598, "UY"));
        mCountryCodes.add(new CountryCode("Uzbekistan", "", 998, "UZ"));

        mCountryCodes.add(new CountryCode("Vanuatu", "", 678, "VU"));
        mCountryCodes.add(new CountryCode("Vatican city", "", 39, "VA"));
        mCountryCodes.add(new CountryCode("Venezuela", "", 58, "VE"));
        mCountryCodes.add(new CountryCode("Vietnam", "", 84, "VN"));
        mCountryCodes.add(new CountryCode("Virgin Islands, British", "", 1, "VG"));
        mCountryCodes.add(new CountryCode("Virgin Islands, United States", "", 1, "VI"));

        mCountryCodes.add(new CountryCode("Wallis and Futuna", "", 681, "WF"));
        mCountryCodes.add(new CountryCode("Western Sahara", "", 212, "EH"));

        mCountryCodes.add(new CountryCode("Yemen", "", 967, "YE"));
        mCountryCodes.add(new CountryCode("Yugoslavia/Serbia And Montenegro", "", 382, "ME"));

        mCountryCodes.add(new CountryCode("Zambia", "", 260, "ZM"));
        mCountryCodes.add(new CountryCode("Zimbabwe", "", 263, "ZW"));

    }

    private String refineCode(String token) {
        if (token.startsWith("+")) {
            token = token.replace("+", "");

        } else if (token.startsWith("00")) {
            token = token.substring(2);
        }

        return token;
    }

    //--------------------------------------------------------------------------------------------//

    /**
     * @param sortBase 1: NameEn, 2: NameAr, 3: Code
     */
    private void sortList(ArrayList<CountryCode> list, final int sortBase) {
        Collections.sort(list, new Comparator<CountryCode>() {
            @Override
            public int compare(CountryCode o1, CountryCode o2) {
                if (sortBase == 3) {
                    if (o1.dialCode > o2.dialCode) return 1;
                    else if (o1.dialCode < o2.dialCode) return -1;
                } else {
                    if (sortBase == 1) {
                        return o1.nameEn.compareTo(o2.nameEn);
                    } else {
                        return o1.nameAr.compareTo(o2.nameAr);
                    }
                }

                return 0;
            }
        });
    }

    public CountryPhoneCodes sortByName() {
        if (Locale.getDefault().getLanguage().equals(Locale.ENGLISH.getLanguage()))
            sortByNameEn();
        else
            sortByNameAr();
        return this;
    }

    public CountryPhoneCodes sortByNameEn() {
        sortList(mCountryCodes, 1);
        return this;
    }

    public CountryPhoneCodes sortByNameAr() {
        sortList(mCountryCodes, 2);
        return this;
    }

    public CountryPhoneCodes sortByCode() {
        sortList(mCountryCodes, 3);
        return this;
    }

    public ArrayList<CountryCode> getCountryCodes() {
        //return new ArrayList<>(mCountryCodes);
        return mCountryCodes;
    }

    //----------------------------------------------------------------------------------------------

    public enum TargetFields {all, name, alpha_code, dial_code}

    public ArrayList<CountryCode> search(String token, TargetFields targetFields) {
        if (token == null || token.isEmpty()) return getCountryCodes();
        token = token.toLowerCase();

        ArrayList<CountryCode> newList = new ArrayList<>();

        for (CountryCode cCode : mCountryCodes) {
            if (TargetFields.name == targetFields) {
                if (cCode.nameEn.toLowerCase().contains(token)) newList.add(cCode);
                else if (cCode.nameAr.contains(token)) newList.add(cCode);

            } else if (TargetFields.alpha_code == targetFields) {
                if (cCode.alpha2Code.toLowerCase().indexOf(token) == 0) newList.add(cCode);

            } else if (TargetFields.dial_code == targetFields) {
                if ((cCode.dialCode + "").indexOf(token) == 0) newList.add(cCode);

            } else {
                if ((cCode.dialCode + "").indexOf(token) == 0) newList.add(cCode);
                else if (cCode.alpha2Code.toLowerCase().indexOf(token) == 0) newList.add(cCode);
                else if (cCode.nameEn.toLowerCase().contains(token)) newList.add(cCode);
                else if (cCode.nameAr.contains(token)) newList.add(cCode);
            }
        }

        return newList;
    }

    public void searchAsync(String token, TargetFields targetFields, ResultCallback<ArrayList<CountryCode>> callback) {
        new Thread(() -> {
            ArrayList<CountryCode> countryCodes = search(token, targetFields);
            new Handler(Looper.getMainLooper()).post(() -> {
                callback.invoke(countryCodes);
            });
        }).start();
    }

    public void findCountryPhoneCodeOfPhoneNumber(String phoneNumber, ResultCallback<CountryPhoneCodes.CountryCode> callback) {
        if (TextUtils.isEmpty(phoneNumber)) {
            if (callback != null) callback.invoke(null);
            return;
        }

        class FindRunnable implements Runnable {
            private final String phoneNumber;
            private ResultCallback<CountryPhoneCodes.CountryCode> callback;

            FindRunnable(String phoneNumber, ResultCallback<CountryPhoneCodes.CountryCode> callback) {
                if (phoneNumber.startsWith("00")) {
                    phoneNumber = phoneNumber.replaceFirst("00", "");

                } else if (phoneNumber.startsWith("+")) {
                    phoneNumber = phoneNumber.replace("+", "");
                }

                this.phoneNumber = phoneNumber;
                this.callback = callback;

            }

            private CountryPhoneCodes.CountryCode foundedCountryCode;

            @Override
            public void run() {
                foundedCountryCode = null;

                for (CountryPhoneCodes.CountryCode cCode : mCountryCodes) {
                    if (phoneNumber.startsWith(cCode.dialCode + "")) {

                        foundedCountryCode = cCode;

                        break;
                    }
                }

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (callback != null) {
                        callback.invoke(foundedCountryCode);
                    }
                    callback = null;
                });

            }
        }

        new Thread(new FindRunnable(phoneNumber, callback)).start();
    }

    //--------------------------------------------------------------------------------------------//

    public static class CountryCode implements Parcelable, Serializable {
        private final String nameEn;
        private final String nameAr;
        private final String alpha2Code;
        private final int dialCode;
        private int flagResId;

        public CountryCode(String nameEn, String nameAr, int dialCode, String alpha2Code) {
            this.nameEn = nameEn;
            this.nameAr = nameAr;

            //if (TextUtils.isEmpty(nameAr)) this.nameAr = nameEn;

            this.dialCode = dialCode;
            this.alpha2Code = alpha2Code;
        }

        public String getName() {
            if (SettingsStorage.Language.usingEnglish()) {
                return nameEn;
            } else {
                if (TextUtils.isEmpty(nameAr)) {
                    return nameEn;
                } else {
                    return nameAr;
                }
            }
        }

        public String getNameEn() {
            return nameEn;
        }

        public String getNameAr() {
            return nameAr;
        }

        public String getAlpha2Code() {
            return alpha2Code;
        }

        public int getDialCode() {
            return dialCode;
        }

        public int getFlagResId(Context context) {
            if (flagResId == 0) {
                int identifier = context.getResources().getIdentifier("flag_" + getAlpha2Code().toLowerCase(), "drawable", null);
                if (identifier > 0) {
                    flagResId = identifier;
                }
            }
            return flagResId;
        }

        @Override
        public String toString() {
            return nameEn + " / " + nameAr + ", (" + alpha2Code + "), " + dialCode;
        }

        protected CountryCode(Parcel in) {
            nameEn = in.readString();
            nameAr = in.readString();
            alpha2Code = in.readString();
            dialCode = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(nameEn);
            dest.writeString(nameAr);
            dest.writeString(alpha2Code);
            dest.writeInt(dialCode);
        }

        public static final Creator<CountryCode> CREATOR = new Creator<CountryCode>() {
            @Override
            public CountryCode createFromParcel(Parcel in) {
                return new CountryCode(in);
            }

            @Override
            public CountryCode[] newArray(int size) {
                return new CountryCode[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj != null) {
                if (this == obj) return true;

                if (obj instanceof CountryCode) {
                    CountryCode nObj = (CountryCode) obj;
                    if (!TextUtils.equals(nameEn, nObj.nameEn)) return false;
                    if (!TextUtils.equals(nameAr, nObj.nameAr)) return false;
                    if (!TextUtils.equals(alpha2Code, nObj.alpha2Code)) return false;

                    return dialCode == nObj.dialCode;
                }
            }

            return false;
        }
    }
}
