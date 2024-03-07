package gmutils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TimePicker;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
public class DateOp implements Serializable {
    //https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html

    public static final String PATTERN_SYMBOL_YEAR = "yyyy";

    public static final String PATTERN_SYMBOL_MONTH = "MM";
    public static final String PATTERN_SYMBOL_MONTH_NAME = "MMMM";
    public static final String PATTERN_SYMBOL_MONTH_HALF_NAME = "MMM";

    public static final String PATTERN_SYMBOL_DAY = "dd";
    public static final String PATTERN_SYMBOL_DAY_NAME = "EEEE";
    public static final String PATTERN_SYMBOL_DAY_HALF_NAME = "EEE";

    public static final String PATTERN_SYMBOL_HOUR24 = "HH";
    public static final String PATTERN_SYMBOL_HOUR12 = "hh";

    public static final String PATTERN_SYMBOL_MINUTE = "mm";
    public static final String PATTERN_SYMBOL_SECOND = "ss";
    public static final String PATTERN_SYMBOL_MILLISECOND = "SSS";
    public static final String PATTERN_SYMBOL_AM_PM = "a";

    public static final String PATTERN_SYMBOL_TIME_ZONE = "z";//General time zone -> GMT+02:00
    public static final String PATTERN_SYMBOL_TIME_ZONE_RFC822 = "Z";//+0200
    public static final String PATTERN_SYMBOL_TIME_ZONE_ISO8601 = "XXX";//+02:00

    //----------------------------------------------------------------------------------------------

    public static final String PATTERN_HH_mm = "HH:mm";
    public static final String PATTERN_hh_mm_a = "hh:mm a";

    public static final String PATTERN_HH_mm_ss = "HH:mm:ss";
    public static final String PATTERN_hh_mm_ss_a = "hh:mm:ss a";

    public static final String PATTERN_HH_mm_ss_SSS = "HH:mm:ss.SSS";
    public static final String PATTERN_HH_mm_ss_SSS_z = "HH:mm:ss.SSS z"; //.... GMT+02:00
    public static final String PATTERN_HH_mm_ss_SSS_Z = "HH:mm:ss.SSS Z"; //.... +0200
    public static final String PATTERN_HH_mm_ss_SSS_XXX = "HH:mm:ss.SSS XXX";//.... +02:00

    public static final String PATTERN_dd_MM_yyyy = "dd-MM-yyyy";
    public static final String PATTERN_dd_MM_yyyy_HH_mm = "dd-MM-yyyy HH:mm";
    public static final String PATTERN_dd_MM_yyyy_HH_mm_ss = "dd-MM-yyyy HH:mm:ss";
    public static final String PATTERN_dd_MM_yyyy_HH_mm_ss_z = "dd-MM-yyyy HH:mm:ss z";//....:00 GMT+02:00
    public static final String PATTERN_dd_MM_yyyy_HH_mm_ssZ = "dd-MM-yyyy HH:mm:ssZ"; //....:00+0200
    public static final String PATTERN_dd_MM_yyyy_HH_mm_ssXXX = "dd-MM-yyyy HH:mm:ssXXX";//...:00+02:00
    public static final String PATTERN_dd_MM_yyyy_HH_mm_ss_SSS_z = "dd-MM-yyyy HH:mm:ss.SSS z";//....:00 GMT+02:00
    public static final String PATTERN_dd_MM_yyyy_HH_mm_ss_SSSZ = "dd-MM-yyyy HH:mm:ss.SSSZ"; //....:00+0200
    public static final String PATTERN_dd_MM_yyyy_HH_mm_ss_SSSXXX = "dd-MM-yyyy HH:mm:ss.SSSXXX";//...:00+02:00

    public static final String PATTERN_yyyy_MM_dd = "yyyy-MM-dd";
    public static final String PATTERN_yyyy_MM_dd_HH_mm = "yyyy-MM-dd HH:mm";
    public static final String PATTERN_yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_yyyy_MM_dd_HH_mm_ss_z = "yyyy-MM-dd HH:mm:ss z";
    public static final String PATTERN_yyyy_MM_dd_HH_mm_ssZ = "yyyy-MM-dd HH:mm:ssZ";
    public static final String PATTERN_yyyy_MM_dd_HH_mm_ssXXX = "yyyy-MM-dd HH:mm:ssXXX";
    public static final String PATTERN_yyyy_MM_dd_HH_mm_ss_SSS_z = "yyyy-MM-dd HH:mm:ss.SSS z";
    public static final String PATTERN_yyyy_MM_dd_HH_mm_ss_SSSZ = "yyyy-MM-dd HH:mm:ss.SSSZ";
    public static final String PATTERN_yyyy_MM_dd_HH_mm_ss_SSSXXX = "yyyy-MM-dd HH:mm:ss.SSSXXX";

    public static final String PATTERN_yyyy_MM_dd_T_HH_mm_ss = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String PATTERN_yyyy_MM_dd_T_HH_mm_ssz = "yyyy-MM-dd'T'HH:mm:ssz";
    public static final String PATTERN_yyyy_MM_dd_T_HH_mm_ss_SSS_z = "yyyy-MM-dd'T'HH:mm:ss.SSSz";

    public static final String PATTERN_dd_MMM_EEEE_yyyy = "dd-MMM, EEEE yyyy"; //01-Mar, Saturday 2020
    public static final String PATTERN_dd_MMM_EEEE_yyyy_HH_mm_ss = "dd-MMM, EEEE yyyy HH:mm:ss";

    public static final String PATTERN_EEEE_dd_MMMM_yyyy = "EEEE dd MMMM yyyy";
    public static final String PATTERN_EEEE_dd_MMMM_yyyy_HH_mm = "EEEE dd MMMM yyyy, HH:mm";
    public static final String PATTERN_EEEE_dd_MMMM_yyyy_HH_mm_ss = "EEEE dd MMMM yyyy, HH:mm:ss";
    public static final String PATTERN_MMMM_dd_EEE_yyyy = "MMMM dd, EEE yyyy";
    public static final String PATTERN_MMMM_dd_yyyy = "MMMM dd, yyyy";
    public static final String PATTERN_dd_MMM_yyyy = "dd-MMM-yyyy";
    public static final String PATTERN_HH_mm_MMM_dd_EEE_yyyy = "HH:mm, MMM dd, EEE yyyy";

    public static final long ONE_SECOND_MILLIS = 1_000L; //   (0d * 00h * 00m * 01s * 1000ms);
    public static final long ONE_MINUTE_MILLIS = 60_000L; //  (0d * 00h * 01m * 60s * 1000ms);
    public static final long ONE_HOUR_MILLIS = 3_600_000L; // (0d * 01h * 60m * 60s * 1000ms);
    public static final long ONE_DAY_MILLIS = 86_400_000L; // (1d * 24h * 60m * 60s * 1000ms);

    private Calendar calendar;
    private Locale currentLocale;

    public static DateOp getInstance() {
        return getInstance(Locale.getDefault());
    }

    public static DateOp getInstance(Locale locale) {
        return new DateOp(Calendar.getInstance(locale), locale);
    }

    public static DateOp getInstance(long timeMillis) {
        return getInstance(timeMillis, Locale.getDefault());
    }

    public static DateOp getInstance(long timeMillis, Locale locale) {
        Calendar calendar = Calendar.getInstance(locale);
        calendar.setTimeInMillis(timeMillis);

        return new DateOp(calendar, locale);
    }

    public static DateOp getInstance(String time, String pattern) {
        return getInstance(time, pattern, false);
    }

    public static DateOp getInstance(String time, String pattern, boolean throwException) {
        return getInstance(time, pattern, Locale.getDefault(), throwException);
    }

    public static DateOp getInstance(String time, String pattern, Locale locale, boolean throwException) {
        DateOp dateOp = new DateOp(Calendar.getInstance(locale), locale);
        return dateOp.parseDate(time, pattern, throwException);
    }

    public static DateOp getInstance(String time, boolean throwException) {
        return getInstance(time, Locale.getDefault(), throwException);
    }

    public static DateOp getInstance(String time, Locale locale, boolean throwException) {
        DateOp dateOp = new DateOp(Calendar.getInstance(locale), locale);
        return dateOp.tryParseDate(time, throwException);
    }

    private DateOp(Calendar calendar, Locale locale) {
        this.calendar = calendar;
        this.currentLocale = locale;
    }

    @NotNull
    @Override
    public String toString() {
        return formatDate("E dd-MMMM'('MM')'-yyyy HH:mm:ss.SSS", false) + " / " + getTimeInMillis();
    }

    //--------------------------------------------------------------------------------------------//

    public DateOp setCurrentLocaleToArabic() {
        return setCurrentLocale(new Locale("ar"));
    }

    public DateOp setCurrentLocaleToEnglish() {
        return setCurrentLocale(Locale.ENGLISH);
    }

    public DateOp setCurrentLocale(Locale currentLocale) {
        long timeInMillis = calendar.getTimeInMillis();
        calendar = Calendar.getInstance(currentLocale);
        calendar.setTimeInMillis(timeInMillis);
        this.currentLocale = currentLocale;
        return this;
    }

    //--------------------------------------------------------------------------------------------//

    public DateOp parseDate(String date, String pattern, boolean throwException) {
        Long l = null;
        String exception = "";

        try {
            l = DateOp.parseDate(date, pattern);
        } catch (Exception E) {
            if (throwException)
                throw new RuntimeException(E);
            else
                exception = E.getMessage();
        }

        if (l != null) {
            setTimeInMillis(l);
            return this;
        } else {
            throw new IllegalArgumentException(date + " | " + pattern + "\n----\n" + exception);
        }
    }

    public DateOp tryParseDate(String date, boolean throwException) {
        Long parseDate = DateOp.tryParseDate(date);
        if (parseDate == null) {
            if (throwException) {
                throw new IllegalArgumentException("Unknown date format of " + date);
            }
        } else {
            setTimeInMillis(parseDate);
        }

        return this;
    }

    //--------------------------------------------------------------------------------------------//

    public DateOp setTimeZone(float hours) {
        TimeZone timeZone = TimeZone.getDefault();
        timeZone.setRawOffset((int) (hours * ONE_HOUR_MILLIS));
        this.calendar.setTimeZone(timeZone);

        return this;
    }

    public DateOp setTimeZone(String areaId) throws Exception {
        /**
         * Africa/Accra
         * Africa/Addis_Ababa
         * Africa/Algiers
         * Africa/Asmara
         * Africa/Asmera
         * Africa/Bamako
         * Africa/Bangui
         * Africa/Banjul
         * Africa/Bissau
         * Africa/Blantyre
         * Africa/Brazzaville
         * Africa/Bujumbura
         * Africa/Cairo
         * Africa/Casablanca
         * Africa/Ceuta
         * Africa/Conakry
         * Africa/Dakar
         * Africa/Dar_es_Salaam
         * Africa/Djibouti
         * Africa/Douala
         * Africa/El_Aaiun
         * Africa/Freetown
         * Africa/Gaborone
         * Africa/Harare
         * Africa/Johannesburg
         * Africa/Juba
         * Africa/Kampala
         * Africa/Khartoum
         * Africa/Kigali
         * Africa/Kinshasa
         * Africa/Lagos
         * Africa/Libreville
         * Africa/Lome
         * Africa/Luanda
         * Africa/Lubumbashi
         * Africa/Lusaka
         * Africa/Malabo
         * Africa/Maputo
         * Africa/Maseru
         * Africa/Mbabane
         * Africa/Mogadishu
         * Africa/Monrovia
         * Africa/Nairobi
         * Africa/Ndjamena
         * Africa/Niamey
         * Africa/Nouakchott
         * Africa/Ouagadougou
         * Africa/Porto-Novo
         * Africa/Sao_Tome
         * Africa/Timbuktu
         * Africa/Tripoli
         * Africa/Tunis
         * Africa/Windhoek
         * America/Adak
         * America/Anchorage
         * America/Anguilla
         * America/Antigua
         * America/Araguaina
         * America/Argentina/Buenos_Aires
         * America/Argentina/Catamarca
         * America/Argentina/ComodRivadavia
         * America/Argentina/Cordoba
         * America/Argentina/Jujuy
         * America/Argentina/La_Rioja
         * America/Argentina/Mendoza
         * America/Argentina/Rio_Gallegos
         * America/Argentina/Salta
         * America/Argentina/San_Juan
         * America/Argentina/San_Luis
         * America/Argentina/Tucuman
         * America/Argentina/Ushuaia
         * America/Aruba
         * America/Asuncion
         * America/Atikokan
         * America/Atka
         * America/Bahia
         * America/Bahia_Banderas
         * America/Barbados
         * America/Belem
         * America/Belize
         * America/Blanc-Sablon
         * America/Boa_Vista
         * America/Bogota
         * America/Boise
         * America/Buenos_Aires
         * America/Cambridge_Bay
         * America/Campo_Grande
         * America/Cancun
         * America/Caracas
         * America/Catamarca
         * America/Cayenne
         * America/Cayman
         * America/Chicago
         * America/Chihuahua
         * America/Coral_Harbour
         * America/Cordoba
         * America/Costa_Rica
         * America/Creston
         * America/Cuiaba
         * America/Curacao
         * America/Danmarkshavn
         * America/Dawson
         * America/Dawson_Creek
         * America/Denver
         * America/Detroit
         * America/Dominica
         * America/Edmonton
         * America/Eirunepe
         * America/El_Salvador
         * America/Ensenada
         * America/Fort_Nelson
         * America/Fort_Wayne
         * America/Fortaleza
         * America/Glace_Bay
         * America/Godthab
         * America/Goose_Bay
         * America/Grand_Turk
         * America/Grenada
         * America/Guadeloupe
         * America/Guatemala
         * America/Guayaquil
         * America/Guyana
         * America/Halifax
         * America/Havana
         * America/Hermosillo
         * America/Indiana/Indianapolis
         * America/Indiana/Knox
         * America/Indiana/Marengo
         * America/Indiana/Petersburg
         * America/Indiana/Tell_City
         * America/Indiana/Vevay
         * America/Indiana/Vincennes
         * America/Indiana/Winamac
         * America/Indianapolis
         * America/Inuvik
         * America/Iqaluit
         * America/Jamaica
         * America/Jujuy
         * America/Juneau
         * America/Kentucky/Louisville
         * America/Kentucky/Monticello
         * America/Knox_IN
         * America/Kralendijk
         * America/La_Paz
         * America/Lima
         * America/Los_Angeles
         * America/Louisville
         * America/Lower_Princes
         * America/Maceio
         * America/Managua
         * America/Manaus
         * America/Marigot
         * America/Martinique
         * America/Matamoros
         * America/Mazatlan
         * America/Mendoza
         * America/Menominee
         * America/Merida
         * America/Metlakatla
         * America/Mexico_City
         * America/Miquelon
         * America/Moncton
         * America/Monterrey
         * America/Montevideo
         * America/Montreal
         * America/Montserrat
         * America/Nassau
         * America/New_York
         * America/Nipigon
         * America/Nome
         * America/Noronha
         * America/North_Dakota/Beulah
         * America/North_Dakota/Center
         * America/North_Dakota/New_Salem
         * America/Ojinaga
         * America/Panama
         * America/Pangnirtung
         * America/Paramaribo
         * America/Phoenix
         * America/Port-au-Prince
         * America/Port_of_Spain
         * America/Porto_Acre
         * America/Porto_Velho
         * America/Puerto_Rico
         * America/Punta_Arenas
         * America/Rainy_River
         * America/Rankin_Inlet
         * America/Recife
         * America/Regina
         * America/Resolute
         * America/Rio_Branco
         * America/Rosario
         * America/Santa_Isabel
         * America/Santarem
         * America/Santiago
         * America/Santo_Domingo
         * America/Sao_Paulo
         * America/Scoresbysund
         * America/Shiprock
         * America/Sitka
         * America/St_Barthelemy
         * America/St_Johns
         * America/St_Kitts
         * America/St_Lucia
         * America/St_Thomas
         * America/St_Vincent
         * America/Swift_Current
         * America/Tegucigalpa
         * America/Thule
         * America/Thunder_Bay
         * America/Tijuana
         * America/Toronto
         * America/Tortola
         * America/Vancouver
         * America/Virgin
         * America/Whitehorse
         * America/Winnipeg
         * America/Yakutat
         * America/Yellowknife
         * Antarctica/Casey
         * Antarctica/Davis
         * Antarctica/DumontDUrville
         * Antarctica/Macquarie
         * Antarctica/Mawson
         * Antarctica/McMurdo
         * Antarctica/Palmer
         * Antarctica/Rothera
         * Antarctica/South_Pole
         * Antarctica/Syowa
         * Antarctica/Troll
         * Antarctica/Vostok
         * Arctic/Longyearbyen
         * Asia/Aden
         * Asia/Almaty
         * Asia/Amman
         * Asia/Anadyr
         * Asia/Aqtau
         * Asia/Aqtobe
         * Asia/Ashgabat
         * Asia/Ashkhabad
         * Asia/Atyrau
         * Asia/Baghdad
         * Asia/Bahrain
         * Asia/Baku
         * Asia/Bangkok
         * Asia/Barnaul
         * Asia/Beirut
         * Asia/Bishkek
         * Asia/Brunei
         * Asia/Calcutta
         * Asia/Chita
         * Asia/Choibalsan
         * Asia/Chongqing
         * Asia/Chungking
         * Asia/Colombo
         * Asia/Dacca
         * Asia/Damascus
         * Asia/Dhaka
         * Asia/Dili
         * Asia/Dubai
         * Asia/Dushanbe
         * Asia/Famagusta
         * Asia/Gaza
         * Asia/Hanoi
         * Asia/Harbin
         * Asia/Hebron
         * Asia/Ho_Chi_Minh
         * Asia/Hong_Kong
         * Asia/Hovd
         * Asia/Irkutsk
         * Asia/Istanbul
         * Asia/Jakarta
         * Asia/Jayapura
         * Asia/Jerusalem
         * Asia/Kabul
         * Asia/Kamchatka
         * Asia/Karachi
         * Asia/Kashgar
         * Asia/Kathmandu
         * Asia/Katmandu
         * Asia/Khandyga
         * Asia/Kolkata
         * Asia/Krasnoyarsk
         * Asia/Kuala_Lumpur
         * Asia/Kuching
         * Asia/Kuwait
         * Asia/Macao
         * Asia/Macau
         * Asia/Magadan
         * Asia/Makassar
         * Asia/Manila
         * Asia/Muscat
         * Asia/Nicosia
         * Asia/Novokuznetsk
         * Asia/Novosibirsk
         * Asia/Omsk
         * Asia/Oral
         * Asia/Phnom_Penh
         * Asia/Pontianak
         * Asia/Pyongyang
         * Asia/Qatar
         * Asia/Qyzylorda
         * Asia/Rangoon
         * Asia/Riyadh
         * Asia/Saigon
         * Asia/Sakhalin
         * Asia/Samarkand
         * Asia/Seoul
         * Asia/Shanghai
         * Asia/Singapore
         * Asia/Srednekolymsk
         * Asia/Taipei
         * Asia/Tashkent
         * Asia/Tbilisi
         * Asia/Tehran
         * Asia/Tel_Aviv
         * Asia/Thimbu
         * Asia/Thimphu
         * Asia/Tokyo
         * Asia/Tomsk
         * Asia/Ujung_Pandang
         * Asia/Ulaanbaatar
         * Asia/Ulan_Bator
         * Asia/Urumqi
         * Asia/Ust-Nera
         * Asia/Vientiane
         * Asia/Vladivostok
         * Asia/Yakutsk
         * Asia/Yangon
         * Asia/Yekaterinburg
         * Asia/Yerevan
         * Atlantic/Azores
         * Atlantic/Bermuda
         * Atlantic/Canary
         * Atlantic/Cape_Verde
         * Atlantic/Faeroe
         * Atlantic/Faroe
         * Atlantic/Jan_Mayen
         * Atlantic/Madeira
         * Atlantic/Reykjavik
         * Atlantic/South_Georgia
         * Atlantic/St_Helena
         * Atlantic/Stanley
         * Australia/ACT
         * Australia/Adelaide
         * Australia/Brisbane
         * Australia/Broken_Hill
         * Australia/Canberra
         * Australia/Currie
         * Australia/Darwin
         * Australia/Eucla
         * Australia/Hobart
         * Australia/LHI
         * Australia/Lindeman
         * Australia/Lord_Howe
         * Australia/Melbourne
         * Australia/NSW
         * Australia/North
         * Australia/Perth
         * Australia/Queensland
         * Australia/South
         * Australia/Sydney
         * Australia/Tasmania
         * Australia/Victoria
         * Australia/West
         * Australia/Yancowinna
         * Brazil/Acre
         * Brazil/DeNoronha
         * Brazil/East
         * Brazil/West
         * CET
         * CST6CDT
         * Canada/Atlantic
         * Canada/Central
         * Canada/Eastern
         * Canada/Mountain
         * Canada/Newfoundland
         * Canada/Pacific
         * Canada/Saskatchewan
         * Canada/Yukon
         * Chile/Continental
         * Chile/EasterIsland
         * Cuba
         * EET
         * EST
         * EST5EDT
         * Egypt
         * Eire
         * Etc/GMT
         * Etc/GMT+0
         * Etc/GMT+1
         * Etc/GMT+10
         * Etc/GMT+11
         * Etc/GMT+12
         * Etc/GMT+2
         * Etc/GMT+3
         * Etc/GMT+4
         * Etc/GMT+5
         * Etc/GMT+6
         * Etc/GMT+7
         * Etc/GMT+8
         * Etc/GMT+9
         * Etc/GMT-0
         * Etc/GMT-1
         * Etc/GMT-10
         * Etc/GMT-11
         * Etc/GMT-12
         * Etc/GMT-13
         * Etc/GMT-14
         * Etc/GMT-2
         * Etc/GMT-3
         * Etc/GMT-4
         * Etc/GMT-5
         * Etc/GMT-6
         * Etc/GMT-7
         * Etc/GMT-8
         * Etc/GMT-9
         * Etc/GMT0
         * Etc/Greenwich
         * Etc/UCT
         * Etc/UTC
         * Etc/Universal
         * Etc/Zulu
         * Europe/Amsterdam
         * Europe/Andorra
         * Europe/Astrakhan
         * Europe/Athens
         * Europe/Belfast
         * Europe/Belgrade
         * Europe/Berlin
         * Europe/Bratislava
         * Europe/Brussels
         * Europe/Bucharest
         * Europe/Budapest
         * Europe/Busingen
         * Europe/Chisinau
         * Europe/Copenhagen
         * Europe/Dublin
         * Europe/Gibraltar
         * Europe/Guernsey
         * Europe/Helsinki
         * Europe/Isle_of_Man
         * Europe/Istanbul
         * Europe/Jersey
         * Europe/Kaliningrad
         * Europe/Kiev
         * Europe/Kirov
         * Europe/Lisbon
         * Europe/Ljubljana
         * Europe/London
         * Europe/Luxembourg
         * Europe/Madrid
         * Europe/Malta
         * Europe/Mariehamn
         * Europe/Minsk
         * Europe/Monaco
         * Europe/Moscow
         * Europe/Nicosia
         * Europe/Oslo
         * Europe/Paris
         * Europe/Podgorica
         * Europe/Prague
         * Europe/Riga
         * Europe/Rome
         * Europe/Samara
         * Europe/San_Marino
         * Europe/Sarajevo
         * Europe/Saratov
         * Europe/Simferopol
         * Europe/Skopje
         * Europe/Sofia
         * Europe/Stockholm
         * Europe/Tallinn
         * Europe/Tirane
         * Europe/Tiraspol
         * Europe/Ulyanovsk
         * Europe/Uzhgorod
         * Europe/Vaduz
         * Europe/Vatican
         * Europe/Vienna
         * Europe/Vilnius
         * Europe/Volgograd
         * Europe/Warsaw
         * Europe/Zagreb
         * Europe/Zaporozhye
         * Europe/Zurich
         * GB
         * GB-Eire
         * GMT
         * GMT+0
         * GMT-0
         * GMT0
         * Greenwich
         * HST
         * Hongkong
         * Iceland
         * Indian/Antananarivo
         * Indian/Chagos
         * Indian/Christmas
         * Indian/Cocos
         * Indian/Comoro
         * Indian/Kerguelen
         * Indian/Mahe
         * Indian/Maldives
         * Indian/Mauritius
         * Indian/Mayotte
         * Indian/Reunion
         * Iran
         * Israel
         * Jamaica
         * Japan
         * Kwajalein
         * Libya
         * MET
         * MST
         * MST7MDT
         * Mexico/BajaNorte
         * Mexico/BajaSur
         * Mexico/General
         * NZ
         * NZ-CHAT
         * Navajo
         * PRC
         * PST8PDT
         * Pacific/Apia
         * Pacific/Auckland
         * Pacific/Bougainville
         * Pacific/Chatham
         * Pacific/Chuuk
         * Pacific/Easter
         * Pacific/Efate
         * Pacific/Enderbury
         * Pacific/Fakaofo
         * Pacific/Fiji
         * Pacific/Funafuti
         * Pacific/Galapagos
         * Pacific/Gambier
         * Pacific/Guadalcanal
         * Pacific/Guam
         * Pacific/Honolulu
         * Pacific/Johnston
         * Pacific/Kiritimati
         * Pacific/Kosrae
         * Pacific/Kwajalein
         * Pacific/Majuro
         * Pacific/Marquesas
         * Pacific/Midway
         * Pacific/Nauru
         * Pacific/Niue
         * Pacific/Norfolk
         * Pacific/Noumea
         * Pacific/Pago_Pago
         * Pacific/Palau
         * Pacific/Pitcairn
         * Pacific/Pohnpei
         * Pacific/Ponape
         * Pacific/Port_Moresby
         * Pacific/Rarotonga
         * Pacific/Saipan
         * Pacific/Samoa
         * Pacific/Tahiti
         * Pacific/Tarawa
         * Pacific/Tongatapu
         * Pacific/Truk
         * Pacific/Wake
         * Pacific/Wallis
         * Pacific/Yap
         * Poland
         * Portugal
         * ROC
         * ROK
         * Singapore
         * Turkey
         * UCT
         * US/Alaska
         * US/Aleutian
         * US/Arizona
         * US/Central
         * US/East-Indiana
         * US/Eastern
         * US/Hawaii
         * US/Indiana-Starke
         * US/Michigan
         * US/Mountain
         * US/Pacific
         * US/Samoa
         * UTC
         * Universal
         * W-SU
         * WET
         * Zulu
         *
         * @param areaId ex. Africa/Cairo
         * @return
         * @throws Exception
         */

        TimeZone timeZone = TimeZone.getTimeZone(areaId);
        if (timeZone == null) {
            throw new Exception("There is no area id with: \"" + areaId + "\"");
        } else {
            this.calendar.setTimeZone(timeZone);
        }
        return this;
    }

    public DateOp setTimeZoneToUTC() {
        try {
            return setTimeZone("Etc/UTC");
        } catch (Exception e) {
            setTimeZone(0);
        }
        return this;
    }

    public DateOp setTimeToUTC() {
        long t = getUTCTimeInMillis();
        return setTimeInMillis(t);
    }

    public DateOp setTimeToLocale() {
        long t = calendar.getTimeInMillis() + TimeZone.getDefault().getRawOffset();
        return setTimeInMillis(t);
    }

    public DateOp setTimeInMillis(long timeInMillis) {
        calendar.setTimeInMillis(timeInMillis);
        return this;
    }

    public DateOp setDate(int day, int month, int year) {
        calendar.set(year, month - 1, day);
        return this;
    }

    public DateOp setTime(int hourOfDay, int minute, int second) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);

        return this;
    }

    //--------------------------------------------------------------------------------------------//

    public Calendar getDate() {
        return calendar;
    }

    public long getTimeInMillis() {
        return calendar.getTimeInMillis();
    }

    public long getUTCTimeInMillis() {
        return calendar.getTimeInMillis() - TimeZone.getDefault().getRawOffset();
    }

    public int getHour24() {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinute() {
        return calendar.get(Calendar.MINUTE);
    }

    public int getSeconds() {
        return calendar.get(Calendar.SECOND);
    }

    public int getDay() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public String getDayName() {
        return DateOp.getDayName(calendar.get(Calendar.DAY_OF_WEEK), currentLocale.getLanguage().toLowerCase().contains("en"));
    }

    public String getDayNameInEnglish() {
        return DateOp.getDayName(calendar.get(Calendar.DAY_OF_WEEK), true);
    }

    public int getDayOfWeek() {
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public int getMonth() {
        return calendar.get(Calendar.MONTH) + 1;
    }

    public String getMonthName() {
        return DateOp.getMonthName(calendar.get(Calendar.MONTH), currentLocale.getLanguage().toLowerCase().contains("en"));
    }

    public String getMonthNameInEnglish() {
        return DateOp.getMonthName(calendar.get(Calendar.MONTH), true);
    }

    public int getYear() {
        return calendar.get(Calendar.YEAR);
    }

    public int getLastDayOfMonth() {
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    //--------------------------------------------------------------------------------------------//

    public String formatDate(String pattern, boolean forceEnglish) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, forceEnglish ? Locale.ENGLISH : currentLocale);
        String format = sdf.format(calendar.getTime());
        return format;
    }


    //--------------------------------------------------------------------------------------------//

    public DateOp subtract(Calendar subtrahend) {
        int Sy = subtrahend.get(Calendar.YEAR);
        int Sm = subtrahend.get(Calendar.MONTH);
        int Sd = subtrahend.get(Calendar.DAY_OF_MONTH);

        calendar.add(Calendar.DAY_OF_MONTH, -Sd);
        calendar.add(Calendar.MONTH, -Sm);
        calendar.add(Calendar.YEAR, -Sy);

        return this;
    }

    public DateOp increaseYears(int amount) {
        calendar.add(Calendar.YEAR, amount);

        return this;
    }

    public DateOp decreaseYears(int amount) {
        calendar.add(Calendar.YEAR, -amount);

        return this;
    }

    public DateOp increaseMonths(int amount) {
        calendar.add(Calendar.MONTH, amount);

        return this;
    }

    public DateOp decreaseMonths(int amount) {
        calendar.add(Calendar.MONTH, -amount);

        return this;
    }

    public DateOp increaseDays(int amount) {
        calendar.add(Calendar.DAY_OF_MONTH, amount);

        return this;
    }

    public DateOp decreaseDays(int amount) {
        calendar.add(Calendar.DAY_OF_MONTH, -amount);

        return this;
    }

    public DateOp increaseHours(int amount) {
        calendar.add(Calendar.HOUR_OF_DAY, amount);

        return this;
    }

    public DateOp decreaseHours(int amount) {
        calendar.add(Calendar.HOUR_OF_DAY, -amount);

        return this;
    }

    public DateOp increaseMinutes(int amount) {
        calendar.add(Calendar.MINUTE, amount);

        return this;
    }

    public DateOp decreaseMinutes(int amount) {
        calendar.add(Calendar.MINUTE, -amount);

        return this;
    }

    public DateOp increaseSeconds(int amount) {
        calendar.add(Calendar.SECOND, amount);

        return this;
    }

    public DateOp decreaseSeconds(int amount) {
        calendar.add(Calendar.SECOND, -amount);

        return this;
    }


    public int computeDaysDifference(long firstDay, long secondDay) {
        double f = (secondDay - firstDay) / (double) DateOp.ONE_DAY_MILLIS;
        int days = 0;
        if (f > 0) days = (int) f;

        days += ((f - days) > 0 ? 1 : 0);

        return days;
    }

    //--------------------------------------------------------------------------------------------//

    public void showDateThenTimePickerDialog(final Context context, final DatePickingListener listener) {
        showDateThenTimePickerDialog(context, null, null, listener);
    }

    public void showDateThenTimePickerDialog(final Context context, @Nullable Long minDate, @Nullable Long maxDate, final DatePickingListener listener) {
        showDatePickerDialog(context, minDate, maxDate, new DatePickingListener() {
            @Override
            public void onDatePicked(DateOp dateOp) {
                showTimePickerDialog(context, listener);
            }
        });
    }


    public void showDatePickerDialog(Context context, DatePickingListener listener) {
        showDatePickerDialog(context, null, null, listener);
    }

    public void showDatePickerDialog(Context context, @Nullable Long minDate, @Nullable Long maxDate, DatePickingListener listener) {
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        int m = calendar.get(Calendar.MONTH);
        int y = calendar.get(Calendar.YEAR);

        showDatePickerDialog(context, d, m, y, minDate, maxDate, listener);
    }

    public void showDatePickerDialog(Context context, int day, int month, int year, @Nullable Long minDate, @Nullable Long maxDate, final DatePickingListener listener) {
        DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if (listener != null) {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    listener.onDatePicked(DateOp.this);
                }
            }
        }, year, month, day);

        if (minDate != null) {
            dialog.getDatePicker().setMinDate(minDate);
        }

        if (maxDate != null) {
            dialog.getDatePicker().setMaxDate(maxDate);
        }

        dialog.show();
    }


    public void showTimePickerDialog(Context context, final DatePickingListener listener) {
        showTimePickerDialog(context, false, listener);
    }

    public void showTimePickerDialog(Context context, boolean is24HourView, final DatePickingListener listener) {
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                if (listener != null) {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);

                    listener.onDatePicked(DateOp.this);
                }
            }
        }, h, m, is24HourView);

        dialog.show();
    }

    public interface DatePickingListener {
        void onDatePicked(DateOp dateOp);//String dayName, String dateLong, String dateShort);
    }


    //--------------------------------------------------------------------------------------------//

    public static String getDayName(int day, boolean en) {
        try {
            switch (day) {
                case Calendar.SUNDAY:
                    return en ? "Sunday" : "الأحد";

                case Calendar.MONDAY:
                    return en ? "Monday" : "الإثنين";

                case Calendar.TUESDAY:
                    return en ? "Tuesday" : "الثلاثاء";

                case Calendar.WEDNESDAY:
                    return en ? "Wednesday" : "الأربعاء";

                case Calendar.THURSDAY:
                    return en ? "Thursday" : "الخميس";

                case Calendar.FRIDAY:
                    return en ? "Friday" : "الجمعة";

                case Calendar.SATURDAY:
                    return en ? "Saturday" : "السبت";
            }
        } catch (Exception e) {
        }

        return "DOW:" + day;
    }

    public static String getMonthName(int month, boolean en) {
        try {
            switch (month) {
                case Calendar.JANUARY:
                    return en ? "January" : "يناير";
                case Calendar.FEBRUARY:
                    return en ? "February" : "فبراير";
                case Calendar.MARCH:
                    return en ? "March" : "مارس";
                case Calendar.APRIL:
                    return en ? "April" : "إبريل";
                case Calendar.MAY:
                    return en ? "May" : "مايو";
                case Calendar.JUNE:
                    return en ? "June" : "يونيو";
                case Calendar.JULY:
                    return en ? "July" : "يوليو";
                case Calendar.AUGUST:
                    return en ? "August" : "أغسطس";
                case Calendar.SEPTEMBER:
                    return en ? "September" : "سبتمبر";
                case Calendar.OCTOBER:
                    return en ? "October" : "أكتوبر";
                case Calendar.NOVEMBER:
                    return en ? "November" : "نوفمبر";
                case Calendar.DECEMBER:
                    return en ? "December" : "ديسمبر";
            }
        } catch (Exception e) {
        }

        return "Month:" + (month + 1);
    }

    //--------------------------------------------------------------------------------------------//

    public static Long parseDate(String time, String pattern) throws Exception {
        Date date = null;

        try {
            date = new SimpleDateFormat(pattern, Locale.ENGLISH).parse(time);
        } catch (Exception e1) {
            time = insureDateSeparatorIsDash(time);
            date = new SimpleDateFormat(pattern, Locale.ENGLISH).parse(time);
        }

        return date.getTime();
    }

    public static Long tryParseDate(String date) {
        String date1 =
                date.replace("'T'", " ")
                        .replace("T", " ");

        String[] patterns;
        if ("yyyy-MM-dd HH:mm:ss".length() < date1.length()) {
            patterns = new String[]{
                    PATTERN_yyyy_MM_dd_HH_mm_ss_SSSXXX,
                    PATTERN_yyyy_MM_dd_HH_mm_ss_SSSZ,
                    PATTERN_yyyy_MM_dd_HH_mm_ss_SSS_z,
                    PATTERN_yyyy_MM_dd_HH_mm_ssXXX,
                    PATTERN_yyyy_MM_dd_HH_mm_ssZ,
                    PATTERN_yyyy_MM_dd_HH_mm_ss_z,

                    PATTERN_dd_MM_yyyy_HH_mm_ss_SSSXXX,
                    PATTERN_dd_MM_yyyy_HH_mm_ss_SSSZ,
                    PATTERN_dd_MM_yyyy_HH_mm_ss_SSS_z,
                    PATTERN_dd_MM_yyyy_HH_mm_ssXXX,
                    PATTERN_dd_MM_yyyy_HH_mm_ssZ,
                    PATTERN_dd_MM_yyyy_HH_mm_ss_z,
            };
        } else {
            patterns = new String[]{
                    PATTERN_yyyy_MM_dd_HH_mm_ss,
                    PATTERN_yyyy_MM_dd_HH_mm,
                    PATTERN_yyyy_MM_dd,

                    PATTERN_dd_MM_yyyy_HH_mm_ss,
                    PATTERN_dd_MM_yyyy_HH_mm,
                    PATTERN_dd_MM_yyyy,

                    PATTERN_HH_mm_ss_SSS_XXX,
                    PATTERN_HH_mm_ss_SSS_Z,
                    PATTERN_HH_mm_ss_SSS_z,
                    PATTERN_HH_mm_ss_SSS,
                    PATTERN_hh_mm_ss_a,
                    PATTERN_HH_mm_ss,
                    PATTERN_hh_mm_a,
                    PATTERN_HH_mm
            };

        }

        for (int i = 0; i < patterns.length; i++) {
            try {
                return parseDate(date1, patterns[i]);
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    public static String insureDateSeparatorIsDash(String date) {
        if (date != null && (date.contains("/") || date.contains("\\") || date.contains("."))) {
            date = date
                    .replace("/", "-")
                    .replace(".", "-");
        }
        return date;
    }

    public static String tryFormat(String originDate, String toPattern, String defaultDate, boolean forceEnglish) {
        try {
            DateOp dateOp = DateOp.getInstance(originDate, true);
            return dateOp.formatDate(toPattern, forceEnglish);
        } catch (Exception e) {
            return defaultDate;
        }
    }

    //--------------------------------------------------------------------------------------------//

    public static String convertTimeToString(long timeInMillis, boolean en) {
        StringBuilder string = new StringBuilder();

        long[] standardIntervals = new long[]{
                DateOp.ONE_DAY_MILLIS,
                DateOp.ONE_HOUR_MILLIS,
                DateOp.ONE_MINUTE_MILLIS,
                DateOp.ONE_SECOND_MILLIS
        };
        String[] intervalNamesEn = new String[]{
                "day",
                "hour",
                "minute",
                "second"
        };
        String[][] intervalNamesAr = new String[][]{
                new String[]{"يوم", "يومان", "أيام"},
                new String[]{"ساعة", "ساعتان", "ساعات"},
                new String[]{"دقيقة", "دقيقتان", "دقائق"},
                new String[]{"ثانية", "ثانيتين", "ثواني"}
        };

        boolean isNeg = false;

        if (timeInMillis < 0) {
            isNeg = true;
            timeInMillis *= -1;
        }

        for (int i = 0; i < standardIntervals.length; i++) {
            int x = (int) (timeInMillis / standardIntervals[i]);

            if (x > 0) {
                if (string.length() > 0) {
                    if (en)
                        string.append(", ");
                    else
                        string.append(" و");
                }

                if (en) {
                    string.append(String.format(Locale.ENGLISH, "%d", x));
                    string.append(" ").append(intervalNamesEn[i]);
                    if (x > 1) {
                        string.append("s");
                    }
                } else {
                    if (x == 1) {
                        string.append(" ").append(intervalNamesAr[i][0]);
                    } else if (x == 2) {
                        string.append(" ").append(intervalNamesAr[i][1]);
                    } else {
                        string.append(String.format(new Locale("ar"), "%d", x));
                        string.append(" ").append(intervalNamesAr[i][2]);
                    }
                }

                timeInMillis -= x * standardIntervals[i];
            }
        }

        return (isNeg ? "(-) " : "(+) ") + string.toString();
    }

}
