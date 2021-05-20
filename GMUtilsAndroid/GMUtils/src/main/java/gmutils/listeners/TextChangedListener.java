package gmutils.listeners;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer with (Java/Kotlin, Swift)
 * Have experience with:
 *      - (C/C++, C#) languages
 *      - .NET environment
 *      - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public abstract class TextChangedListener implements TextWatcher {
    public interface OnChange {
        void onChange(String text);
    }

    public static TextChangedListener create(OnChange change) {
        return new TextChangedListener() {
            @Override
            public void onTextChanged(String s) {
                change.onChange(s);
            }
        };
    }

    @Override
    public void beforeTextChanged(
            CharSequence s,
            int start,
            int count,
            int after
    ) {
    }

    @Override
    public void onTextChanged(
            CharSequence s,
            int start,
            int before,
            int count
    ) {
    }

    @Override
    public void afterTextChanged(Editable e) {
        onTextChanged(e.toString());
    }

    public abstract void onTextChanged(String s);

}
