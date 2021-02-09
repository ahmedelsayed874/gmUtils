package com.blogspot.gm4s1.gmutils.database.sqlitecommands;

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

public enum DataTypes {
    /**
     The value is a NULL value.
     */
    NULL,

    /**
     The value is a signed integer, stored in 1, 2, 3, 4, 6, or 8 bytes
     depending on the magnitude of the value.
     */
    INTEGER,

    /**
     The value is a floating point value, stored as an 8-byte IEEE
     floating point number.
     */
    REAL,

    /**
     The value is a text string, stored using the database
     encoding (UTF-8, UTF-16BE or UTF-16LE)
     */
    TEXT,

    /**
     The value is a blob of data, stored exactly as it was input.
     */
    BLOB
}
