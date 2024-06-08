package com.blogspot.gm4s.gmutileexample.customViewExamples;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.blogspot.gm4s.gmutileexample.R;

public class TextBoxWithTitle_Way2 {

    public static TextBoxWithTitle_Way2 create(ViewGroup container) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.custom_view_textbox_with_title, null);
        container.addView(view);
        return new TextBoxWithTitle_Way2(view);
    }

    //----------------------------------------------------------------------------------------------

    private View view;
    private final TextView titleTv;
    private final EditText inputEt;


    public TextBoxWithTitle_Way2(View view) {
        titleTv = view.findViewById(R.id.titleTv);
        inputEt = view.findViewById(R.id.inputEt);
    }

    public View getRoot() {
        return view;
    }

    public void setTitle(CharSequence title) {
        titleTv.setText(title);
    }

    public CharSequence getTitle() {
        return titleTv.getText();
    }

    public void setHint(CharSequence hint) {
        inputEt.setHint(hint);
    }

    public CharSequence getHint() {
        return inputEt.getHint();
    }

    public void setText(CharSequence hint) {
        inputEt.setText(hint);
    }

    public CharSequence getText() {
        return inputEt.getText();
    }
}
