package gmutils.database.sqlitecommands;

import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
public class WhereClause implements ICommand {

    public static class Clause implements ICommand {
        private final LogicOperator logicOperator;
        private final String key;
        private final CompareOperator compareOperator;
        private final Object value;

        public Clause(@NotNull String key, @NotNull CompareOperator compareOperator, @NotNull Object value) {
            this(null, key, compareOperator, value);
        }

        public Clause(@Nullable LogicOperator logicOperator, @NotNull String key, @NotNull CompareOperator compareOperator, @NotNull Object value) {
            this.logicOperator = logicOperator;
            this.key = key;
            this.compareOperator = compareOperator;
            this.value = value;
        }

        @Override
        public String getCode() {
            StringBuilder sb = new StringBuilder();
            if (logicOperator != null) {
                sb.append(logicOperator.value);
                sb.append(" ");
            }

            sb.append(key);
            sb.append(" ");

            sb.append(compareOperator.value);
            sb.append(" ");

            if (value.getClass() == String.class) {
                sb.append("'");
                sb.append(value);
                sb.append("'");

            } else {
                try {
                    boolean v = (boolean) value;
                    sb.append(v ? 1 : 0);
                } catch (Exception e) {
                    sb.append(value);
                }
            }

            return sb.toString();
        }

        @Override
        public String toString() {
            return getCode();
        }
    }

    public enum CompareOperator {
        Equal("="),
        NotEqual("<>"),

        GreaterThan(">"),
        GreaterThanOrEqual(">="),

        LessThan("<"),
        LessThanOrEqual("<="),

        Like("LIKE"),
        In("IN"),
        BETWEEN("BETWEEN");

        String value;

        CompareOperator(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum LogicOperator {
        And("AND"),
        Or("OR"),
        NOT("NOT");

        String value;

        LogicOperator(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    //----------------------------------------------------------------------------------------------

    private final List<Clause> clauseList;

    public WhereClause() {
        clauseList = new ArrayList<>();
    }

    public WhereClause append(Clause clause) {
        clauseList.add(clause);
        return this;
    }

    public boolean hasCode() {
        return !TextUtils.isEmpty(getCode());
    }

    @Override
    public String getCode() {
        StringBuilder sb = new StringBuilder();

        int i = 0;
        int size = clauseList.size();

        for (Clause clause : clauseList) {
            sb.append(clause.getCode());

            i++;
            if (i < size) {
                sb.append(" ");
            }
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return getCode();
    }
}
