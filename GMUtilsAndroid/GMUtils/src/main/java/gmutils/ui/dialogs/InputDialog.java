package gmutils.ui.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private TextView tvPositiveBtn;
    private TextView tvCancelBtn;

    private ResultCallback2<InputDialog, String> positiveButtonCallback;
    private ResultCallback<InputDialog> cancelButtonCallback;
    

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
        tvPositiveBtn = view.findViewById(R.id.tv_done);
        tvCancelBtn = view.findViewById(R.id.tv_cancel);

        tvPositiveBtn.setOnClickListener(v -> {
            if (positiveButtonCallback != null)
                positiveButtonCallback.invoke(InputDialog.this, tvInput.getText().toString());
            dismiss();
        });

        tvCancelBtn.setOnClickListener(v -> {
            if (cancelButtonCallback != null)
                cancelButtonCallback.invoke(InputDialog.this);
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

    public InputDialog setPositiveButtonText(int textId) {
        if (textId > 0) {
            tvPositiveBtn.setText(textId);
            tvPositiveBtn.setVisibility(View.VISIBLE);
        } else {
            tvPositiveBtn.setVisibility(View.GONE);
        }
        return this;
    }

    public InputDialog setPositiveButtonText(CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            tvPositiveBtn.setText(text);
            tvPositiveBtn.setVisibility(View.VISIBLE);
        } else {
            tvPositiveBtn.setVisibility(View.GONE);
        }
        return this;
    }

    public InputDialog setPositiveButtonCallback(ResultCallback2<InputDialog, String> callback) {
        this.positiveButtonCallback = callback;
        return this;
    }

    //----------------------------------------------------------------------------------------------

    public InputDialog setCancelButtonText(int textId) {
        if (textId > 0) {
            tvCancelBtn.setText(textId);
            tvCancelBtn.setVisibility(View.VISIBLE);
        } else {
            tvCancelBtn.setVisibility(View.GONE);
        }
        return this;
    }

    public InputDialog setCancelButtonText(CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            tvCancelBtn.setText(text);
            tvCancelBtn.setVisibility(View.VISIBLE);
        } else {
            tvCancelBtn.setVisibility(View.GONE);
        }
        return this;
    }

    public InputDialog setCancelButtonCallback(ResultCallback<InputDialog> callback) {
        this.cancelButtonCallback = callback;
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

        tvPositiveBtn.setTextColor(color);
        tvCancelBtn.setTextColor(color);

        return this;
    }

    public InputDialog setCancelable(boolean cancellable) {
        super.setCancelable(cancellable);
        return this;
    }

    //----------------------------------------------------------------------------------------------

    /**
     * @param minWidth: -1 = MatchParent
     */
    public InputDialog setMinimumWidth(int minWidth) {
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

    //----------------------------------------------------------------------------------------------

    @Override
    public InputDialog show() {
        super.show();
        return this;
    }

    @Override
    protected BaseDialog reinitialize(Context context) {
        InputDialog dialog = new InputDialog(context);
        dialog.setTitle(tvTitle.getText());
        dialog.setMessage(tvMsg.getText());
        dialog.setInputHint(tvInput.getHint());
        dialog.setInputTextType(tvInput.getInputType());
        dialog.setPositiveButtonText(tvPositiveBtn.getText());
        dialog.setPositiveButtonCallback(positiveButtonCallback);
        dialog.setCancelButtonText(tvCancelBtn.getText());
        dialog.setCancelButtonCallback(cancelButtonCallback);

        return dialog;
    }

    //----------------------------------------------------------------------------------------------

    @Override
    protected void onDestroy() {
        this.lyContainer = null;
        this.tvTitle = null;
        this.tvMsg = null;
        this.tvInput = null;

        this.tvPositiveBtn = null;
        this.tvCancelBtn = null;

        this.positiveButtonCallback = null;
        this.cancelButtonCallback = null;
    }
}
