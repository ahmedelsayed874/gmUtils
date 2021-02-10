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

public enum ConstraintKeywords {
    /**
     Ensures that a column cannot have NULL value.
     */
    NOT_NULL,

    /**
     Provides a default value for a column when none is specified.
     */
    DEFAULT,

    /**
     Ensures that all values in a column are different.
     */
    UNIQUE,

    /**
     Uniquely identified each rows/records in a database table.
     */
    PRIMARY_KEY,

    AUTOINCREMENT,

    /**
     The CHECK constraint ensures that all values in a column satisfy certain conditions.
     */
    CHECK;

    public String getName(){
        if (this.name().indexOf("_") >= 0) return this.name().replace('_', ' ');
        else return this.name();
    }
}
