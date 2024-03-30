package gmutils;

import android.util.Pair;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Calculator {
    private BigDecimal result = BigDecimal.valueOf(0);

    //----------------------------------------------------------------------------------------------

    public void calculateEquation(String equation) {
        //equation = 1+(2+3)-(4*5/(6*7))+(9*10/1)
        double v = doCalculateEquation(equation);
        result = result.add(BigDecimal.valueOf(v));
    }

    private double doCalculateEquation(String eq) throws IllegalArgumentException {
        StringBuilder equation = new StringBuilder(eq);//1+(2+3)-(4*5/(6*(7-1)))+(9*10/1
        boolean canEnd = false;

        do {
            //region manipulation ( )
            int parenthesesStartIndex = equation.indexOf("(");//0
            if (parenthesesStartIndex >= 0) {
                int parenthesesStartIndex2 = equation.indexOf("(", parenthesesStartIndex);//5
                int parenthesesEndIndex = equation.indexOf(")", parenthesesStartIndex);//12

                while (parenthesesStartIndex2 > parenthesesStartIndex && parenthesesStartIndex2 < parenthesesEndIndex) {
                    parenthesesStartIndex2 = equation.indexOf("(", parenthesesStartIndex2);//8,-1|16
                    parenthesesEndIndex = equation.indexOf(")", parenthesesEndIndex);//13, 14
                }

                if (parenthesesEndIndex < parenthesesStartIndex)
                    parenthesesEndIndex = equation.length();
                String subEquation = equation.substring(parenthesesStartIndex + 1, parenthesesEndIndex);
                double r = doCalculateEquation(subEquation);

                if (parenthesesEndIndex == equation.length()) {
                    equation.delete(parenthesesStartIndex, parenthesesEndIndex);
                } else {
                    equation.delete(parenthesesStartIndex, parenthesesEndIndex + 1);
                }
                equation.insert(parenthesesStartIndex, r);
            }
            //endregion

            //region manipulation * / + -
            else {
                //region manipulation * /
                int multiplyIndex = equation.indexOf("*");
                int divideIndex = equation.indexOf("/");
                if (multiplyIndex > 0 || divideIndex > 0) {
                    int symIndex = Math.min(multiplyIndex, divideIndex);
                    if (symIndex == -1) symIndex = Math.max(multiplyIndex, divideIndex);

                    Pair<Pair<Double, Double>, Pair<Integer, Integer>> equationTermsAndIndexes;
                    equationTermsAndIndexes = getEquationTerms(equation, symIndex, 1);

                    Pair<Double, Double> equationTerms = equationTermsAndIndexes.first;
                    double term1;
                    double term2;
                    try {
                        term1 = equationTerms.first;
                    } catch (Exception e) {
                        throw new IllegalArgumentException(e);
                    }

                    try {
                        term2 = equationTerms.second;
                    } catch (Exception e) {
                        throw new IllegalArgumentException(e);
                    }

                    double result;
                    if (symIndex == divideIndex) {
                        try {
                            result = term1 / term2;
                        } catch (Exception e) {
                            throw new IllegalArgumentException(e);
                        }
                    } else {
                        result = term1 * term2;
                    }

                    Pair<Integer, Integer> indexes = equationTermsAndIndexes.second;
                    equation.replace(indexes.first, indexes.second + 1, "" + result);
                }
                //endregion

                //region manipulation - +
                else {
                    int addIndex = equation.indexOf("+");
                    int subtractIndex = equation.indexOf("-");

                    if (addIndex == 0 || subtractIndex == 0) {
                        if (equation.length() > 1) {
                            addIndex = equation.indexOf("+", 1);
                            subtractIndex = equation.indexOf("-", 1);
                        }
                    }

                    if (addIndex > 0 || subtractIndex > 0) {
                        int symIndex = Math.min(addIndex, subtractIndex);
                        if (symIndex == -1) symIndex = Math.max(addIndex, subtractIndex);

                        Pair<Pair<Double, Double>, Pair<Integer, Integer>> equationTermsAndIndexes;
                        equationTermsAndIndexes = getEquationTerms(equation, symIndex);

                        Pair<Double, Double> equationTerms = equationTermsAndIndexes.first;
                        double term1;
                        double term2;
                        try {
                            term1 = equationTerms.first;
                        } catch (Exception e) {
                            throw new IllegalArgumentException(e);
                        }

                        try {
                            term2 = equationTerms.second;
                        } catch (Exception e) {
                            throw new IllegalArgumentException(e);
                        }

                        double result;
                        if (symIndex == addIndex) {
                            result = term1 + term2;
                        } else {
                            result = term1 - term2;
                        }

                        Pair<Integer, Integer> indexes = equationTermsAndIndexes.second;
                        equation.replace(indexes.first, indexes.second + 1, "" + result);
                    }

                    //
                    else {
                        canEnd = true;
                    }
                }
                //endregion
            }
            //endregion
        } while (!canEnd);

        try {
            return Double.parseDouble(equation.toString());
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Pair<Pair<Double, Double>, Pair<Integer, Integer>> getEquationTerms(StringBuilder equation, int symIndex) {
        return getEquationTerms(equation, symIndex, 0);
    }

    private Pair<Pair<Double, Double>, Pair<Integer, Integer>> getEquationTerms(StringBuilder equation, int symIndex, double leftTermDefaultValue) {
        StringBuilder rightTerm = new StringBuilder();
        StringBuilder leftTerm = new StringBuilder();
        int startIndex = 0;
        int endIndex = 0;

        int index = symIndex - 1;

        //-1.2+3.4
        if (index >= 0) {
            do {
                char c = equation.charAt(index--);
                if (c >= '0' && c <= '9' || c == '.') {
                    startIndex = index + 1;
                    rightTerm.insert(0, c);
                } else {
                    if (c == '-' || c == '+') {
                        if (rightTerm.length() > 0 && rightTerm.charAt(0) == '.') {
                            rightTerm.insert(0, 0);
                        }

                        if (index == -1) {
                            startIndex = 0;
                            rightTerm.insert(0, c);
                            break;
                        } else if (index > 0) {
                            char c2 = equation.charAt(index--);
                            if (c2 == '-' || c2 == '+') {
                                startIndex = index + 1;
                                rightTerm.insert(0, c);
                                break;
                            } else {
                                break;
                            }
                        }
                    }
                }
            } while (index >= 0);

            if (rightTerm.length() > 0 && rightTerm.charAt(0) == '.') {
                rightTerm.insert(0, 0);
            }
        }

        index = symIndex + 1;
        //-1.2+-3.4
        if (index < equation.length()) {
            do {
                char c = equation.charAt(index++);
                if (c == '.') {
                    if (leftTerm.length() == 0) {
                        leftTerm.append(0);
                    }
                    endIndex = index - 1;
                    leftTerm.append(c);
                }
                //
                else if (c >= '0' && c <= '9') {
                    endIndex = index - 1;
                    leftTerm.append(c);
                }
                //
                else if (c == '-' || c == '+') {
                    if (leftTerm.length() == 0) {
                        endIndex = index - 1;
                        leftTerm.append(c);
                    } else {
                        break;
                    }
                }
            } while (index < equation.length());

            if (leftTerm.length() > 0 && leftTerm.charAt(leftTerm.length() - 1) == '.') {
                leftTerm.append(0);
            }
        } else {
            endIndex = index - 1;
        }

        if (rightTerm.length() == 0) {
            rightTerm.append(0);
        }

        if (leftTerm.length() == 0) {
            leftTerm.append(leftTermDefaultValue);
        }

        Double rightTermAsNumber = null;
        Double leftTermAsNumber = null;
        try {
            rightTermAsNumber = Double.parseDouble(rightTerm.toString().trim());
        } catch (Exception ignored) {}
        try {
            leftTermAsNumber = Double.parseDouble(leftTerm.toString().trim());
        } catch (Exception ignored) {}

        return new Pair<>(
                new Pair<>(rightTermAsNumber, leftTermAsNumber),
                new Pair<>(startIndex, endIndex)
        );
    }

    //----------------------------------------------------------------------------------------------

    public void add(double number) {
        result = result.add(BigDecimal.valueOf(number));
    }

    public void subtract(double number) {
        result = result.subtract(BigDecimal.valueOf(number));
    }

    public void multiplyBy(double multiplier) {
        result = result.multiply(BigDecimal.valueOf(multiplier));
    }

    public void divide(double divisor) throws IllegalArgumentException {
        divide(divisor, 3);
    }

    public void divide(double divisor, int precision) throws IllegalArgumentException {
        try {
            result = result.divide(BigDecimal.valueOf(divisor), new MathContext(precision));
        } catch (Exception e) {
           throw new IllegalArgumentException(e);
        }
    }

    public void reset() {
        result = BigDecimal.ZERO;
    }

    public BigDecimal result() {
        return result;
    }
}