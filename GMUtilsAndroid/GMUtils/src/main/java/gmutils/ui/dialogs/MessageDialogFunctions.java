package gmutils.ui.dialogs;


import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import org.json.JSONArray;

import gmutils.R;
import gmutils.listeners.ResultCallback;
import gmutils.storage.GeneralStorage;

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
public class MessageDialogFunctions {

    public interface Listener {
        View getView();
        void onButtonClick(MessageDialogFunctions dialog, int button);
    }


    private View lyContainer;
    private TextView tvTitle;
    private TextView tvMsg;
    private View lyDontShowAgain;
    private TextView tvDontShowAgain;
    private CheckBox chkDontShowAgain;
    private TextView tvButton1;
    private TextView tvButton2;
    private TextView tvButton3;

    private Listener mListener;


    MessageDialogFunctions(Listener listener) {
        this.mListener = listener;

        View view = listener.getView();
        lyContainer = view.findViewById(R.id.lyContainer);
        tvTitle = view.findViewById(R.id.tv_title);
        tvMsg = view.findViewById(R.id.tv_msg);
        lyDontShowAgain = view.findViewById(R.id.lyDontShowAgain);
        tvDontShowAgain = view.findViewById(R.id.tvDontShowAgain);
        chkDontShowAgain = view.findViewById(R.id.chkDontShowAgain);
        tvButton1 = view.findViewById(R.id.tv_btn1);
        tvButton2 = view.findViewById(R.id.tv_btn2);
        tvButton3 = view.findViewById(R.id.tv_btn3);

        tvButton1.setOnClickListener(v -> {
            if (mListener != null)
                mListener.onButtonClick(MessageDialogFunctions.this, 1);
        });

        tvButton2.setOnClickListener(v -> {
            if (mListener != null)
                mListener.onButtonClick(MessageDialogFunctions.this, 2);
        });

        tvButton3.setOnClickListener(v -> {
            if (mListener != null)
                mListener.onButtonClick(MessageDialogFunctions.this, 3);
        });

    }

    public MessageDialogFunctions setBackground(int resId) {
        lyContainer.setBackgroundResource(resId);
        return this;
    }

    public MessageDialogFunctions setBackgroundColor(int color) {
        lyContainer.setBackgroundColor(color);
        return this;
    }

    public MessageDialogFunctions setTextColor(int color) {
        tvTitle.setTextColor(color);
        tvMsg.setTextColor(color);
        tvDontShowAgain.setTextColor(color);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            chkDontShowAgain.setButtonTintList(ColorStateList.valueOf(color));
        }
        tvButton1.setTextColor(color);
        tvButton2.setTextColor(color);
        tvButton3.setTextColor(color);

        return this;
    }


    public MessageDialogFunctions setTitle(int msg) {
        tvTitle.setText(msg);
        tvTitle.setVisibility(View.VISIBLE);
        return this;
    }

    public MessageDialogFunctions setTitle(CharSequence msg) {
        tvTitle.setText(msg);
        tvTitle.setVisibility(View.VISIBLE);
        return this;
    }


    public MessageDialogFunctions setMessage(int msg) {
        tvMsg.setText(msg);
        return this;
    }

    public MessageDialogFunctions setMessage(CharSequence msg) {
        tvMsg.setText(msg);
        return this;
    }

    @SuppressLint("SetTextI18n")
    public MessageDialogFunctions appendMessage(CharSequence msg) {
        tvMsg.setText(tvMsg.getText() + " " + msg);
        return this;
    }

    public MessageDialogFunctions setMessageGravity(int gravity) {
        tvMsg.setGravity(gravity);
        return this;
    }

    public MessageDialogFunctions setMessageBold() {
        Typeface typeface = tvMsg.getTypeface();
        Typeface.create(typeface, Typeface.BOLD);
        tvMsg.setTypeface(typeface);
        return this;
    }


    //----------------------------------------------------------------------------------------------

    private boolean isShowingDisabled = false;

    @NonNull
    JSONArray getDontShowAgainCheckboxTagsArray() {
        GeneralStorage preferences = GeneralStorage.getInstance(MessageDialogFunctions.class.getName());
        String tagsJson = preferences.retrieve("TAGS", "[]");
        try {
            JSONArray tagsJsonArray = new JSONArray(tagsJson);
            return tagsJsonArray;
        } catch (Exception ignored) {
        }

        return new JSONArray();
    }

    void saveDontShowAgainCheckboxTagsArray(@NonNull JSONArray tagsJsonArray) {
        GeneralStorage preferences = GeneralStorage.getInstance(MessageDialogFunctions.class.getName());
        preferences.save("TAGS", tagsJsonArray.toString());
    }

    /**
     * @param tag      : unique number points to checkbox state
     * @param listener : sends {@MessageDialog, @Boolean that represents the result}
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public MessageDialogFunctions isDontShowAgainCheckboxDisabledByUser(int tag, ResultCallback<Boolean> listener) {
        JSONArray tagsJsonArray = getDontShowAgainCheckboxTagsArray();

        try {
            int foundedIndex = -1;

            for (int i = 0; i < tagsJsonArray.length(); i++) {
                if (tagsJsonArray.getInt(i) == tag) {
                    foundedIndex = i;
                    break;
                }
            }

            listener.invoke(foundedIndex != -1);
        } catch (Exception e) {
            listener.invoke(false);
        }

        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public MessageDialogFunctions dropDontShowAgainCheckboxDisabledByUser(int tag) {
        JSONArray tagsJsonArray = getDontShowAgainCheckboxTagsArray();

        try {
            int foundedIndex = -1;

            for (int i = 0; i < tagsJsonArray.length(); i++) {
                if (tagsJsonArray.getInt(i) == tag) {
                    foundedIndex = i;
                    break;
                }
            }

            if (foundedIndex != -1) {
                tagsJsonArray.remove(foundedIndex);
                saveDontShowAgainCheckboxTagsArray(tagsJsonArray);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    /**
     * @param tag unique number points to checkbox state
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public MessageDialogFunctions showDontShowAgainCheckbox(int tag) {
        isDontShowAgainCheckboxDisabledByUser(tag, (result) -> {
            isShowingDisabled = result;
            if (!isShowingDisabled) {

                lyDontShowAgain.setVisibility(View.VISIBLE);

                chkDontShowAgain.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    JSONArray tagsJsonArray = getDontShowAgainCheckboxTagsArray();

                    if (isChecked) { //add to disabled list
                        tagsJsonArray.put(tag);
                        saveDontShowAgainCheckboxTagsArray(tagsJsonArray);

                    } else {
                        for (int i = 0; i < tagsJsonArray.length(); i++) {
                            if (tagsJsonArray.optInt(i, -1) == tag) {
                                tagsJsonArray.remove(i);
                                saveDontShowAgainCheckboxTagsArray(tagsJsonArray);
                                break;
                            }
                        }
                    }
                });

            }
        });

        return this;
    }

    /**
     * @param tag unique number points to checkbox state
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public MessageDialogFunctions showDontShowAgainCheckbox(int tag, CharSequence msg) {
        showDontShowAgainCheckbox(tag);
        tvDontShowAgain.setText(msg);
        return this;
    }

    //----------------------------------------------------------------------------------------------

    public MessageDialogFunctions setButton1(int textId) {
        if (textId != 0) {
            return setButton1(tvButton1.getResources().getString(textId));
        }
        return this;
    }

    public MessageDialogFunctions setButton1(CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            tvButton1.setText(text);
            tvButton1.setVisibility(View.VISIBLE);
        } else {
            tvButton1.setVisibility(View.GONE);
        }

        return this;
    }

    public MessageDialogFunctions setButton2(int textId) {
        if (textId != 0) {
            return setButton2(tvButton2.getResources().getString(textId));
        }
        return this;
    }

    public MessageDialogFunctions setButton2(CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            tvButton2.setText(text);
            tvButton2.setVisibility(View.VISIBLE);
        } else {
            tvButton2.setVisibility(View.GONE);
        }

        return this;
    }

    public MessageDialogFunctions setButton3(int textId) {
        if (textId != 0) {
            return setButton3(tvButton3.getResources().getString(textId));
        }
        return this;
    }

    public MessageDialogFunctions setButton3(CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            tvButton3.setText(text);
            tvButton3.setVisibility(View.VISIBLE);
        } else {
            tvButton3.setVisibility(View.GONE);
        }

        return this;
    }

    //----------------------------------------------------------------------------------------------

    void copyProperties(MessageDialogFunctions from) {
        this.lyContainer.setBackground(from.lyContainer.getBackground());
        this.setTextColor(from.tvTitle.getCurrentTextColor());
        this.setTitle(from.tvTitle.getText());
        this.setMessage(from.tvMsg.getText());
        this.setMessageGravity(from.tvMsg.getGravity());
        this.tvMsg.setTypeface(from.tvMsg.getTypeface());
        this.setButton1(from.tvButton1.getText());
        this.setButton2(from.tvButton2.getText());
        this.setButton3(from.tvButton3.getText());
    }

    //----------------------------------------------------------------------------------------------

    public void onDestroy() {
        this.lyContainer = null;
        this.tvTitle = null;
        this.tvMsg = null;
        this.lyDontShowAgain = null;
        this.tvDontShowAgain = null;
        this.chkDontShowAgain = null;
        this.tvButton1 = null;
        this.tvButton2 = null;
        this.tvButton3 = null;

        this.mListener = null;
    }
}
