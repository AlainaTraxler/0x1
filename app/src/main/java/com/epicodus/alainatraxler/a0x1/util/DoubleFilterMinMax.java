package com.epicodus.alainatraxler.a0x1.util;

/**
 * Created by Guest on 12/20/16.
 */
import android.text.InputFilter;
import android.text.Spanned;

public class DoubleFilterMinMax implements InputFilter {

    private Double min, max;

    public DoubleFilterMinMax(Double min, Double max) {
        this.min = min;
        this.max = max;
    }

    public DoubleFilterMinMax(String min, String max) {
        this.min = Double.parseDouble(min);
        this.max = Double.parseDouble(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            Double input = Double.parseDouble(dest.toString() + source.toString());

            String text = Double.toString(Math.abs(input));
            int integerPlaces = text.indexOf('.');
            int decimalPlaces = text.length() - integerPlaces - 1;
            if (isInRange(min, max, input) && decimalPlaces <= 2)
                return null;
        } catch (NumberFormatException nfe) { }
        return "";
    }

    private boolean isInRange(Double a, Double b, Double c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
