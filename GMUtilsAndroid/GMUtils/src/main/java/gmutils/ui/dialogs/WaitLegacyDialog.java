package gmutils.ui.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.StringRes;

import org.jetbrains.annotations.NotNull;

import gmutils.R;

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
public class WaitLegacyDialog extends BaseLegacyDialog {

    public static WaitLegacyDialog show(Context context) {
        return show(context, R.string.wait_moments);
    }

    public static WaitLegacyDialog show(Context context, @StringRes int msg) {
        WaitLegacyDialog waitDialog = new WaitLegacyDialog(context);
        if (msg != 0) waitDialog.textView.setText(msg);
        waitDialog.show();
        return waitDialog;
    }

    private TextView textView;

    @NotNull
    @Override
    protected View createView(LayoutInflater layoutInflater) {
        return layoutInflater.inflate(R.layout.dialog_wait, null);
    }

    public WaitLegacyDialog(Context context) {
        super(context);
        textView = getView().findViewById(R.id.tv_msg);
    }

    @Override
    protected void onViewCreated(View view) {
        super.onViewCreated(view);
        getDialog().setCanceledOnTouchOutside(false);
    }

    public TextView textView() { return textView; }

    @Override
    protected void onDestroy() {
        textView = null;
    }
}
