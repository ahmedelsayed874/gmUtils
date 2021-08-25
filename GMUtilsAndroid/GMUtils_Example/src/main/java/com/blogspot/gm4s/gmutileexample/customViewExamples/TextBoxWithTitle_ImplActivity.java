package com.blogspot.gm4s.gmutileexample.customViewExamples;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.blogspot.gm4s.gmutileexample.R;
import com.blogspot.gm4s.gmutileexample.databinding.ActivityTextboxWithTitleImplBinding;

import gmutils.ui.activities.BaseActivity;
import gmutils.ui.utils.ViewSource;

public class TextBoxWithTitle_ImplActivity extends BaseActivity {

    @NonNull
    @Override
    protected ViewSource getViewSource(@NonNull LayoutInflater inflater) {
        //return new ViewSource.LayoutResource(R.layout.activity_textbox_with_title_impl);
        return new ViewSource.ViewBinding(ActivityTextboxWithTitleImplBinding.inflate(inflater));
    }

    @Override
    public ActivityTextboxWithTitleImplBinding getViewBinding() {
        return (ActivityTextboxWithTitleImplBinding) super.getViewBinding();
    }

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityTextboxWithTitleImplBinding view = getViewBinding();
        //view.textboxW1.setTitle("Title-1"); //no need for this where we define in xml
        //view.textboxW1.setHint("Hint-1"); //no need for this where we define in xml
        //view.textboxW1.setText("Text-1");

        TextBoxWithTitle_Way2 txtBx21 = new TextBoxWithTitle_Way2(view.textboxW21.getRoot());
        //or: TextBoxWithTitle_Way2 txtBx21 = new TextBoxWithTitle_Way2(findViewById(R.id.textboxW2_1));
        txtBx21.setTitle("Title-21"); //we need to do that in code, where no way to do in xml
        txtBx21.setHint("Hint-21"); //we need to do that in code, where no way to do in xml
        //txtBx21.setText("Text-21");

        TextBoxWithTitle_Way2 txtBx22 = new TextBoxWithTitle_Way2(view.textboxW22.getRoot());
        //or: TextBoxWithTitle_Way2 txtBx22 = new TextBoxWithTitle_Way2(findViewById(R.id.textboxW2_2));
        txtBx22.setTitle("Title-22"); //we need to do that in code, where no way to do in xml
        txtBx22.setHint("Hint-22"); //we need to do that in code, where no way to do in xml
        //txtBx22.setText("Text-22");



        //add new instance
        //for: way1
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams1.topMargin = getResources().getDimensionPixelOffset(R.dimen.size_15);

        TextBoxWithTitle_Way1 newTxtBx = new TextBoxWithTitle_Way1(this);
        newTxtBx.setLayoutParams(layoutParams1);
        view.root.addView(newTxtBx);

        //for: way2
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams2.topMargin = getResources().getDimensionPixelOffset(R.dimen.size_15);

        TextBoxWithTitle_Way2 newTxtBxW2 = TextBoxWithTitle_Way2.create(view.root);
        newTxtBxW2.getRoot().setLayoutParams(layoutParams1);

    }
}
