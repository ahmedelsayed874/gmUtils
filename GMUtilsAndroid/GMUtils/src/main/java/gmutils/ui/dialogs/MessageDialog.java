package gmutils.ui.dialogs;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import org.json.JSONArray;

import gmutils.R;
import gmutils.listeners.ResultCallback2;

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
public class MessageDialog extends BaseDialog {

    public interface Listener {
        void invoke(MessageDialog dialog);
    }

    public static MessageDialog create(Context context) {
        MessageDialog dialog = new MessageDialog(context);
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

    MessageDialog(Context context) {
        super(context);

        functions = new MessageDialogFunctions(new MessageDialogFunctions.Listener() {
            @Override
            public View getView() {
                return MessageDialog.this.getView();
            }

            @Override
            public void onButtonClick(MessageDialogFunctions dialog, int button) {
                if (button == 1) {
                    if (listenerBtn1 != null)
                        listenerBtn1.invoke(MessageDialog.this);

                } else if (button == 2) {
                    if (listenerBtn2 != null)
                        listenerBtn2.invoke(MessageDialog.this);

                } else if (button == 3) {
                    if (listenerBtn3 != null)
                        listenerBtn3.invoke(MessageDialog.this);
                }

                dismiss();
            }
        });
    }

    public MessageDialog setBackground(int resId) {
        functions.setBackground(resId);
        return this;
    }

    public MessageDialog setTextColor(int color) {
        functions.setTextColor(color);
        return this;
    }


    public MessageDialog setTitle(int msg) {
        functions.setTitle(msg);
        return this;
    }

    public MessageDialog setTitle(CharSequence msg) {
        functions.setTitle(msg);
        return this;
    }


    public MessageDialog setMessage(int msg) {
        functions.setMessage(msg);
        return this;
    }

    public MessageDialog setMessage(CharSequence msg) {
        functions.setMessage(msg);
        return this;
    }

    @SuppressLint("SetTextI18n")
    public MessageDialog appendMessage(CharSequence msg) {
        functions.appendMessage(msg);
        return this;
    }

    public MessageDialog setMessageGravity(int gravity) {
        functions.setMessageGravity(gravity);
        return this;
    }

    public MessageDialog setMessageBold() {
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
    public MessageDialog isDontShowAgainCheckboxDisabledByUser(int tag, ResultCallback2<MessageDialog, Boolean> listener) {
        functions.isDontShowAgainCheckboxDisabledByUser(tag, result -> {
            listener.invoke(MessageDialog.this, result);
        });
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public MessageDialog dropDontShowAgainCheckboxDisabledByUser(int tag) {
        functions.dropDontShowAgainCheckboxDisabledByUser(tag);
        return this;
    }

    /**
     * @param tag unique number points to checkbox state
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public MessageDialog showDontShowAgainCheckbox(int tag) {
        functions.showDontShowAgainCheckbox(tag);
        return this;
    }

    /**
     * @param tag unique number points to checkbox state
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public MessageDialog showDontShowAgainCheckbox(int tag, CharSequence msg) {
        functions.showDontShowAgainCheckbox(tag, msg);
        return this;
    }

    //----------------------------------------------------------------------------------------------

    public MessageDialog setButton1(int textId, Listener listener) {
        functions.setButton1(textId);
        this.listenerBtn1 = listener;
        return this;
    }

    public MessageDialog setButton1(CharSequence text, Listener listener) {
        functions.setButton1(text);
        this.listenerBtn1 = listener;
        return this;
    }

    public MessageDialog setButton2(int textId, Listener listener) {
        functions.setButton2(textId);
        this.listenerBtn2 = listener;
        return this;
    }

    public MessageDialog setButton2(CharSequence text, Listener listener) {
        functions.setButton2(text);
        this.listenerBtn2 = listener;
        return this;
    }

    public MessageDialog setButton3(int textId, Listener listener) {
        functions.setButton3(textId);
        this.listenerBtn3 = listener;
        return this;
    }

    public MessageDialog setButton3(CharSequence text, Listener listener) {
        functions.setButton3(text);
        this.listenerBtn3 = listener;
        return this;
    }


    public MessageDialog setCancelable(boolean cancellable) {
        super.setCancelable(cancellable);
        return this;
    }

    /**
     * @param minWidth: -1 = MatchParent
     */
    public MessageDialog setMinimumWidth(int minWidth) {
        View lyContainer = getView().findViewById(R.id.lyContainer);

        if (minWidth < 0) {
            ViewGroup.LayoutParams layoutParams = lyContainer.getLayoutParams();
            layoutParams.width = -1;
            lyContainer.setLayoutParams(layoutParams);

        } else {
            lyContainer.setMinimumWidth(minWidth);
        }

        return this;
    }

    @Override
    public MessageDialog show() {
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
