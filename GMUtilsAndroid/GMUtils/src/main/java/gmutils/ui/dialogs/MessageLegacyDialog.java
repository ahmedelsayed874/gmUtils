package gmutils.ui.dialogs;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import org.json.JSONArray;

import gmutils.R;
import gmutils.listeners.ResultCallback;
import gmutils.listeners.ResultCallback2;
import gmutils.storage.GeneralStorage;

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
public class MessageLegacyDialog extends BaseLegacyDialog {

    public interface Listener {
        void invoke(MessageLegacyDialog dialog);
    }

    public static MessageLegacyDialog create(Context context) {
        MessageLegacyDialog dialog = new MessageLegacyDialog(context);
        return dialog;
    }

    @NonNull
    @Override
    public View createView(LayoutInflater layoutInflater) {
        return layoutInflater.inflate(R.layout.dialog_message_gm4s, null);
    }

    private MessageDialogFunctions functions;
    private Listener listenerBtn1;
    private Listener listenerBtn2;
    private Listener listenerBtn3;

    MessageLegacyDialog(Context context) {
        super(context);

        functions = new MessageDialogFunctions(new MessageDialogFunctions.Listener() {
            @Override
            public View getView() {
                return MessageLegacyDialog.this.getView();
            }

            @Override
            public void onButtonClick(MessageDialogFunctions dialog, int button) {
                if (button == 1) {
                    if (listenerBtn1 != null)
                        listenerBtn1.invoke(MessageLegacyDialog.this);

                } else if (button == 2) {
                    if (listenerBtn2 != null)
                        listenerBtn2.invoke(MessageLegacyDialog.this);

                } else if (button == 3) {
                    if (listenerBtn3 != null)
                        listenerBtn3.invoke(MessageLegacyDialog.this);
                }

                dismiss();
            }
        });
    }

    public MessageLegacyDialog setBackground(int resId) {
        functions.setBackground(resId);
        return this;
    }

    public MessageLegacyDialog setTextColor(int color) {
        functions.setTextColor(color);
        return this;
    }


    public MessageLegacyDialog setTitle(int msg) {
        functions.setTitle(msg);
        return this;
    }

    public MessageLegacyDialog setTitle(CharSequence msg) {
        functions.setTitle(msg);
        return this;
    }


    public MessageLegacyDialog setMessage(int msg) {
        functions.setMessage(msg);
        return this;
    }

    public MessageLegacyDialog setMessage(CharSequence msg) {
        functions.setMessage(msg);
        return this;
    }

    @SuppressLint("SetTextI18n")
    public MessageLegacyDialog appendMessage(CharSequence msg) {
        functions.appendMessage(msg);
        return this;
    }

    public MessageLegacyDialog setMessageGravity(int gravity) {
        functions.setMessageGravity(gravity);
        return this;
    }

    public MessageLegacyDialog setMessageBold() {
        functions.setMessageBold();
        return this;
    }


    //----------------------------------------------------------------------------------------------

    @NonNull
    private JSONArray getDontShowAgainCheckboxTagsArray() {
        return functions.getDontShowAgainCheckboxTagsArray();
    }

    private void saveDontShowAgainCheckboxTagsArray(@NonNull JSONArray tagsJsonArray) {
        functions.saveDontShowAgainCheckboxTagsArray(tagsJsonArray);
    }

    /**
     * @param tag      : unique number points to checkbox state
     * @param listener : sends {@MessageDialog, @Boolean that represents the result}
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public MessageLegacyDialog isDontShowAgainCheckboxDisabledByUser(int tag, ResultCallback2<MessageLegacyDialog, Boolean> listener) {
        functions.isDontShowAgainCheckboxDisabledByUser(tag, result -> {
            listener.invoke(MessageLegacyDialog.this, result);
        });
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public MessageLegacyDialog dropDontShowAgainCheckboxDisabledByUser(int tag) {
        functions.dropDontShowAgainCheckboxDisabledByUser(tag);
        return this;
    }

    /**
     * @param tag unique number points to checkbox state
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public MessageLegacyDialog showDontShowAgainCheckbox(int tag) {
        functions.showDontShowAgainCheckbox(tag);
        return this;
    }

    /**
     * @param tag unique number points to checkbox state
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public MessageLegacyDialog showDontShowAgainCheckbox(int tag, CharSequence msg) {
        functions.showDontShowAgainCheckbox(tag, msg);
        return this;
    }

    //----------------------------------------------------------------------------------------------

    public MessageLegacyDialog setButton1(int textId, Listener listener) {
        functions.setButton1(textId);
        this.listenerBtn1 = listener;
        return this;
    }

    public MessageLegacyDialog setButton1(CharSequence text, Listener listener) {
        functions.setButton1(text);
        this.listenerBtn1 = listener;
        return this;
    }

    public MessageLegacyDialog setButton2(int textId, Listener listener) {
        functions.setButton2(textId);
        this.listenerBtn2 = listener;
        return this;
    }

    public MessageLegacyDialog setButton2(CharSequence text, Listener listener) {
        functions.setButton2(text);
        this.listenerBtn2 = listener;
        return this;
    }

    public MessageLegacyDialog setButton3(int textId, Listener listener) {
        functions.setButton3(textId);
        this.listenerBtn3 = listener;
        return this;
    }

    public MessageLegacyDialog setButton3(CharSequence text, Listener listener) {
        functions.setButton3(text);
        this.listenerBtn3 = listener;
        return this;
    }


    public MessageLegacyDialog setCancelable(boolean cancellable) {
        super.setCancelable(cancellable);
        return this;
    }

    @Override
    public MessageLegacyDialog show() {
        super.show();
        return this;
    }

    //----------------------------------------------------------------------------------------------


    @Override
    protected void onDestroy() {
        functions.onDestroy();
        functions = null;

        this.listenerBtn1 = null;
        this.listenerBtn2 = null;
        this.listenerBtn3 = null;
    }
}
