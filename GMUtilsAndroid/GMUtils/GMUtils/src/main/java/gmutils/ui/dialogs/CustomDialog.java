package gmutils.ui.dialogs;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.CompoundButtonCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gmutils.R;
import gmutils.listeners.ResultCallback;

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
public class CustomDialog extends BaseDialog {
    private final ScrollView scrollView;
    private final LinearLayout root;
    private final TextView titleTv;

    @NonNull
    @Override
    protected View createView(LayoutInflater layoutInflater) {
        return layoutInflater.inflate(
                R.layout.dialog_custom,
                null
        );
    }

    public CustomDialog(Context context) {
        super(context);

        scrollView = getView().findViewById(R.id.scrollView);
        root = getView().findViewById(R.id.root);
        titleTv = getView().findViewById(R.id.titleTv);

        titleTv.setVisibility(View.GONE);
    }

    @Override
    protected void onViewCreated(View view) {
        super.onViewCreated(view);
    }

    //==============================================================================================

    public CustomDialog setWidth(int width) {
        ViewGroup.LayoutParams layoutParams = root.getLayoutParams();
        layoutParams.width = width;
        root.setLayoutParams(layoutParams);
        return this;
    }

    public CustomDialog setMaxHeight(int maxHeight) {
        ViewGroup.LayoutParams layoutParams = scrollView.getLayoutParams();
        layoutParams.height = maxHeight;
        scrollView.setLayoutParams(layoutParams);
        return this;
    }

    public CustomDialog setTitle(int title) {
        titleTv.setText(title);
        titleTv.setVisibility(View.VISIBLE);
        return this;
    }

    public CustomDialog setTitle(CharSequence title) {
        titleTv.setText(title);
        titleTv.setVisibility(View.VISIBLE);
        return this;
    }

    public CustomDialog setTitleColor(int color) {
        titleTv.setTextColor(color);
        return this;
    }

    @Override
    public CustomDialog setTitleColorRes(int resid) {
        titleTv.setTextColor(ContextCompat.getColor(titleTv.getContext(), resid));
        return this;
    }

    @Override
    public CustomDialog setTextColorRes(int resid) {
        throw new UnsupportedOperationException("CustomDialog does not support setTextColorRes.");
    }

    public CustomDialog addChild(View view) {
        root.addView(view);
        return this;
    }

    public CustomDialog addChild(BaseView child) {
        root.addView(child.build(root.getContext()));
        return this;
    }

    public CustomDialog addChildren(BaseView... children) {
        for (BaseView child : children) {
            root.addView(child.build(root.getContext()));
        }
        return this;
    }

    //==============================================================================================

    public CustomDialog getRoot(ResultCallback<LinearLayout> callback) {
        callback.invoke(root);
        return this;
    }

    public CustomDialog getTitleView(ResultCallback<TextView> callback) {
        callback.invoke(titleTv);
        return this;
    }

    //==============================================================================================

    @Override
    protected void onDestroy() {
    }

    @Override
    protected BaseDialog reinitialize(Context context) {
        throw new UnsupportedOperationException("CustomDialog does not support reinitialization.");
    }

    //==============================================================================================

    public static abstract class BaseView {
        private Integer width;
        private Integer height;
        private Integer bgColor;
        private Integer bgRes;
        private Integer paddingLeft;
        private Integer paddingTop;
        private Integer paddingRight;
        private Integer paddingBottom;
        private View.OnClickListener onClickListener;
        private View.OnLongClickListener onLongClickListener;
        private View.OnTouchListener onTouchListener;

        public BaseView setWidth(int width) {
            this.width = width;
            return this;
        }

        public BaseView setHeight(Integer height) {
            this.height = height;
            return this;
        }

        public BaseView setBackgroundColor(int color) {
            this.bgColor = color;
            this.bgRes = null;
            return this;
        }

        public BaseView setBackgroundResource(int resId) {
            this.bgColor = null;
            this.bgRes = resId;
            return this;
        }

        public BaseView setPadding(
                Integer paddingLeft,
                Integer paddingTop,
                Integer paddingRight,
                Integer paddingBottom
        ) {
            this.paddingLeft = paddingLeft;
            this.paddingTop = paddingTop;
            this.paddingRight = paddingRight;
            this.paddingBottom = paddingBottom;
            return this;
        }

        public BaseView setOnClickListener(View.OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
            return this;
        }

        public BaseView setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
            this.onLongClickListener = onLongClickListener;
            return this;
        }

        public BaseView setOnTouchListener(View.OnTouchListener onTouchListener) {
            this.onTouchListener = onTouchListener;
            return this;
        }

        protected abstract View build(Context context);

        protected void setupView(View view) {
            view.setLayoutParams(new LinearLayout.LayoutParams(
                    width != null ? width : LinearLayout.LayoutParams.WRAP_CONTENT,
                    height != null ? height : LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            if (bgColor != null) {
                view.setBackgroundColor(bgColor);
            }
            //
            else if (bgRes != null) {
                view.setBackgroundResource(bgRes);
            }

            view.setPadding(
                    paddingLeft != null ? paddingLeft : view.getPaddingLeft(),
                    paddingTop != null ? paddingTop : view.getPaddingTop(),
                    paddingRight != null ? paddingRight : view.getPaddingRight(),
                    paddingBottom != null ? paddingBottom : view.getPaddingBottom()
            );

            view.setOnClickListener(onClickListener);
            view.setOnLongClickListener(onLongClickListener);
            view.setOnTouchListener(onTouchListener);
        }
    }

    public static class Text extends BaseView {
        private Integer textRes;
        private CharSequence text;
        private Integer color;
        private Typeface typeface;
        private Float textSizeSp;

        @Override
        public Text setWidth(int width) {
            return (Text) super.setWidth(width);
        }

        @Override
        public Text setHeight(Integer height) {
            return (Text) super.setHeight(height);
        }

        public Text setTextRes(int textRes) {
            this.textRes = textRes;
            this.text = null;
            return this;
        }

        public Text setText(CharSequence text) {
            this.textRes = null;
            this.text = text;
            return this;
        }

        public Text setColor(int color) {
            this.color = color;
            return this;
        }

        public Text setTypeface(Typeface typeface) {
            this.typeface = typeface;
            return this;
        }

        public Text setTextSizeInSp(float textSizeSp) {
            this.textSizeSp = textSizeSp;
            return this;
        }

        @Override
        public Text setBackgroundColor(int color) {
            return (Text) super.setBackgroundColor(color);
        }

        @Override
        public Text setBackgroundResource(int resId) {
            return (Text) super.setBackgroundResource(resId);
        }

        @Override
        public Text setPadding(
                Integer paddingLeft,
                Integer paddingTop,
                Integer paddingRight,
                Integer paddingBottom
        ) {
            return (Text) super.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }

        @Override
        protected View build(Context context) {
            TextView textView = new TextView(context);
            setupTextView(textView);
            return textView;
        }

        protected void setupTextView(TextView textView) {
            setupView(textView);

            if (textRes != null) {
                textView.setText(textRes);
            }
            //
            else if (text != null) {
                textView.setText(text);
            }
            if (color != null) {
                textView.setTextColor(color);
            }
            if (typeface != null) {
                textView.setTypeface(typeface);
            }
            if (textSizeSp != null) {
                textView.setTextSize(textSizeSp);
            }
        }
    }

    public static class TextField extends Text {
        private Integer hintRes;
        private CharSequence hint;
        private Integer hintColor;

        public Text setHintRes(int hintRes) {
            this.hintRes = hintRes;
            this.hint = null;
            return this;
        }

        public Text setHint(CharSequence hint) {
            this.hintRes = null;
            this.hint = hint;
            return this;
        }

        public Text setHintColor(Integer color) {
            this.hintColor = color;
            return this;
        }

        @Override
        public TextField setWidth(int width) {
            return (TextField) super.setWidth(width);
        }

        @Override
        public TextField setHeight(Integer height) {
            return (TextField) super.setHeight(height);
        }

        @Override
        public TextField setBackgroundColor(int color) {
            return (TextField) super.setBackgroundColor(color);
        }

        @Override
        public TextField setBackgroundResource(int resId) {
            return (TextField) super.setBackgroundResource(resId);
        }

        @Override
        public TextField setPadding(
                Integer paddingLeft,
                Integer paddingTop,
                Integer paddingRight,
                Integer paddingBottom
        ) {
            return (TextField) super.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }

        @Override
        protected View build(Context context) {
            EditText editText = new EditText(context);

            setupTextView(editText);

            if (hintRes != null) {
                editText.setHint(hintRes);
            }
            //
            else if (hint != null) {
                editText.setHint(hint);
            }

            if (hintColor != null) {
                editText.setHintTextColor(hintColor);
            }

            return editText;
        }
    }

    public static class Checkbox extends Text {
        private Integer checkboxColor;

        public Checkbox setCheckboxColor(Integer checkboxColor) {
            this.checkboxColor = checkboxColor;
            return this;
        }

        @Override
        public Checkbox setWidth(int width) {
            return (Checkbox) super.setWidth(width);
        }

        @Override
        public Checkbox setHeight(Integer height) {
            return (Checkbox) super.setHeight(height);
        }

        @Override
        public Checkbox setBackgroundColor(int color) {
            return (Checkbox) super.setBackgroundColor(color);
        }

        @Override
        public Checkbox setBackgroundResource(int resId) {
            return (Checkbox) super.setBackgroundResource(resId);
        }

        @Override
        public Checkbox setPadding(
                Integer paddingLeft,
                Integer paddingTop,
                Integer paddingRight,
                Integer paddingBottom
        ) {
            return (Checkbox) super.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }

        @Override
        protected View build(Context context) {
            CheckBox checkBox = new CheckBox(context);

            setupTextView(checkBox);

            if (checkboxColor != null) {
                CompoundButtonCompat.setButtonTintList(
                        checkBox,
                        ColorStateList.valueOf(checkboxColor)
                );
            }

            return checkBox;
        }
    }

    public static class Options extends BaseView {
        private Integer checkColor;
        private List<Text> options = new ArrayList<>();

        public Options setCheckColor(Integer color) {
            this.checkColor = color;
            return this;
        }

        public Options addOption(Text text, View.OnClickListener onClick) {
            text.setOnClickListener(onClick);
            options.add(text);
            return this;
        }

        @Override
        public Options setWidth(int width) {
            return (Options) super.setWidth(width);
        }

        @Override
        public Options setHeight(Integer height) {
            return (Options) super.setHeight(height);
        }

        @Override
        public Options setBackgroundColor(int color) {
            return (Options) super.setBackgroundColor(color);
        }

        @Override
        public Options setBackgroundResource(int resId) {
            return (Options) super.setBackgroundResource(resId);
        }

        @Override
        public Options setPadding(
                Integer paddingLeft,
                Integer paddingTop,
                Integer paddingRight,
                Integer paddingBottom
        ) {
            return (Options) super.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }

        @Override
        protected View build(Context context) {
            RadioGroup radioGroup = new RadioGroup(context);

            for (Text option : options) {
                RadioButton radioButton = new RadioButton(context);
                option.setupTextView(radioButton);
                if (checkColor != null) {
                    CompoundButtonCompat.setButtonTintList(
                            radioButton,
                            ColorStateList.valueOf(checkColor)
                    );
                }
                radioGroup.addView(radioButton);
            }

            setupView(radioGroup);

            return radioGroup;
        }
    }

    public static class Button extends Text {
        private Integer tintColor;

        public Button setTintColor(Integer tintColor) {
            this.tintColor = tintColor;
            return this;
        }

        @Override
        public Button setWidth(int width) {
            return (Button) super.setWidth(width);
        }

        @Override
        public Button setHeight(Integer height) {
            return (Button) super.setHeight(height);
        }

        @Override
        public Button setBackgroundColor(int color) {
            return (Button) super.setBackgroundColor(color);
        }

        @Override
        public Button setBackgroundResource(int resId) {
            return (Button) super.setBackgroundResource(resId);
        }

        @Override
        public Button setPadding(
                Integer paddingLeft,
                Integer paddingTop,
                Integer paddingRight,
                Integer paddingBottom
        ) {
            return (Button) super.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }

        @Override
        protected View build(Context context) {
            android.widget.Button button = new android.widget.Button(context);

            setupTextView(button);

            if (tintColor != null) {
                ViewCompat.setBackgroundTintList(
                        button,
                        ColorStateList.valueOf(tintColor)
                );
            }

            return button;
        }
    }

    public static class Row extends BaseView {
        private List<BaseView> children = new ArrayList<>();

        public Row addChild(BaseView child) {
            this.children.add(child);
            return this;
        }

        public Row addChildren(BaseView... children) {
            this.children.addAll(Arrays.asList(children));
            return this;
        }

        @Override
        public Row setWidth(int width) {
            return (Row) super.setWidth(width);
        }

        @Override
        public Row setHeight(Integer height) {
            return (Row) super.setHeight(height);
        }

        @Override
        public Row setBackgroundColor(int color) {
            return (Row) super.setBackgroundColor(color);
        }

        @Override
        public Row setBackgroundResource(int resId) {
            return (Row) super.setBackgroundResource(resId);
        }

        @Override
        public Row setPadding(
                Integer paddingLeft,
                Integer paddingTop,
                Integer paddingRight,
                Integer paddingBottom
        ) {
            return (Row) super.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }

        @Override
        protected View build(Context context) {
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            setupView(linearLayout);

            for (BaseView child : children) {
                linearLayout.addView(child.build(context));
            }

            return linearLayout;
        }
    }

    public static class Scroll extends BaseView {
        private boolean isVertical = true;
        private BaseView child;

        public void setVertical(boolean vertical) {
            this.isVertical = vertical;
        }

        public Scroll addChild(BaseView child) {
            this.child = child;
            return this;
        }

        @Override
        public Scroll setWidth(int width) {
            return (Scroll) super.setWidth(width);
        }

        @Override
        public Scroll setHeight(Integer height) {
            return (Scroll) super.setHeight(height);
        }

        @Override
        public Scroll setBackgroundColor(int color) {
            return (Scroll) super.setBackgroundColor(color);
        }

        @Override
        public Scroll setBackgroundResource(int resId) {
            return (Scroll) super.setBackgroundResource(resId);
        }

        @Override
        public Scroll setPadding(
                Integer paddingLeft,
                Integer paddingTop,
                Integer paddingRight,
                Integer paddingBottom
        ) {
            return (Scroll) super.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }

        @Override
        protected View build(Context context) {
            ViewGroup view;

            if (isVertical) {
                view = new ScrollView(context);
            } else {
                view = new HorizontalScrollView(context);
            }

            setupView(view);

            view.addView(child.build(context));

            return view;
        }
    }

    public static class BasicView extends BaseView {

        public BasicView(int width, int height) {
            this.setWidth(width);
            this.setHeight(height);
        }

        public BasicView(int width, int height, int bgRes) {
            this.setWidth(width);
            this.setHeight(height);
            this.setBackgroundResource(bgRes);
        }

        public BasicView(int width, int height, int bgRes, Integer paddingLeft, Integer paddingTop, Integer paddingRight, Integer paddingBottom) {
            this.setWidth(width);
            this.setHeight(height);
            this.setBackgroundResource(bgRes);
            this.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }

        public BasicView(int width, int height, int bgRes, Integer paddingLeft, Integer paddingTop, Integer paddingRight, Integer paddingBottom, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener, View.OnTouchListener onTouchListener) {
            this.setWidth(width);
            this.setHeight(height);
            this.setBackgroundResource(bgRes);
            this.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
            this.setOnClickListener(onClickListener);
            this.setOnLongClickListener(onLongClickListener);
            this.setOnTouchListener(onTouchListener);
        }


        @Override
        public BasicView setWidth(int width) {
            return (BasicView) super.setWidth(width);
        }

        @Override
        public BasicView setHeight(Integer height) {
            return (BasicView) super.setHeight(height);
        }

        @Override
        public BasicView setBackgroundColor(int color) {
            return (BasicView) super.setBackgroundColor(color);
        }

        @Override
        public BasicView setBackgroundResource(int resId) {
            return (BasicView) super.setBackgroundResource(resId);
        }

        @Override
        public BasicView setPadding(
                Integer paddingLeft,
                Integer paddingTop,
                Integer paddingRight,
                Integer paddingBottom
        ) {
            return (BasicView) super.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }

        @Override
        public BasicView setOnClickListener(View.OnClickListener onClickListener) {
            return (BasicView) super.setOnClickListener(onClickListener);
        }

        @Override
        public BasicView setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
            return (BasicView) super.setOnLongClickListener(onLongClickListener);
        }

        @Override
        public BasicView setOnTouchListener(View.OnTouchListener onTouchListener) {
            return (BasicView) super.setOnTouchListener(onTouchListener);
        }

        @Override
        protected View build(Context context) {
            View view = new View(context);
            setupView(view);
            return view;
        }

    }
}
