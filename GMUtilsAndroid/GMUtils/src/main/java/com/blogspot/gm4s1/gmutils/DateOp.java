package com.blogspot.gm4s1.gmutils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
public class DateOp implements Serializable {
    public static final String PATTERN_HH_mm = "HH:mm";
    public static final String PATTERN_HH_mm_ss = "HH:mm:ss";
    public static final String PATTERN_dd_MM_yyyy = "dd-MM-yyyy";
    public static final String PATTERN_dd_MM_yyyy_HH_mm = "dd-MM-yyyy HH:mm";
    public static final String PATTERN_dd_MM_yyyy_HH_mm_ss = "dd-MM-yyyy HH:mm:ss";
    public static final String PATTERN_dd_MM_yyyy_HH_mm_ssz = "dd-MM-yyyy HH:mm:ssz";
    public static final String PATTERN_yyyy_MM_dd = "yyyy-MM-dd";
    public static final String PATTERN_yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_yyyy_MM_dd_HH_mm_ssz = "yyyy-MM-dd HH:mm:ssz";
    public static final String PATTERN_yyyy_MM_dd_HH_mm_ss_Sz = "yyyy-MM-dd HH:mm:ss.Sz";
    public static final String PATTERN_yyyy_MM_dd_T_HH_mm_ss = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String PATTERN_yyyy_MM_dd_T_HH_mm_ssz = "yyyy-MM-dd'T'HH:mm:ssz";
    public static final String PATTERN_yyyy_MM_dd_T_HH_mm_ss_SSS_z = "yyyy-MM-dd'T'HH:mm:ss.SSSz";
    public static final String PATTERN_dd_MMM_E_yyyy = "dd-MMM, E yyyy";
    public static final String PATTERN_dd_MMM_E_yyyy_HH_mm_ss = "dd-MMM, E yyyy HH:mm:ss";

    public static final int STYLE_DName_dd_MName_yyyy_HH_mm_ss = -1;    //Saturday 01 January 2020, 20:00:ss
    public static final int STYLE_DName_dd_MName_yyyy_HH_mm = 0;        //Saturday 01 January 2020, 20:00
    public static final int STYLE_DName_dd_MName_yyyy = 1;              //Saturday 01 January 2020
    public static final int STYLE_DName_dd_MName = 2;                   //Saturday 01 January
    public static final int STYLE_MName_dd_DHName_yyyy = 3;             //January 01, Sat 2020
    public static final int STYLE_MName_dd_yyyy = 4;                    //January 01, 2020
    public static final int STYLE_dd_MHName_yyyy = 5;                   //01-Jan-2020
    public static final int STYLE_dd_MHName_DHName_NL_yyyy = 6;         //01-Jan, Sat\n2020
    public static final int STYLE_dd_MHName_yyyy2 = 7;                  //01 Jan, 2020
    public static final int STYLE_dd_MM_yyyy = 8;                       //01-01-2020
    public static final int STYLE_yyyy_MM_dd = 9;                       //2020-01-01
    public static final int STYLE_yyyy_MM_dd_HH_mm_ss = 10;             //2020-01-01 20:00:00
    public static final int STYLE_dd_MM_yyyy_HH_mm_ss = 11;             //01-01-2020 20:00:00
    public static final int STYLE_HH_mm_MHName_dd_NL_DHName_yyyy = 12;  //20:00, Jan 01\nSat, 2020
    public static final int STYLE_HH_mm_MHName_dd_DHName_yyyy2 = 13;    //20:00, Jan 01 Sat, 2020
    public static final int STYLE_HH_mm = 14;                           //20:00

    public static final long ONE_DAY_MILLIS = 86_400_000L; // (1d * 24h * 60m * 60s * 1000ms);
    public static final long ONE_HOUR_MILLIS = 3_600_000L; // (0d * 01h * 60m * 60s * 1000ms);
    public static final long ONE_MINUTE_MILLIS = 60_000L; //  (0d * 00h * 01m * 60s * 1000ms);
    public static final long TEN_SECOND_MILLIS = 10_000L; //  (0d * 00h * 00m * 10s * 1000ms);
    public static final long ONE_SECOND_MILLIS = 1_000L; //   (0d * 00h * 00m * 01s * 1000ms);

    private Calendar calendar;
    private Locale currentLocale = Locale.getDefault();

    public static DateOp getInstance() {
        return new DateOp(Calendar.getInstance());
    }

    public static DateOp getInstance(long timeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);

        return new DateOp(calendar);
    }

    public static DateOp getInstance(String time, String pattern) {
        return getInstance(time, pattern, false);
    }

    public static DateOp getInstance(String time, String pattern, boolean throwException) {
        DateOp dateOp = new DateOp(Calendar.getInstance());
        return dateOp.parseDate(time, pattern, throwException);
    }

    public static DateOp getInstance(String time, boolean throwException) {
        DateOp dateOp = new DateOp(Calendar.getInstance());
        return dateOp.tryParseDate(time, throwException);
    }

    private DateOp(Calendar calendar) {
        this.calendar = calendar;
    }

    @Override
    public String toString() {
        return String.format(
                currentLocale,
                "%s %02d %s %04d, %02d:%02d:%02d / %d",
                getDayName(),
                getDay(),
                getMonthName(),
                getYear(),

                getHour24(),
                getMinute(),
                getSeconds(),

                getTimeInMillis()
        );
    }

    public String toString(int style) {

        String format;
        Object[] args;
        Locale lang = currentLocale;

        switch (style) {
            case STYLE_DName_dd_MName_yyyy:
                format = "%s %02d %s %04d";
                args = new Object[]{getDayName(), getDay(), getMonthName(), getYear()};
                break;

            case STYLE_dd_MHName_yyyy:
                format = "%02d-%s-%04d";
                args = new Object[]{getDay(), getMonthName().substring(0, 3), getYear()};
                break;

            case STYLE_dd_MHName_yyyy2:
                format = "%02d %s, %04d";
                args = new Object[]{getDay(), getMonthName().substring(0, 3), getYear()};
                break;

            case STYLE_DName_dd_MName:
                format = "%s %02d %s";
                args = new Object[]{getDayName(), getDay(), getMonthName()};
                break;

            case STYLE_HH_mm:
                format = "%02d:%02d";
                args = new Object[]{getHour24(), getMinute()};
                break;

            case STYLE_DName_dd_MName_yyyy_HH_mm:
                format = "%s %02d %s %04d, %02d:%02d";
                args = new Object[]{getDayName(), getDay(), getMonthName(), getYear(), getHour24(), getMinute()};
                break;

            case STYLE_DName_dd_MName_yyyy_HH_mm_ss:
                format = "%s %02d %s %04d, %02d:%02d:%02d";
                args = new Object[]{getDayName(), getDay(), getMonthName(), getYear(), getHour24(), getMinute(), getSeconds()};
                break;

            case STYLE_dd_MM_yyyy:
                format = "%02d-%02d-%04d";
                args = new Object[]{getDay(), getMonth(), getYear()};
                break;

            case STYLE_dd_MM_yyyy_HH_mm_ss:
                format = "%02d-%02d-%04d %02d:%02d:%02d";
                args = new Object[]{getDay(), getMonth(), getYear(), getHour24(), getMinute(), getSeconds()};
                break;

            case STYLE_yyyy_MM_dd_HH_mm_ss:
                format = "%04d-%02d-%02d %02d:%02d:%02d";
                args = new Object[]{getYear(), getMonth(), getDay(), getHour24(), getMinute(), getSeconds()};
                break;

            case STYLE_yyyy_MM_dd:
                format = "%04d-%02d-%02d";
                args = new Object[]{getYear(), getMonth(), getDay()};
                break;

            case STYLE_MName_dd_DHName_yyyy:
                format = "%s %02d, %s %04d";
                args = new Object[]{getMonthName(), getDay(), getDayName().substring(0, 3), getYear()};
                break;

            case STYLE_HH_mm_MHName_dd_NL_DHName_yyyy:
                format = "%02d:%02d, %s %02d\n%s, %04d";
                args = new Object[]{getHour24(), getMinute(), getMonthName().substring(0, 3), getDay(), getDayName().substring(0, 3), getYear()};
                break;

            case STYLE_HH_mm_MHName_dd_DHName_yyyy2:
                format = "%02d:%02d, %s %02d %s, %04d";
                args = new Object[]{getHour24(), getMinute(), getMonthName().substring(0, 3), getDay(), getDayName().substring(0, 3), getYear()};
                break;

            case STYLE_dd_MHName_DHName_NL_yyyy://12-Jan, Sat\n2020
                format = "%02d-%s, %s\n%04d";
                args = new Object[]{getDay(), getMonthName().substring(0, 3), getDayName().substring(0, 3), getYear()};
                break;

            case STYLE_MName_dd_yyyy://January 01, 2020
                format = "%s %02d, %04d";
                args = new Object[]{getMonthName(), getDay(), getYear()};
                break;

            default:
                format = toString();
                args = null;
                break;
        }

        return String.format(lang, format, args);
    }

    //--------------------------------------------------------------------------------------------//

    public DateOp setCurrentLocaleToArabic() {
        return setCurrentLocale(new Locale("ar"));
    }

    public DateOp setCurrentLocaleToEnglish() {
        return setCurrentLocale(Locale.ENGLISH);
    }

    public DateOp setCurrentLocale(Locale currentLocale) {
        this.currentLocale = currentLocale;
        return this;
    }

    //--------------------------------------------------------------------------------------------//

    public DateOp setTimeInMillis(long timeInMillis) {
        calendar.setTimeInMillis(timeInMillis);
        return this;
    }

    public DateOp setTimeZone(int zone) {
        TimeZone aDefault = TimeZone.getDefault();
        aDefault.setRawOffset((int) (zone * ONE_HOUR_MILLIS));
        this.calendar.setTimeZone(aDefault);

        return this;
    }

    /**
     Africa/Accra
     Africa/Addis_Ababa
     Africa/Algiers
     Africa/Asmara
     Africa/Asmera
     Africa/Bamako
     Africa/Bangui
     Africa/Banjul
     Africa/Bissau
     Africa/Blantyre
     Africa/Brazzaville
     Africa/Bujumbura
     Africa/Cairo
     Africa/Casablanca
     Africa/Ceuta
     Africa/Conakry
     Africa/Dakar
     Africa/Dar_es_Salaam
     Africa/Djibouti
     Africa/Douala
     Africa/El_Aaiun
     Africa/Freetown
     Africa/Gaborone
     Africa/Harare
     Africa/Johannesburg
     Africa/Juba
     Africa/Kampala
     Africa/Khartoum
     Africa/Kigali
     Africa/Kinshasa
     Africa/Lagos
     Africa/Libreville
     Africa/Lome
     Africa/Luanda
     Africa/Lubumbashi
     Africa/Lusaka
     Africa/Malabo
     Africa/Maputo
     Africa/Maseru
     Africa/Mbabane
     Africa/Mogadishu
     Africa/Monrovia
     Africa/Nairobi
     Africa/Ndjamena
     Africa/Niamey
     Africa/Nouakchott
     Africa/Ouagadougou
     Africa/Porto-Novo
     Africa/Sao_Tome
     Africa/Timbuktu
     Africa/Tripoli
     Africa/Tunis
     Africa/Windhoek
     America/Adak
     America/Anchorage
     America/Anguilla
     America/Antigua
     America/Araguaina
     America/Argentina/Buenos_Aires
     America/Argentina/Catamarca
     America/Argentina/ComodRivadavia
     America/Argentina/Cordoba
     America/Argentina/Jujuy
     America/Argentina/La_Rioja
     America/Argentina/Mendoza
     America/Argentina/Rio_Gallegos
     America/Argentina/Salta
     America/Argentina/San_Juan
     America/Argentina/San_Luis
     America/Argentina/Tucuman
     America/Argentina/Ushuaia
     America/Aruba
     America/Asuncion
     America/Atikokan
     America/Atka
     America/Bahia
     America/Bahia_Banderas
     America/Barbados
     America/Belem
     America/Belize
     America/Blanc-Sablon
     America/Boa_Vista
     America/Bogota
     America/Boise
     America/Buenos_Aires
     America/Cambridge_Bay
     America/Campo_Grande
     America/Cancun
     America/Caracas
     America/Catamarca
     America/Cayenne
     America/Cayman
     America/Chicago
     America/Chihuahua
     America/Coral_Harbour
     America/Cordoba
     America/Costa_Rica
     America/Creston
     America/Cuiaba
     America/Curacao
     America/Danmarkshavn
     America/Dawson
     America/Dawson_Creek
     America/Denver
     America/Detroit
     America/Dominica
     America/Edmonton
     America/Eirunepe
     America/El_Salvador
     America/Ensenada
     America/Fort_Nelson
     America/Fort_Wayne
     America/Fortaleza
     America/Glace_Bay
     America/Godthab
     America/Goose_Bay
     America/Grand_Turk
     America/Grenada
     America/Guadeloupe
     America/Guatemala
     America/Guayaquil
     America/Guyana
     America/Halifax
     America/Havana
     America/Hermosillo
     America/Indiana/Indianapolis
     America/Indiana/Knox
     America/Indiana/Marengo
     America/Indiana/Petersburg
     America/Indiana/Tell_City
     America/Indiana/Vevay
     America/Indiana/Vincennes
     America/Indiana/Winamac
     America/Indianapolis
     America/Inuvik
     America/Iqaluit
     America/Jamaica
     America/Jujuy
     America/Juneau
     America/Kentucky/Louisville
     America/Kentucky/Monticello
     America/Knox_IN
     America/Kralendijk
     America/La_Paz
     America/Lima
     America/Los_Angeles
     America/Louisville
     America/Lower_Princes
     America/Maceio
     America/Managua
     America/Manaus
     America/Marigot
     America/Martinique
     America/Matamoros
     America/Mazatlan
     America/Mendoza
     America/Menominee
     America/Merida
     America/Metlakatla
     America/Mexico_City
     America/Miquelon
     America/Moncton
     America/Monterrey
     America/Montevideo
     America/Montreal
     America/Montserrat
     America/Nassau
     America/New_York
     America/Nipigon
     America/Nome
     America/Noronha
     America/North_Dakota/Beulah
     America/North_Dakota/Center
     America/North_Dakota/New_Salem
     America/Ojinaga
     America/Panama
     America/Pangnirtung
     America/Paramaribo
     America/Phoenix
     America/Port-au-Prince
     America/Port_of_Spain
     America/Porto_Acre
     America/Porto_Velho
     America/Puerto_Rico
     America/Punta_Arenas
     America/Rainy_River
     America/Rankin_Inlet
     America/Recife
     America/Regina
     America/Resolute
     America/Rio_Branco
     America/Rosario
     America/Santa_Isabel
     America/Santarem
     America/Santiago
     America/Santo_Domingo
     America/Sao_Paulo
     America/Scoresbysund
     America/Shiprock
     America/Sitka
     America/St_Barthelemy
     America/St_Johns
     America/St_Kitts
     America/St_Lucia
     America/St_Thomas
     America/St_Vincent
     America/Swift_Current
     America/Tegucigalpa
     America/Thule
     America/Thunder_Bay
     America/Tijuana
     America/Toronto
     America/Tortola
     America/Vancouver
     America/Virgin
     America/Whitehorse
     America/Winnipeg
     America/Yakutat
     America/Yellowknife
     Antarctica/Casey
     Antarctica/Davis
     Antarctica/DumontDUrville
     Antarctica/Macquarie
     Antarctica/Mawson
     Antarctica/McMurdo
     Antarctica/Palmer
     Antarctica/Rothera
     Antarctica/South_Pole
     Antarctica/Syowa
     Antarctica/Troll
     Antarctica/Vostok
     Arctic/Longyearbyen
     Asia/Aden
     Asia/Almaty
     Asia/Amman
     Asia/Anadyr
     Asia/Aqtau
     Asia/Aqtobe
     Asia/Ashgabat
     Asia/Ashkhabad
     Asia/Atyrau
     Asia/Baghdad
     Asia/Bahrain
     Asia/Baku
     Asia/Bangkok
     Asia/Barnaul
     Asia/Beirut
     Asia/Bishkek
     Asia/Brunei
     Asia/Calcutta
     Asia/Chita
     Asia/Choibalsan
     Asia/Chongqing
     Asia/Chungking
     Asia/Colombo
     Asia/Dacca
     Asia/Damascus
     Asia/Dhaka
     Asia/Dili
     Asia/Dubai
     Asia/Dushanbe
     Asia/Famagusta
     Asia/Gaza
     Asia/Hanoi
     Asia/Harbin
     Asia/Hebron
     Asia/Ho_Chi_Minh
     Asia/Hong_Kong
     Asia/Hovd
     Asia/Irkutsk
     Asia/Istanbul
     Asia/Jakarta
     Asia/Jayapura
     Asia/Jerusalem
     Asia/Kabul
     Asia/Kamchatka
     Asia/Karachi
     Asia/Kashgar
     Asia/Kathmandu
     Asia/Katmandu
     Asia/Khandyga
     Asia/Kolkata
     Asia/Krasnoyarsk
     Asia/Kuala_Lumpur
     Asia/Kuching
     Asia/Kuwait
     Asia/Macao
     Asia/Macau
     Asia/Magadan
     Asia/Makassar
     Asia/Manila
     Asia/Muscat
     Asia/Nicosia
     Asia/Novokuznetsk
     Asia/Novosibirsk
     Asia/Omsk
     Asia/Oral
     Asia/Phnom_Penh
     Asia/Pontianak
     Asia/Pyongyang
     Asia/Qatar
     Asia/Qyzylorda
     Asia/Rangoon
     Asia/Riyadh
     Asia/Saigon
     Asia/Sakhalin
     Asia/Samarkand
     Asia/Seoul
     Asia/Shanghai
     Asia/Singapore
     Asia/Srednekolymsk
     Asia/Taipei
     Asia/Tashkent
     Asia/Tbilisi
     Asia/Tehran
     Asia/Tel_Aviv
     Asia/Thimbu
     Asia/Thimphu
     Asia/Tokyo
     Asia/Tomsk
     Asia/Ujung_Pandang
     Asia/Ulaanbaatar
     Asia/Ulan_Bator
     Asia/Urumqi
     Asia/Ust-Nera
     Asia/Vientiane
     Asia/Vladivostok
     Asia/Yakutsk
     Asia/Yangon
     Asia/Yekaterinburg
     Asia/Yerevan
     Atlantic/Azores
     Atlantic/Bermuda
     Atlantic/Canary
     Atlantic/Cape_Verde
     Atlantic/Faeroe
     Atlantic/Faroe
     Atlantic/Jan_Mayen
     Atlantic/Madeira
     Atlantic/Reykjavik
     Atlantic/South_Georgia
     Atlantic/St_Helena
     Atlantic/Stanley
     Australia/ACT
     Australia/Adelaide
     Australia/Brisbane
     Australia/Broken_Hill
     Australia/Canberra
     Australia/Currie
     Australia/Darwin
     Australia/Eucla
     Australia/Hobart
     Australia/LHI
     Australia/Lindeman
     Australia/Lord_Howe
     Australia/Melbourne
     Australia/NSW
     Australia/North
     Australia/Perth
     Australia/Queensland
     Australia/South
     Australia/Sydney
     Australia/Tasmania
     Australia/Victoria
     Australia/West
     Australia/Yancowinna
     Brazil/Acre
     Brazil/DeNoronha
     Brazil/East
     Brazil/West
     CET
     CST6CDT
     Canada/Atlantic
     Canada/Central
     Canada/Eastern
     Canada/Mountain
     Canada/Newfoundland
     Canada/Pacific
     Canada/Saskatchewan
     Canada/Yukon
     Chile/Continental
     Chile/EasterIsland
     Cuba
     EET
     EST
     EST5EDT
     Egypt
     Eire
     Etc/GMT
     Etc/GMT+0
     Etc/GMT+1
     Etc/GMT+10
     Etc/GMT+11
     Etc/GMT+12
     Etc/GMT+2
     Etc/GMT+3
     Etc/GMT+4
     Etc/GMT+5
     Etc/GMT+6
     Etc/GMT+7
     Etc/GMT+8
     Etc/GMT+9
     Etc/GMT-0
     Etc/GMT-1
     Etc/GMT-10
     Etc/GMT-11
     Etc/GMT-12
     Etc/GMT-13
     Etc/GMT-14
     Etc/GMT-2
     Etc/GMT-3
     Etc/GMT-4
     Etc/GMT-5
     Etc/GMT-6
     Etc/GMT-7
     Etc/GMT-8
     Etc/GMT-9
     Etc/GMT0
     Etc/Greenwich
     Etc/UCT
     Etc/UTC
     Etc/Universal
     Etc/Zulu
     Europe/Amsterdam
     Europe/Andorra
     Europe/Astrakhan
     Europe/Athens
     Europe/Belfast
     Europe/Belgrade
     Europe/Berlin
     Europe/Bratislava
     Europe/Brussels
     Europe/Bucharest
     Europe/Budapest
     Europe/Busingen
     Europe/Chisinau
     Europe/Copenhagen
     Europe/Dublin
     Europe/Gibraltar
     Europe/Guernsey
     Europe/Helsinki
     Europe/Isle_of_Man
     Europe/Istanbul
     Europe/Jersey
     Europe/Kaliningrad
     Europe/Kiev
     Europe/Kirov
     Europe/Lisbon
     Europe/Ljubljana
     Europe/London
     Europe/Luxembourg
     Europe/Madrid
     Europe/Malta
     Europe/Mariehamn
     Europe/Minsk
     Europe/Monaco
     Europe/Moscow
     Europe/Nicosia
     Europe/Oslo
     Europe/Paris
     Europe/Podgorica
     Europe/Prague
     Europe/Riga
     Europe/Rome
     Europe/Samara
     Europe/San_Marino
     Europe/Sarajevo
     Europe/Saratov
     Europe/Simferopol
     Europe/Skopje
     Europe/Sofia
     Europe/Stockholm
     Europe/Tallinn
     Europe/Tirane
     Europe/Tiraspol
     Europe/Ulyanovsk
     Europe/Uzhgorod
     Europe/Vaduz
     Europe/Vatican
     Europe/Vienna
     Europe/Vilnius
     Europe/Volgograd
     Europe/Warsaw
     Europe/Zagreb
     Europe/Zaporozhye
     Europe/Zurich
     GB
     GB-Eire
     GMT
     GMT+0
     GMT-0
     GMT0
     Greenwich
     HST
     Hongkong
     Iceland
     Indian/Antananarivo
     Indian/Chagos
     Indian/Christmas
     Indian/Cocos
     Indian/Comoro
     Indian/Kerguelen
     Indian/Mahe
     Indian/Maldives
     Indian/Mauritius
     Indian/Mayotte
     Indian/Reunion
     Iran
     Israel
     Jamaica
     Japan
     Kwajalein
     Libya
     MET
     MST
     MST7MDT
     Mexico/BajaNorte
     Mexico/BajaSur
     Mexico/General
     NZ
     NZ-CHAT
     Navajo
     PRC
     PST8PDT
     Pacific/Apia
     Pacific/Auckland
     Pacific/Bougainville
     Pacific/Chatham
     Pacific/Chuuk
     Pacific/Easter
     Pacific/Efate
     Pacific/Enderbury
     Pacific/Fakaofo
     Pacific/Fiji
     Pacific/Funafuti
     Pacific/Galapagos
     Pacific/Gambier
     Pacific/Guadalcanal
     Pacific/Guam
     Pacific/Honolulu
     Pacific/Johnston
     Pacific/Kiritimati
     Pacific/Kosrae
     Pacific/Kwajalein
     Pacific/Majuro
     Pacific/Marquesas
     Pacific/Midway
     Pacific/Nauru
     Pacific/Niue
     Pacific/Norfolk
     Pacific/Noumea
     Pacific/Pago_Pago
     Pacific/Palau
     Pacific/Pitcairn
     Pacific/Pohnpei
     Pacific/Ponape
     Pacific/Port_Moresby
     Pacific/Rarotonga
     Pacific/Saipan
     Pacific/Samoa
     Pacific/Tahiti
     Pacific/Tarawa
     Pacific/Tongatapu
     Pacific/Truk
     Pacific/Wake
     Pacific/Wallis
     Pacific/Yap
     Poland
     Portugal
     ROC
     ROK
     Singapore
     Turkey
     UCT
     US/Alaska
     US/Aleutian
     US/Arizona
     US/Central
     US/East-Indiana
     US/Eastern
     US/Hawaii
     US/Indiana-Starke
     US/Michigan
     US/Mountain
     US/Pacific
     US/Samoa
     UTC
     Universal
     W-SU
     WET
     Zulu
     * @param areaId ex. Africa/Cairo
     * @return
     * @throws Exception
     */
    public DateOp setTimeZone(String areaId) throws Exception {
        TimeZone timeZone = TimeZone.getTimeZone(areaId);
        if (timeZone == null) {
            throw new Exception("There is no area id with: \"" + areaId + "\"");
        } else {
            this.calendar.setTimeZone(timeZone);
        }
        return this;
    }

    public DateOp parseDate(String date, String pattern, boolean throwException) {
        long l = 0;
        String exception = "";

        if (throwException) {
            try {
                l = DateOp.parseDateTime(date, pattern);
            } catch (Exception E) {
                throw new RuntimeException(E);
            }
        } else {
            try {
                l = DateOp.parseDateTime(date, pattern);
            } catch (Exception E) {
                exception = E.getMessage();
            }
        }

        if (l != 0) {
            this.calendar.setTimeInMillis(l);
            return this;
        } else {
            throw new IllegalArgumentException(date + " | " + pattern + "\n----\n" + exception);
        }
    }

    public DateOp tryParseDate(String date, boolean throwException) {
        String date1 =
                date.replace("'T'", " ")
                .replace("T", " ");

        try {
            return parseDate(date1, DateOp.PATTERN_yyyy_MM_dd_HH_mm_ss_Sz, true);
        } catch (Exception e0) {
            try {
                return parseDate(date1, DateOp.PATTERN_yyyy_MM_dd_HH_mm_ssz, true);
            } catch (Exception e1) {
                try {
                    return parseDate(date1, DateOp.PATTERN_yyyy_MM_dd_HH_mm_ss, true);
                } catch (Exception e2) {
                    try {
                        return parseDate(date1, DateOp.PATTERN_yyyy_MM_dd, true);
                    } catch (Exception e3) {
                        try {
                            return parseDate(date1, DateOp.PATTERN_dd_MM_yyyy_HH_mm_ss, true);
                        } catch (Exception e4) {
                            try {
                                return parseDate(date1, DateOp.PATTERN_dd_MM_yyyy, true);
                            } catch (Exception e5) {
                                try {
                                    return parseDate(date1, DateOp.PATTERN_yyyy_MM_dd_T_HH_mm_ssz, true);
                                } catch (Exception e6) {
                                    if (throwException) {
                                        throw new IllegalArgumentException("Date does't match any date format");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return this;
    }

    public DateOp setDate(int year, int month, int date) {
        calendar.set(year, month - 1, date);
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

    public int getDayPosition() {
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

    public String getTime() {
        return String.format(
                currentLocale,
                "%02d:%02d",
                getHour24(),
                getMinute()
        );
    }

    public int getLastDayOfMonth() {
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
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

    public String formatDate(String pattern, boolean forceEnglish) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, forceEnglish ? Locale.ENGLISH : currentLocale);
        String format = sdf.format(calendar.getTime());
        return format;
    }

    //--------------------------------------------------------------------------------------------//

    public void showDateTimePickerDialog(final Context context, final DatePickingListener listener) {
        showDateTimePickerDialog(context, null, null, listener);
    }

    public void showDateTimePickerDialog(final Context context, @Nullable Long minDate, @Nullable Long maxDate, final DatePickingListener listener) {
        showDatePickerDialog(context, minDate, maxDate, new DatePickingListener() {
            @Override
            public void onDatePicked(DateOp dateOp) {
                showTimePickerDialog(context, listener);
            }
        });
    }

    public void showTimePickerDialog(Context context, final DatePickingListener listener) {
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);

        new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                if (listener != null) {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);

                    listener.onDatePicked(DateOp.this);
                }
            }
        }, h, m, false);
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

    public static long parseDateTime(String time, String pattern) throws Exception {
        Date date = null;
        try {
            date = new SimpleDateFormat(pattern, Locale.ENGLISH).parse(time);
        } catch (Exception e1) {
            try {
                time = insureDateSeparatorIsDash(time);
                date = new SimpleDateFormat(pattern, Locale.ENGLISH).parse(time);
            } catch (Exception e2) {
            }
        }
        return date.getTime();
    }

    public static String insureDateSeparatorIsDash(String date) {
        if (date != null && (date.contains("/") || date.contains("\\") || date.contains("."))) {
            date = date
                    .replace("/", "-")
                    .replace(".", "-");
        }
        return date;
    }

}
