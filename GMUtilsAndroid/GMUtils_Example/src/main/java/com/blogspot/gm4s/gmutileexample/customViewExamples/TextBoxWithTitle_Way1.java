package com.blogspot.gm4s.gmutileexample.customViewExamples;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.blogspot.gm4s.gmutileexample.R;

public class TextBoxWithTitle_Way1 extends LinearLayout {

    private TextView titleTv;
    private EditText inputEt;

    public TextBoxWithTitle_Way1(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public TextBoxWithTitle_Way1(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public TextBoxWithTitle_Way1(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TextBoxWithTitle_Way1(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        LayoutInflater.from(context).inflate(R.layout.custom_view_textbox_with_title, this);
        //there is other way to fill the view with component by initiating each View and its layout params
        //but it need more experience
        //used way is easy and fast

        titleTv = findViewById(R.id.titleTv);
        inputEt = findViewById(R.id.inputEt);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextBoxWithTitle_Way1, defStyleAttr, defStyleRes);

        String title = typedArray.getString(R.styleable.TextBoxWithTitle_Way1_title);
        setTitle(title);

        String hint = typedArray.getString(R.styleable.TextBoxWithTitle_Way1_hint);
        setHint(hint);

        typedArray.recycle();
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
