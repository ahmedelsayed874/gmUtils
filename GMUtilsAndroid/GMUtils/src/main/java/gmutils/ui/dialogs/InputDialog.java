package gmutils.ui.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import gmutils.R;
import gmutils.listeners.ResultCallback;
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
public class InputDialog extends BaseDialog {

    public static InputDialog create(Context context) {
        return new InputDialog(context);
    }

    private View lyContainer;
    private TextView tvTitle;
    private TextView tvMsg;
    private EditText tvInput;
    private TextView tvDone;
    private TextView tvCancel;

    private ResultCallback2<InputDialog, String> doneCallback;
    private ResultCallback<InputDialog> cancelCallback;
    

    @NonNull
    @Override
    protected View createView(LayoutInflater layoutInflater) {
        return layoutInflater.inflate(R.layout.dialog_input_gm4s, null);
    }

    public InputDialog(Context context) {
        super(context);

        View view = getView();
        lyContainer = view.findViewById(R.id.lyContainer);
        tvTitle = view.findViewById(R.id.tv_title);
        tvMsg = view.findViewById(R.id.tv_msg);
        tvInput = view.findViewById(R.id.tv_input);
        tvDone = view.findViewById(R.id.tv_btn1);
        tvCancel = view.findViewById(R.id.tv_btn2);

        tvDone.setOnClickListener(v -> {
            if (doneCallback != null)
                doneCallback.invoke(InputDialog.this, tvInput.getText().toString());
            dismiss();
        });

        tvCancel.setOnClickListener(v -> {
            if (cancelCallback != null)
                cancelCallback.invoke(InputDialog.this);
            dismiss();
        });
    }
    
    //----------------------------------------------------------------------------------------------
    
    public InputDialog setTitle(int msg) {
        tvTitle.setText(msg);
        tvTitle.setVisibility(View.VISIBLE);
        return this;
    }

    public InputDialog setTitle(CharSequence msg) {
        tvTitle.setText(msg);
        tvTitle.setVisibility(View.VISIBLE);
        return this;
    }

    //----------------------------------------------------------------------------------------------

    public InputDialog setMessage(int msg) {
        tvMsg.setText(msg);
        return this;
    }

    public InputDialog setMessage(CharSequence msg) {
        tvMsg.setText(msg);
        return this;
    }

    @SuppressLint("SetTextI18n")
    public InputDialog appendMessage(CharSequence msg) {
        tvMsg.setText(tvMsg.getText() + " " + msg);
        return this;
    }

    public InputDialog setMessageGravity(int gravity) {
        tvMsg.setGravity(gravity);
        return this;
    }

    public InputDialog setMessageBold() {
        Typeface typeface = tvMsg.getTypeface();
        Typeface.create(typeface, Typeface.BOLD);
        return this;
    }

    //----------------------------------------------------------------------------------------------

    public InputDialog setInputHint(int textId) {
        tvInput.setHint(textId);
        return this;
    }

    public InputDialog setInputHint(CharSequence text) {
        tvInput.setHint(text);
        return this;
    }

    public InputDialog setInputText(CharSequence text) {
        tvInput.setText(text);
        return this;
    }

    /**
     * {@link EditorInfo#inputType}
     * @see android.text.InputType
     */
    public InputDialog setInputTextType(int type) {
        tvInput.setInputType(type);
        return this;
    }

    //----------------------------------------------------------------------------------------------

    public InputDialog setDoneText(int textId) {
        tvDone.setText(textId);
        return this;
    }

    public InputDialog setDoneText(CharSequence text) {
        tvDone.setText(text);
        return this;
    }

    public InputDialog setDoneCallback(ResultCallback2<InputDialog, String> callback) {
        this.doneCallback = callback;
        return this;
    }

    //----------------------------------------------------------------------------------------------

    public InputDialog setCancelText(int textId) {
        tvCancel.setText(textId);
        return this;
    }

    public InputDialog setCancelText(CharSequence text) {
        tvCancel.setText(text);
        return this;
    }

    public InputDialog setCancelCallback(ResultCallback<InputDialog> callback) {
        this.cancelCallback = callback;
        return this;
    }

    //----------------------------------------------------------------------------------------------

    public InputDialog setBackground(int resId) {
        lyContainer.setBackgroundResource(resId);
        return this;
    }

    public InputDialog setTextColor(int color) {
        tvTitle.setTextColor(color);
        tvMsg.setTextColor(color);
        tvInput.setTextColor(color);

        tvDone.setTextColor(color);
        tvCancel.setTextColor(color);

        return this;
    }

    public InputDialog setCancelable(boolean cancellable) {
        super.setCancelable(cancellable);
        return this;
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public InputDialog show() {
        super.show();
        return this;
    }
    
    @Override
    protected void onDestroy() {
        this.lyContainer = null;
        this.tvTitle = null;
        this.tvMsg = null;
        this.tvInput = null;

        this.tvDone = null;
        this.tvCancel = null;

        this.doneCallback = null;
        this.cancelCallback = null;
    }
}
