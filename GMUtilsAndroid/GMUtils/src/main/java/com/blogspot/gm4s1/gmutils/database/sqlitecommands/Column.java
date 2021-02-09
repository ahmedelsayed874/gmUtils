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

public class Column implements ICommand {
    String name;
    DataTypes dataType;
    Constraints[] constraints;

    public Column(String name, DataTypes dataType, Constraints[] constraints){
        this.name = name;
        this.dataType = dataType;
        this.constraints = constraints;
    }

    @Override
    public String getCode() {
        String contraintstr = " ";
        if (constraints != null) {
            for (int i = 0; i < constraints.length; i++) {
                contraintstr += constraints[i].getName() + " ";
            }
        }
        return name + " " + dataType.name() + contraintstr;
    }
}
