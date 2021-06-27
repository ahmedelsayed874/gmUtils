package gmutils.inputFilters;

import android.text.InputFilter;
import android.text.Spanned;


public class IntegerRangeFilter implements InputFilter {

    private final int mMin;
    private final int mMax;

    public IntegerRangeFilter(int min, int max) {
        mMin = min;
        mMax = max;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

        try {
            String input = "";
            input += dest.subSequence(0, dstart);
            input += source.subSequence(start, end);
            input += dest.subSequence(dend, dest.length());
            int value = Integer.parseInt(input);
            if (isRange(mMin, mMax, value)) {
                return null;
            }
        }
        catch (Exception ex) {

        }
        return "";
    }

    private boolean isRange(int min, int max, int value) {
        return max > min ? value >= min && value <= max : value >= max && value <= min;
    }
}
