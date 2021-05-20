package gmutils.database.sqlitecommands;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
public class CreateTable implements ICommand {
    public static class Column implements ICommand {
        String name;
        DataTypes dataType;
        Constraint[] constraints;

        public Column(String name, DataTypes dataType, Constraint[] constraints){
            this.name = name;
            this.dataType = dataType;
            this.constraints = constraints;
        }

        @Override
        public String getCode() {
            String contraintstr = " ";
            if (constraints != null) {
                for (int i = 0; i < constraints.length; i++) {
                    contraintstr += constraints[i].getCode() + " ";
                }
            }
            return name + " " + dataType.name() + contraintstr;
        }
    }

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

    public static class Constraint implements ICommand {
        private final ConstraintKeywords constraint;
        private final Object extra;
        private boolean isFunction;

        public Constraint(@NotNull ConstraintKeywords name) {
            this(name, null, false);
        }

        public Constraint(@NotNull ConstraintKeywords name, String extra) {
            this(name, extra, false);
        }

        public Constraint(@NotNull ConstraintKeywords name, Object extra, boolean isFunction) {
            this.constraint = name;
            if (name == ConstraintKeywords.DEFAULT && null == extra) {
                this.extra = "";
            } else {
                this.extra = extra;
            }

            this.isFunction = isFunction;
        }

        public ConstraintKeywords getConstraint() {
            return constraint;
        }

        @Override
        public String getCode() {
            String code = constraint.getName();
            if (null != extra) {
                if (extra.getClass() == String.class && !isFunction) {
                    code += " '" + extra + "'";
                } else {
                    code += " " + extra;
                }
            }
            return code;
        }

        @Override
        public String toString() {
            return getCode();
        }
    }

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

    //----------------------------------------------------------------------------------------------

    private final String tableName;
    private final List<Column> columns = new ArrayList<>();

    public CreateTable(String tableName) {
        if (tableName == null || tableName.length() == 0) {
            throw new RuntimeException("Table Name not allowed to be null or empty");

        } else {
            this.tableName = tableName;
        }
    }

    public void addColumn(String name, DataTypes dataType, Constraint[] constraints) {
        columns.add(new Column(name, dataType, constraints));
    }

    @Override
    public String getCode() {
        StringBuilder cols = new StringBuilder();
        int i = 0;

        for (Column col : columns) {
            cols.append(col.getCode());
            i++;
            if (i < columns.size()) cols.append(", ");
        }

        if (cols.length() == 0)
            throw new RuntimeException("No Column are added to " + this.tableName + " table");
        else {
            return "CREATE TABLE " + this.tableName + " (" + cols.toString() + ");";
        }
    }

    @Override
    public String toString() {
        return getCode();
    }
}
