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
public class DropTable implements ICommand {
    private String tableName;

    public DropTable(String tableName) {
        if (tableName == null || tableName.length() == 0) {
            throw new RuntimeException("Table Name not allowed to be null or empty");

        } else {
            this.tableName = tableName;
        }
    }

    @Override
    public String getCode() {
        return "DROP TABLE " + this.tableName;
    }

    @Override
    public String toString() {
        return getCode();
    }
}
