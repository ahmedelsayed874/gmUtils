package com.blogspot.gm4s1.gmutils.dialogs;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blogspot.gm4s1.gmutils.R;
import com.blogspot.gm4s1.gmutils._bases.BaseDialog;
import com.blogspot.gm4s1.gmutils.listeners.ActionCallback;
import com.blogspot.gm4s1.gmutils.storage.GeneralStorage;

import org.json.JSONArray;

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

    private View lyContainer;
    private TextView tvTitle;
    private TextView tvMsg;
    private View lyDontShowAgain;
    private TextView tvDontShowAgain;
    private CheckBox chkDontShowAgain;
    private TextView tvButton1;
    private TextView tvButton2;
    private TextView tvButton3;

    private Listener listenerBtn1;
    private Listener listenerBtn2;
    private Listener listenerBtn3;


    @NonNull
    @Override
    public View createView(LayoutInflater layoutInflater) {
        return layoutInflater.inflate(R.layout.dialog_message, null);
    }

    MessageDialog(Context context) {
        super(context);

        View view = getView();
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
            if (listenerBtn1 != null)
                listenerBtn1.invoke(MessageDialog.this);
            dismiss();
        });

        tvButton2.setOnClickListener(v -> {
            if (listenerBtn2 != null)
                listenerBtn2.invoke(MessageDialog.this);
            dismiss();
        });

        tvButton3.setOnClickListener(v -> {
            if (listenerBtn3 != null)
                listenerBtn3.invoke(MessageDialog.this);
            dismiss();
        });

    }

    public MessageDialog setBackground(int resId) {
        lyContainer.setBackgroundResource(resId);
        return this;
    }

    public MessageDialog setTextColor(int color) {
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


    public MessageDialog setTitle(int msg) {
        tvTitle.setText(msg);
        tvTitle.setVisibility(View.VISIBLE);
        return this;
    }

    public MessageDialog setTitle(CharSequence msg) {
        tvTitle.setText(msg);
        tvTitle.setVisibility(View.VISIBLE);
        return this;
    }


    public MessageDialog setMessage(int msg) {
        tvMsg.setText(msg);
        return this;
    }

    public MessageDialog setMessage(CharSequence msg) {
        tvMsg.setText(msg);
        return this;
    }

    @SuppressLint("SetTextI18n")
    public MessageDialog appendMessage(CharSequence msg) {
        tvMsg.setText(tvMsg.getText() + " " + msg);
        return this;
    }

    public MessageDialog setMessageGravity(int gravity) {
        tvMsg.setGravity(gravity);
        return this;
    }

    public MessageDialog setMessageBold() {
        Typeface typeface = tvMsg.getTypeface();
        Typeface.create(typeface, Typeface.BOLD);
        return this;
    }


    //----------------------------------------------------------------------------------------------

    private boolean isShowingDisabled = false;

    @NonNull
    private JSONArray getDontShowAgainCheckboxTagsArray() {
        GeneralStorage preferences = GeneralStorage.getInstance(MessageDialog.class.getName());
        String tagsJson = preferences.retrieve("TAGS", "[]");
        try {
            JSONArray tagsJsonArray = new JSONArray(tagsJson);
            return tagsJsonArray;
        } catch (Exception ignored) {
        }

        return new JSONArray();
    }

    private void saveDontShowAgainCheckboxTagsArray(@NonNull JSONArray tagsJsonArray) {
        GeneralStorage preferences = GeneralStorage.getInstance(MessageDialog.class.getName());
        preferences.save("TAGS", tagsJsonArray.toString());
    }

    /**
     * @param tag      : unique number points to checkbox state
     * @param listener : sends {@MessageDialog, @Boolean that represents the result} and return an action indicator to remove saved tag or leave
     */
    public MessageDialog isDontShowAgainCheckboxDisabledByUser(int tag, ActionCallback<Pair<MessageDialog, Boolean>, Boolean> listener) {
        JSONArray tagsJsonArray = getDontShowAgainCheckboxTagsArray();

        try {
            int foundedIndex = -1;

            for (int i = 0; i < tagsJsonArray.length(); i++) {
                if (tagsJsonArray.getInt(i) == tag) {
                    foundedIndex = i;
                    break;
                }
            }

            Boolean remove = listener.invoke(new Pair<>(MessageDialog.this, foundedIndex != -1));
            if (foundedIndex != -1) {
                if (remove != null && remove) {
                    tagsJsonArray.remove(foundedIndex);
                    saveDontShowAgainCheckboxTagsArray(tagsJsonArray);
                }
            }
        } catch (Exception e) {
            listener.invoke(new Pair<>(MessageDialog.this, false));
        }

        return this;
    }

    /**
     * @param tag unique number points to checkbox state
     */
    public MessageDialog showDontShowAgainCheckbox(int tag) {
        isDontShowAgainCheckboxDisabledByUser(tag, result -> {
            isShowingDisabled = result.second;
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
            return null;
        });

        return this;
    }

    /**
     * @param tag unique number points to checkbox state
     */
    public MessageDialog showDontShowAgainCheckbox(int tag, CharSequence msg) {
        showDontShowAgainCheckbox(tag);
        tvDontShowAgain.setText(msg);
        return this;
    }

    //----------------------------------------------------------------------------------------------

    public MessageDialog setButton1(int textId, Listener listener) {
        if (textId != 0) {
            return setButton1(tvButton1.getResources().getString(textId), listener);
        }
        return this;
    }

    public MessageDialog setButton1(CharSequence text, Listener listener) {
        if (!TextUtils.isEmpty(text)) {
            tvButton1.setText(text);
            tvButton1.setVisibility(View.VISIBLE);
            this.listenerBtn1 = listener;
        } else {
            tvButton1.setVisibility(View.GONE);
            this.listenerBtn1 = null;
        }

        return this;
    }

    public MessageDialog setButton2(int textId, Listener listener) {
        if (textId != 0) {
            return setButton2(tvButton2.getResources().getString(textId), listener);
        }
        return this;
    }

    public MessageDialog setButton2(CharSequence text, Listener listener) {
        if (!TextUtils.isEmpty(text)) {
            tvButton2.setText(text);
            tvButton2.setVisibility(View.VISIBLE);
            this.listenerBtn2 = listener;
        } else {
            tvButton2.setVisibility(View.GONE);
            this.listenerBtn2 = null;
        }

        return this;
    }

    public MessageDialog setButton3(int textId, Listener listener) {
        if (textId != 0) {
            return setButton3(tvButton3.getResources().getString(textId), listener);
        }
        return this;
    }

    public MessageDialog setButton3(CharSequence text, Listener listener) {
        if (!TextUtils.isEmpty(text)) {
            tvButton3.setText(text);
            tvButton3.setVisibility(View.VISIBLE);
            this.listenerBtn3 = listener;
        } else {
            tvButton3.setVisibility(View.GONE);
            this.listenerBtn3 = null;
        }

        return this;
    }


    public MessageDialog setCancelable(boolean cancellable) {
        super.setCancelable(cancellable);
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
        this.lyContainer = null;
        this.tvTitle = null;
        this.tvMsg = null;
        this.lyDontShowAgain = null;
        this.tvDontShowAgain = null;
        this.chkDontShowAgain = null;
        this.tvButton1 = null;
        this.tvButton2 = null;
        this.tvButton3 = null;

        this.listenerBtn1 = null;
        this.listenerBtn2 = null;
        this.listenerBtn3 = null;
    }
}
