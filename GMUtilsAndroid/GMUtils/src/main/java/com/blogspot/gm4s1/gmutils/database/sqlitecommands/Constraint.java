package com.blogspot.gm4s1.gmutils.database.sqlitecommands;

import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;

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

public class Constraint implements ICommand {
    private final ConstraintKeywords constraint;
    private final String name;
    private final String extra;

    public Constraint(@NotNull ConstraintKeywords name) {
        this.constraint = name;
        this.name = name.getName();
        this.extra = "";
    }

    public Constraint(@NotNull ConstraintKeywords name, String extra) {
        this.constraint = name;
        this.name = name.getName();
        this.extra = extra;
    }

    public ConstraintKeywords getConstraint() {
        return constraint;
    }

    @Override
    public String getCode() {
        String code = name;
        if (!TextUtils.isEmpty(extra)) {
            if (extra.contains("()"))
                code += " " + extra;
            else
                code += " '" + extra + "'";
        }
        return code;
    }

    @Override
    public String toString() {
        return getCode();
    }
}
