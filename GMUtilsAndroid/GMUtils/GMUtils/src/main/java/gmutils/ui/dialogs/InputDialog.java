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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import gmutils.KeypadOp;
import gmutils.R;
import gmutils.listeners.ActionCallback;
import gmutils.listeners.ResultCallback;
import gmutils.listeners.ResultCallback2;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer (Java/Kotlin, Swift) also Flutter (Dart)
 * Have precedent experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - Java swing
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
    private LinearLayout inputFieldsContainer;
    private final List<InputField> inputFields = new ArrayList<>();
    private TextView tvPositiveBtn;
    private TextView tvCancelBtn;

    private ActionCallback<String[], CharSequence[]> positiveButtonCallback;
    private Runnable cancelButtonCallback;


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
        inputFieldsContainer = view.findViewById(R.id.inputFieldsContainer);
        tvPositiveBtn = view.findViewById(R.id.tv_done);
        tvCancelBtn = view.findViewById(R.id.tv_cancel);

        tvPositiveBtn.setOnClickListener(v -> {
            if (positiveButtonCallback != null) {
                String[] inputs = new String[inputFields.size()];
                for (int i = 0; i < inputFields.size(); i++) {
                    inputs[i] = inputFields.get(i).inputEditText.getText().toString();
                }

                CharSequence[] errors = positiveButtonCallback.invoke(inputs);
                if (errors != null) {
                    assert inputs.length == errors.length;
                    int x = 0;

                    for (int i = 0; i < errors.length; i++) {
                        try {
                            if (!TextUtils.isEmpty(errors[i])) {
                                x++;
                                inputFields.get(i).inputEditText.setError(errors[i]);
                            }
                        } catch (Exception ignored) {
                        }
                    }

                    if (x == 0) dismiss();
                } else {
                    dismiss();
                }
            } else {
                dismiss();
            }
        });

        tvCancelBtn.setOnClickListener(v -> {
            if (cancelButtonCallback != null)
                cancelButtonCallback.run();
            dismiss();
        });

    }

    @Override
    public void dismiss() {
        for (InputField inputField : inputFields) {
            try {
                KeypadOp.hide(inputField.inputEditText);
            } catch (Exception e) {
            }
        }
        super.dismiss();
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

    public InputDialog setMessageMinLines(int min) {
        tvMsg.setMinLines(min);
        return this;
    }

    public InputDialog setMessageBold() {
        Typeface typeface = tvMsg.getTypeface();
        Typeface.create(typeface, Typeface.BOLD);
        return this;
    }

    //----------------------------------------------------------------------------------------------

    public InputDialog addInputField(ResultCallback<InputField> inputFieldBuilder) {
        InputField inputField1 = InputField.addTo(inputFieldsContainer);
        if (inputFieldBuilder != null) inputFieldBuilder.invoke(inputField1);
        inputFields.add(inputField1);
        return this;
    }

    public InputDialog getInputFieldAt(int index, ResultCallback<InputField> inputFieldBuilder) {
        if (inputFields.isEmpty()) addInputField(null);
        if (index >= 0 && index < inputFields.size()) {
            inputFieldBuilder.invoke(inputFields.get(index));
        } else {
            inputFieldBuilder.invoke(null);
        }
        return this;
    }

    //------------------------------------------------------

    public InputDialog setInputTitle(int textId) {
        getInputFieldAt(0, tf -> tf.setTitle(textId));
        return this;
    }

    public InputDialog setInputTitle(CharSequence text) {
        getInputFieldAt(0, tf -> tf.setTitle(text));
        return this;
    }

    public InputDialog setInputHint(int textId) {
        getInputFieldAt(0, tf -> tf.setHint(textId));
        return this;
    }

    public InputDialog setInputHint(CharSequence text) {
        getInputFieldAt(0, tf -> tf.setHint(text));
        return this;
    }

    public InputDialog setInputText(CharSequence text) {
        getInputFieldAt(0, tf -> tf.setText(text));
        return this;
    }

    /**
     * {@link EditorInfo#inputType}
     *
     * @see android.text.InputType
     */
    public InputDialog setInputTextType(int type) {
        getInputFieldAt(0, tf -> tf.setTextType(type));
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

    public InputDialog setPositiveButtonCallback(ActionCallback<String[], CharSequence[]> callback) {
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

    public InputDialog setCancelButtonCallback(Runnable callback) {
        this.cancelButtonCallback = callback;
        return this;
    }

    //----------------------------------------------------------------------------------------------

    public InputDialog setBackground(int resId) {
        lyContainer.setBackgroundResource(resId);
        return this;
    }

    @Override
    public InputDialog setTitleColorRes(int resid) {
        tvTitle.setTextColor(ContextCompat.getColor(tvTitle.getContext(), resid));
        return this;
    }

    public InputDialog setTitleColor(int color) {
        tvTitle.setTextColor(color);
        return this;
    }

    @Override
    public InputDialog setTextColorRes(int resid) {
        setTextColor(ContextCompat.getColor(tvTitle.getContext(), resid));
        return this;
    }

    public InputDialog setTextColor(int color) {
        tvMsg.setTextColor(color);

        for (InputField inputField : inputFields) {
            inputField.setTextColor(color);
        }

        return this;
    }

    public InputDialog setPositiveButtonColor(int color) {
        tvPositiveBtn.setTextColor(color);
        return this;
    }

    public InputDialog setCancelButtonColor(int color) {
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
        if (inputFields.isEmpty()) addInputField(null);

        super.show();
        return this;
    }

    @Override
    protected BaseDialog reinitialize(Context context) {
        InputDialog dialog = new InputDialog(context);
        dialog.setTitle(tvTitle.getText());
        dialog.setMessage(tvMsg.getText());
        for (InputField inputField : inputFields) {
            dialog.addInputField((tf) -> tf.cloneFrom(inputField));
        }
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
        this.inputFieldsContainer = null;

        for (InputField inputField : inputFields) inputField.destroy();
        this.inputFields.clear();

        this.tvPositiveBtn = null;
        this.tvCancelBtn = null;

        this.positiveButtonCallback = null;
        this.cancelButtonCallback = null;
    }

    //----------------------------------------------------------------------------------------------

    public static class InputField {
        static InputField addTo(ViewGroup container) {
            View view = LayoutInflater.from(container.getContext()).inflate(R.layout.dialog_input_element_gm4s, null);
            container.addView(view);
            return new InputField(view);
        }

        //public final TextInputLayout textInputLayout;
        public final TextView titleTextView;
        public final EditText inputEditText;

        public InputField(View view) {
            titleTextView = view.findViewById(R.id.titleTv);
            inputEditText = view.findViewById(R.id.inputEt);
        }

        public InputField getTitleTextView(ResultCallback<TextView> callback) {
            callback.invoke(titleTextView);
            return this;
        }

        public InputField getEditText(ResultCallback<EditText> callback) {
            callback.invoke(inputEditText);
            return this;
        }

        public InputField setTitle(int textId) {
            titleTextView.setText(textId);
            return this;
        }

        public InputField setTitle(CharSequence text) {
            titleTextView.setText(text);
            return this;
        }

        public InputField setHint(int textId) {
            inputEditText.setHint(textId);
            return this;
        }

        public InputField setHint(CharSequence text) {
            inputEditText.setHint(text);
            return this;
        }

        public InputField setText(CharSequence text) {
            inputEditText.setText(text);
            return this;
        }

        /**
         * {@link EditorInfo#inputType}
         *
         * @see android.text.InputType
         */
        public InputField setTextType(int type) {
            inputEditText.setInputType(type);
            return this;
        }

        public InputField setTextColor(int color) {
            inputEditText.setTextColor(color);
            return this;
        }

        void cloneFrom(InputField inputField) {
            setHint(inputField.inputEditText.getHint());
            setText(inputField.inputEditText.getText());
            setTextType(inputField.inputEditText.getInputType());
            setTextColor(inputField.inputEditText.getCurrentTextColor());
        }

        public void destroy() {

        }

    }

    /*public static class InputField {
        static InputField addTo(ViewGroup container) {
            View view = LayoutInflater.from(container.getContext()).inflate(R.layout.dialog_input_element_gm4s, null);
            container.addView(view);
            return new InputField(view);
        }

        public final TextInputLayout textInputLayout;

        public InputField(View view) {
            if (view instanceof TextInputLayout) {
                textInputLayout = (TextInputLayout) view;
            } else {
                textInputLayout = view.findViewById(R.id.tv_input);
            }

        }

        public InputField getTextInputLayout(ResultCallback<TextInputLayout> callback) {
            callback.invoke(textInputLayout);
            return this;
        }

        public InputField setHint(int textId) {
            textInputLayout.setHint(textId);
            return this;
        }

        public InputField setHint(CharSequence text) {
            textInputLayout.setHint(text);
            return this;
        }

        public InputField setText(CharSequence text) {
            textInputLayout.getEditText().setText(text);
            return this;
        }

        *//**
     * {@link EditorInfo#inputType}
     *
     * @see android.text.InputType
     *//*
        public InputField setTextType(int type) {
            textInputLayout.getEditText().setInputType(type);
            return this;
        }

        public InputField setTextColor(int color) {
            textInputLayout.getEditText().setTextColor(color);
            return this;
        }

        void cloneFrom(InputField inputField) {
            setHint(inputField.textInputLayout.getHint());
            setText(inputField.textInputLayout.getEditText().getText());
            setTextType(inputField.textInputLayout.getEditText().getInputType());
            setTextColor(inputField.textInputLayout.getEditText().getCurrentTextColor());
        }

        public void destroy() {

        }

    }*/

}
