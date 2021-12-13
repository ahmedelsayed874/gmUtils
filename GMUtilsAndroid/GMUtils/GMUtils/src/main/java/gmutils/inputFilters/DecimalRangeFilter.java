package gmutils.inputFilters;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;


public class DecimalRangeFilter implements InputFilter {

    private final double mMin;
    private final double mMax;

    public DecimalRangeFilter(double min, double max) {
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

            if (input.indexOf("00") == 0) return "";

            try {
                double value0 = Double.parseDouble(input);
                Log.e("******", "value: " + value0);
                if (isRange(mMin, mMax, value0))
                    return null;
                else
                    return "";
            } catch (Exception e) {
                Log.e("******", "value: " + input + " X");
                return "";
            }
        } catch (Exception ex) {
        }
        return "";
    }

    private boolean isRange(double min, double max, double value) {
        return max > min ? value >= min && value <= max : value >= max && value <= min;
    }
}
