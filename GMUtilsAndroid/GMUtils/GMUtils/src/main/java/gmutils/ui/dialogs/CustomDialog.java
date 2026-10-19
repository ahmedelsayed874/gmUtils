package gmutils.ui.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Editable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.CompoundButtonCompat;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import gmutils.R;
import gmutils.collections.values.Value4;
import gmutils.images.ImageLoader;
import gmutils.listeners.ActionCallback2;
import gmutils.listeners.ActionCallback3;
import gmutils.listeners.ResultCallback;
import gmutils.listeners.ResultCallback2;
import gmutils.listeners.TextChangedListener;

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

    public CustomDialog getRoot(ResultCallback<LinearLayout> callback) {
        callback.invoke(root);
        return this;
    }

    public CustomDialog setWidth(int width) {
        ViewGroup.LayoutParams layoutParams = root.getLayoutParams();
        layoutParams.width = width;
        root.setLayoutParams(layoutParams);
        return this;
    }

    public CustomDialog setWidthBy(int dimenId) {
        int width = root.getContext().getResources().getDimensionPixelSize(dimenId);
        return setWidth(width);
    }

    public CustomDialog setWidthToMax(int horizontalMargin) {
        int size = root.getContext().getResources().getDisplayMetrics().widthPixels;
        return setWidth(size - horizontalMargin * 2);
    }

    public CustomDialog setHeight(int maxHeight) {
        ViewGroup.LayoutParams layoutParams = scrollView.getLayoutParams();
        layoutParams.height = maxHeight;
        scrollView.setLayoutParams(layoutParams);
        return this;
    }

    public CustomDialog setHeightBy(int dimenId) {
        int height = root.getContext().getResources().getDimensionPixelSize(dimenId);
        return setHeight(height);
    }

    public CustomDialog setHeightToMax(int verticalMargin) {
        int size = root.getContext().getResources().getDisplayMetrics().heightPixels;
        return setWidth(size - verticalMargin * 2);
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

    public CustomDialog setTitleBy(int stringId) {
        titleTv.setText(stringId);
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

    public CustomDialog getTitleView(ResultCallback<TextView> callback) {
        callback.invoke(titleTv);
        return this;
    }

    //----------------------------------------------------------------------------------------------

    private final List<BaseView<?>> children = new ArrayList<>();
    private Event _event;

    public CustomDialog addChild(@NotNull CustomDialog.BaseViewBuilder<?> child) {
        if (_event == null) {
            _event = (viewBuilder, triggeredBy) -> {
                informObserver(new ViewUpdateArgs(
                        new ArrayList<>(children),
                        //c,
                        viewBuilder,
                        triggeredBy
                ));

                if (!allowDestroy) {
                    allowDestroy = true;
                    onDestroy();
                }
            };
        }

        View build = child.build(root.getContext(), _event);
        children.add(child);
        root.addView(build);

        return this;
    }

    public CustomDialog addChildren(@NotNull BaseViewBuilder<?>... children) {
        for (BaseViewBuilder<?> child : children) {
            addChild(child);
        }
        return this;
    }

    public void dispatchStateUpdated(BaseView<?> view, String triggeredBy) {
        if (_event != null) _event.onStatusChanged(view, triggeredBy);
    }

    //==============================================================================================

    ResultCallback<ViewUpdateArgs> updateObserver;

    public CustomDialog subscribeOnUpdate(ResultCallback<ViewUpdateArgs> updateObserver) {
        this.updateObserver = updateObserver;
        return this;
    }

    final void informObserver(ViewUpdateArgs args) {
        if (this.updateObserver != null) this.updateObserver.invoke(args);
    }

    //==============================================================================================

    private boolean allowDestroy = true;

    @Override
    public void dismiss(String dismissedBy) {
        allowDestroy = _event == null;
        super.dismiss(dismissedBy);
    }

    @Override
    protected void onDestroy() {
        if (!allowDestroy) return;

        for (BaseView child : children) {
            child.destroy();
        }
        children.clear();
        _event = null;
    }

    @Override
    protected BaseDialog reinitialize(Context context) {
        throw new UnsupportedOperationException("CustomDialog does not support reinitialization.");
    }

    //\/////////////////////////////////////////////////////////////////////////////////////////////

    public interface Event {
        void onStatusChanged(
                @NotNull BaseView view,
                @NotNull String triggeredBy
        );
    }

    public static class ViewUpdateArgs {
        public final List<BaseView> children;
        public final BaseView updatedView;
        public final String triggeredBy;

        public ViewUpdateArgs(
                @NotNull List<BaseView> children,
                @NotNull BaseView updatedView,
                @NotNull String triggeredBy
        ) {
            this.children = children;
            this.updatedView = updatedView;
            this.triggeredBy = triggeredBy;
        }

        public BaseView getViewById(int id) {
            Integer id2 = id;

            for (BaseView child : children) {
                if (id2.equals( child.id)) {
                    return child;
                }
            }

            return null;
        }
    }

    //-----------------------------------------------

    public static abstract class ViewSize {
        public final float value;

        public ViewSize(float value) {
            this.value = value;
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " | value = " + value;
        }

        public static ViewSize wrapContent() {
            return new Absolute(ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        public static ViewSize matchParent() {
            return new Absolute(ViewGroup.LayoutParams.MATCH_PARENT);
        }

        public static ViewSize absolute(int value) {
            return new Absolute(value);
        }

        public static ViewSize percentage(float value) {
            return new Percentage(value);
        }

        static class Absolute extends ViewSize {
            public Absolute(int value) {
                super(value);
            }
        }

        static class Percentage extends ViewSize {
            public Percentage(float value) {
                super(value);
            }
        }
    }

    //-----------------------------------------------

    public static abstract class BaseView<NView extends View> {
        Integer id;
        private Object extra;
        private BaseView<?> parent;
        boolean isEnabled = true;
        NView nativeView;

        //--------------------------------------------------

        public final BaseView<NView> setId(int id) {
            this.id = id;
            if (nativeView != null) nativeView.setId(id);
            return this;
        }

        public Integer getId() {
            if (nativeView != null) return nativeView.getId();
            return id;
        }

        //--------------------------------------------------

        public final BaseView<NView> setExtra(Object extra) {
            this.extra = extra;
            return this;
        }

        public final Object getExtra() {
            return extra;
        }

        //--------------------------------------------------

        protected final BaseView<NView> setParent(BaseView<?> parent) {
            this.parent = parent;
            return this;
        }

        public BaseView<?> getParent() {
            return parent;
        }

        //--------------------------------------------------

        public NView getNativeView() {
            return nativeView;
        }

        //--------------------------------------------------

        public final BaseView<NView> setEnabled(boolean enabled) {
            isEnabled = enabled;

            if (nativeView != null) {
                nativeView.setEnabled(enabled);

                List<BaseView<?>> subViews = getSubViews();
                if (subViews != null) {
                    for (BaseView<?> subView : subViews) {
                        subView.setEnabled(enabled);
                    }
                }
            }

            return this;
        }

        public final boolean isEnabled() {
            if (nativeView != null) return nativeView.isEnabled();
            return isEnabled;
        }

        //--------------------------------------------------

        public List<BaseView<?>> getSubViews() {
            return null;
        }

        //--------------------------------------------------

        public BaseView<NView> apply(@NotNull ResultCallback<BaseView<NView>> callback) {
            callback.invoke(this);
            return this;
        }

        //--------------------------------------------------

        public abstract Object getValue();

        //------------------------------------------------------------------------------------------

        protected void destroy() {
            List<BaseView<?>> subViews = getSubViews();
            if (subViews != null) {
                for (BaseView<?> child : subViews) {
                    if (child != null) child.destroy();
                }
            }

            id = null;
            extra = null;
            parent = null;
            nativeView = null;
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + "\n" +
                    "id = " + id + "\n" +
                    "extra = " + extra + "\n" +
                    "nativeView = " + nativeView + "\n" +
                    "parent = " + (parent == null ? null : parent.toShortString());
        }

        public String toShortString() {
            return super.toString();
        }

    }

    public static abstract class BaseViewBuilder<NView extends View> extends BaseView<NView> {
        private Float x, y;
        private ViewSize width, height;
        private Integer gravity;
        private Integer bgColor;
        private Integer bgRes;
        private Value4<Integer, Integer, Integer, Integer> paddingLTRB;
        private Value4<Integer, Integer, Integer, Integer> marginLTRB;
        private int visibility = View.VISIBLE;
        protected ResultCallback2<BaseView<NView>, View> onClickListener;
        protected boolean watchClicks = false;
        private ActionCallback2<BaseView<NView>, View, Boolean> onLongClickListener;
        protected boolean watchLongClicks = false;
        private ActionCallback3<BaseView<NView>, View, MotionEvent, Boolean> onTouchListener;
        protected boolean watchTouches = false;
        //
        private ResultCallback<NView> onBuiltCallback;

        //----------------------------------------------------------------------

        public BaseViewBuilder<NView> setX(float x) {
            this.x = x;
            if (nativeView != null) nativeView.setTranslationX(x);
            return this;
        }

        public BaseViewBuilder<NView> setY(float y) {
            this.y = y;
            if (nativeView != null) nativeView.setTranslationY(y);
            return this;
        }

        public BaseViewBuilder<NView> setWidth(@NotNull ViewSize width) {
            this.width = width;
            if (nativeView != null) nativeView.setLayoutParams(instantiateLayoutParams());
            return this;
        }

        public BaseViewBuilder<NView> setHeight(@NotNull ViewSize height) {
            this.height = height;
            if (nativeView != null) nativeView.setLayoutParams(instantiateLayoutParams());
            return this;
        }

        public BaseViewBuilder<NView> setLayoutGravity(int gravity) {
            this.gravity = gravity;
            if (nativeView != null) nativeView.setLayoutParams(instantiateLayoutParams());
            return this;
        }

        public BaseViewBuilder<NView> setBackgroundColor(int color) {
            this.bgColor = color;
            this.bgRes = null;
            if (nativeView != null) nativeView.setBackgroundColor(color);
            return this;
        }

        public BaseViewBuilder<NView> setBackgroundResource(int resId) {
            this.bgColor = null;
            this.bgRes = resId;
            if (nativeView != null) nativeView.setBackgroundResource(resId);
            return this;
        }

        public BaseViewBuilder<NView> setPaddingLTRB(
                int left,
                int top,
                int right,
                int bottom
        ) {
            this.paddingLTRB = new Value4<>(left, top, right, bottom);
            if (nativeView != null) setPadding(nativeView, paddingLTRB);
            return this;
        }

        public BaseViewBuilder<NView> setPaddingSymmetric(
                int horizontal,
                int vertical
        ) {
            this.paddingLTRB = new Value4<>(horizontal, vertical, horizontal, vertical);
            if (nativeView != null) setPadding(nativeView, paddingLTRB);
            return this;
        }

        private void setPadding(View view, Value4<Integer, Integer, Integer, Integer> paddingLTRB) {
            view.setPadding(
                    paddingLTRB.value1 != null ? paddingLTRB.value1 : view.getPaddingLeft(),
                    paddingLTRB.value2 != null ? paddingLTRB.value2 : view.getPaddingTop(),
                    paddingLTRB.value3 != null ? paddingLTRB.value3 : view.getPaddingRight(),
                    paddingLTRB.value4 != null ? paddingLTRB.value4 : view.getPaddingBottom()
            );
        }

        public BaseViewBuilder<NView> setMarginLTRB(
                int left,
                int top,
                int right,
                int bottom
        ) {
            this.marginLTRB = new Value4<>(left, top, right, bottom);
            if (nativeView != null) nativeView.setLayoutParams(instantiateLayoutParams());
            return this;
        }

        public BaseViewBuilder<NView> setMarginSymmetric(
                int horizontal,
                int vertical
        ) {
            this.marginLTRB = new Value4<>(horizontal, vertical, horizontal, vertical);
            if (nativeView != null) nativeView.setLayoutParams(instantiateLayoutParams());
            return this;
        }

        public BaseViewBuilder<NView> setVisibility(int visibility) {
            this.visibility = visibility;
            if (nativeView != null) nativeView.setVisibility(visibility);
            return this;
        }

        public BaseViewBuilder<NView> setOnClickListener(ResultCallback2<BaseView<NView>, View> onClickListener) {
            this.onClickListener = onClickListener;
            return this;
        }

        public BaseViewBuilder<NView> watchClicks() {
            this.watchClicks = true;
            return this;
        }

        public BaseViewBuilder<NView> setOnLongClickListener(ActionCallback2<BaseView<NView>, View, Boolean> onLongClickListener) {
            this.onLongClickListener = onLongClickListener;
            return this;
        }

        public BaseViewBuilder<NView> watchLongClicks() {
            this.watchLongClicks = true;
            return this;
        }

        public BaseViewBuilder<NView> setOnTouchListener(ActionCallback3<BaseView<NView>, View, MotionEvent, Boolean> onTouchListener) {
            this.onTouchListener = onTouchListener;
            return this;
        }

        public BaseViewBuilder<NView> watchTouches() {
            this.watchTouches = true;
            return this;
        }

        //----------------------------------------------------------------------

        public final BaseViewBuilder<NView> setOnBuiltCallback(ResultCallback<NView> onBuiltCallback) {
            this.onBuiltCallback = onBuiltCallback;
            return this;
        }

        //----------------------------------------------------------------------

        NView build(Context context, @NotNull Event event) {
            this.nativeView = implementNativeView(context, event);
            setupView(this.nativeView, event);
            if (onBuiltCallback != null) onBuiltCallback.invoke(this.nativeView);
            return this.nativeView;
        }

        protected abstract NView implementNativeView(Context context, @NotNull Event event);

        @SuppressLint("ClickableViewAccessibility")
        private void setupView(NView view, Event event) {
            if (this.id != null) setId(this.id);

            if (x != null) view.setTranslationX(x);
            if (y != null) view.setTranslationY(y);

            view.setLayoutParams(instantiateLayoutParams());

            if (bgColor != null) view.setBackgroundColor(bgColor);

            if (bgRes != null) view.setBackgroundResource(bgRes);

            if (paddingLTRB != null) {
                setPadding(nativeView, paddingLTRB);
            }

            view.setVisibility(visibility);

            if (!this.isEnabled) setEnabled(false);

            if (watchClicks || onClickListener != null) {
                view.setOnClickListener(v -> {
                    if (onClickListener != null) onClickListener.invoke(this, v);
                    event.onStatusChanged(this, "ClickEvent");
                });
            }

            if (watchLongClicks || onLongClickListener != null) {
                view.setOnLongClickListener(v -> {
                    boolean b = false;
                    if (onLongClickListener != null) b = onLongClickListener.invoke(this, v);
                    event.onStatusChanged(this, "LongClickEvent");
                    return b;
                });
            }

            if (watchTouches || onTouchListener != null) {
                view.setOnTouchListener((v, e) -> {
                    boolean b = false;
                    if (onTouchListener != null) b = onTouchListener.invoke(this, v, e);
                    event.onStatusChanged(this, "TouchEvent-" + e.getAction());
                    return b;
                });
            }

        }

        private LinearLayout.LayoutParams instantiateLayoutParams() {
            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            float weight = 0;

            if (
                    this.width instanceof ViewSize.Percentage &&
                            this.height instanceof ViewSize.Percentage
            ) {
                throw new IllegalArgumentException("Either width or height has a Percentage size");
            }

            if (this.width != null) {
                if (this.width instanceof ViewSize.Percentage) {
                    width = 0;
                    weight = this.width.value;
                }
                //
                else {
                    width = (int) this.width.value;
                }
            }

            if (this.height != null) {
                if (this.height instanceof ViewSize.Percentage) {
                    height = 0;
                    weight = this.height.value;
                }
                //
                else {
                    height = (int) this.height.value;
                }
            }

            LinearLayout.LayoutParams layoutParams;
            layoutParams = new LinearLayout.LayoutParams(width, height, weight);
            if (gravity != null) layoutParams.gravity = gravity;

            if (marginLTRB != null) {
                layoutParams.setMargins(
                        marginLTRB.value1 != null ? marginLTRB.value1 : 0,
                        marginLTRB.value2 != null ? marginLTRB.value2 : 0,
                        marginLTRB.value3 != null ? marginLTRB.value3 : 0,
                        marginLTRB.value4 != null ? marginLTRB.value4 : 0
                );
            }

            return layoutParams;
        }

        @Override
        protected void destroy() {
            super.destroy();

            x = null;
            y = null;
            width = null;
            height = null;
            gravity = null;
            bgColor = null;
            paddingLTRB = null;
            marginLTRB = null;

            onClickListener = null;
            onLongClickListener = null;
            onTouchListener = null;
            onBuiltCallback = null;
        }
    }

    //--- TextBase -------------------------------------
    public static abstract class TextBase<TView extends TextView> extends BaseViewBuilder<TView> {
        private final Integer textRes;
        private final CharSequence text;
        private Integer color;
        private Typeface typeface;
        private Float textSizeSp;

        public TextBase(int textRes) {
            this.textRes = textRes;
            this.text = null;
        }

        public TextBase(CharSequence text) {
            this.textRes = null;
            this.text = text;
        }

        public TextBase<TView> setColor(int color) {
            this.color = color;
            if (nativeView != null) nativeView.setTextColor(color);
            return this;
        }

        public TextBase<TView> setTypeface(@NotNull Typeface typeface) {
            this.typeface = typeface;
            if (nativeView != null) nativeView.setTypeface(typeface);
            return this;
        }

        public TextBase<TView> setTextSizeInSp(float textSizeSp) {
            this.textSizeSp = textSizeSp;
            if (nativeView != null) nativeView.setTextSize(textSizeSp);
            return this;
        }

        @Override
        protected final TView implementNativeView(Context context, @NotNull Event event) {
            TView textView = instantiateTextView(context, event);
            setupTextView(textView, event);
            return textView;
        }

        protected abstract TView instantiateTextView(Context context, @NotNull Event event);

        private void setupTextView(TView textView, Event event) {
            super.setupView(textView, event);

            if (textRes != null) textView.setText(textRes);

            if (text != null) textView.setText(text);

            if (color != null) textView.setTextColor(color);

            if (typeface != null) textView.setTypeface(typeface);

            if (textSizeSp != null) textView.setTextSize(textSizeSp);
        }

        @Override
        public Object getValue() {
            return this.nativeView.getText();
        }
    }

    public static class Text extends TextBase<TextView> {
        public Text(int textRes) {
            super(textRes);
        }

        public Text(CharSequence text) {
            super(text);
        }

        @Override
        protected TextView instantiateTextView(Context context, @NotNull Event event) {
            return new TextView(context);
        }
    }

    public static class TextField extends TextBase<EditText> {
        private final Integer hintRes;
        private final CharSequence hint;
        private Integer hintColor;
        private TextChangedListener textChangedListener;
        private boolean watchTextChanges = false;

        public TextField() {
            super("");
            setWidth(ViewSize.matchParent());

            this.hintRes = null;
            this.hint = null;
        }

        public TextField(int hintRes) {
            super("");
            setWidth(ViewSize.matchParent());

            this.hintRes = hintRes;
            this.hint = null;
        }

        public TextField(CharSequence hint) {
            super("");
            setWidth(ViewSize.matchParent());

            this.hintRes = null;
            this.hint = hint;
        }

        public TextField(CharSequence text, CharSequence hint) {
            super(text);
            setWidth(ViewSize.matchParent());

            this.hintRes = null;
            this.hint = hint;
        }

        public TextField setHintColor(int color) {
            this.hintColor = color;
            if (nativeView != null) nativeView.setHintTextColor(color);
            return this;
        }

        public TextField setTextChangedListener(TextChangedListener textChangedListener) {
            this.textChangedListener = textChangedListener;
            return this;
        }

        public TextField watchTextChanges() {
            this.watchTextChanges = true;
            return this;
        }

        @Override
        protected EditText instantiateTextView(Context context, @NotNull Event event) {
            EditText editText = new EditText(context);

            if (hintRes != null) editText.setHint(hintRes);

            if (hint != null) editText.setHint(hint);

            if (hintColor != null) editText.setHintTextColor(hintColor);

            if (watchTextChanges || textChangedListener != null) {
                editText.addTextChangedListener(new TextChangedListener() {
                    @Override
                    public void onTextChanged(String s) {
                        if (textChangedListener != null) textChangedListener.onTextChanged(s);
                        event.onStatusChanged(TextField.this, "TextChangeEvent");
                    }
                });
            }

            return editText;
        }

        @Override
        public Editable getValue() {
            return this.nativeView.getText();
        }

        @Override
        protected void destroy() {
            super.destroy();
            textChangedListener = null;
        }
    }

    public static class Button extends TextBase<android.widget.Button> {
        private Integer tintColor;

        public Button(int textRes) {
            super(textRes);
        }

        public Button(CharSequence text) {
            super(text);
        }

        public Button setTintColor(int tintColor) {
            this.tintColor = tintColor;
            if (nativeView != null) ViewCompat.setBackgroundTintList(
                    nativeView,
                    ColorStateList.valueOf(tintColor)
            );
            return this;
        }

        @Override
        public Button setColor(int color) {
            return (Button) super.setColor(color);
        }

        @Override
        protected android.widget.Button instantiateTextView(Context context, @NotNull Event event) {
            android.widget.Button button = new android.widget.Button(context);

            button.setAllCaps(false);

            if (tintColor != null) ViewCompat.setBackgroundTintList(
                    button,
                    ColorStateList.valueOf(tintColor)
            );

            return button;
        }

        @Override
        public CharSequence getValue() {
            return ((android.widget.Button) this.nativeView).getText();
        }
    }

    public static class Checkbox extends TextBase<CheckBox> {
        private Integer checkboxColor;
        private boolean isChecked = false;

        public Checkbox(int textRes) {
            super(textRes);
        }

        public Checkbox(CharSequence text) {
            super(text);
        }

        public Checkbox setChecked(boolean checked) {
            isChecked = checked;
            if (nativeView != null) nativeView.setChecked(checked);
            return this;
        }

        public Checkbox setCheckboxColor(int checkboxColor) {
            this.checkboxColor = checkboxColor;
            if (nativeView != null) CompoundButtonCompat.setButtonTintList(
                    nativeView,
                    ColorStateList.valueOf(checkboxColor)
            );
            return this;
        }

        public Checkbox watchCheckChanges() {
            this.watchClicks();
            return this;
        }

        @Override
        protected CheckBox instantiateTextView(Context context, @NotNull Event event) {
            CheckBox checkBox = new CheckBox(context);

            checkBox.setChecked(isChecked);

            if (checkboxColor != null) CompoundButtonCompat.setButtonTintList(
                    checkBox,
                    ColorStateList.valueOf(checkboxColor)
            );

            return checkBox;
        }

        @Override
        public Boolean getValue() {
            return this.nativeView.isChecked();
        }
    }

    public static class Options extends BaseViewBuilder<RadioGroup> {
        public static class OptionElement extends TextBase<RadioButton> {
            private Integer checkColor;
            private boolean isSelected = false;

            public OptionElement(int textRes) {
                super(textRes);
            }

            public OptionElement(CharSequence text) {
                super(text);
            }

            public OptionElement setCheckColor(int checkColor) {
                this.checkColor = checkColor;
                if (nativeView != null) CompoundButtonCompat.setButtonTintList(
                        nativeView,
                        ColorStateList.valueOf(checkColor)
                );
                return this;
            }

            public OptionElement setSelected(boolean selected) {
                isSelected = selected;
                if (nativeView != null) nativeView.setChecked(selected);
                return this;
            }

            @Override
            protected android.widget.RadioButton instantiateTextView(Context context, @NotNull Event event) {
                RadioButton radioButton = new RadioButton(context);

                radioButton.setChecked(isSelected);

                if (checkColor != null) CompoundButtonCompat.setButtonTintList(
                        radioButton,
                        ColorStateList.valueOf(checkColor)
                );

                return radioButton;
            }

            @Override
            public Pair<CharSequence, Boolean> getValue() {
                return new Pair<CharSequence, Boolean>(
                        this.nativeView.getText(),
                        this.nativeView.isChecked()
                );
                /*return "{" +
                        "\"text\": \"" + ((android.widget.RadioButton) this.nativeView).getText() + "\"" +
                        "\"checked\": " + ((android.widget.RadioButton) this.nativeView).isChecked() +
                        "}";*/
            }
        }

        private final List<OptionElement> options = new ArrayList<>();
        private boolean isVertical = true;
        private Integer checkColor;
        private ResultCallback2<OptionElement, Integer> onOptionSelected;
        private boolean watchOptionSelections = false;

        public Options setCheckColor(int color) {
            this.checkColor = color;
            if (nativeView != null) {
                for (OptionElement option : options) {
                    option.setCheckColor(color);
                }
            }
            return this;
        }

        public Options addOption(OptionElement option) {
            options.add(option);
            return this;
        }

        public Options setVertical(boolean vertical) {
            isVertical = vertical;
            if (nativeView != null) nativeView.setOrientation(isVertical ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
            return this;
        }

        public Options setOnOptionSelected(ResultCallback2<OptionElement, Integer> onOptionSelected) {
            this.onOptionSelected = onOptionSelected;
            return this;
        }

        public Options watchOptionSelections() {
            this.watchOptionSelections = true;
            return this;
        }

        public OptionElement getOptionAt(int index) {
            return options.get(index);
        }

        @Override
        public List<BaseView<?>> getSubViews() {
            return List.copyOf(options);
        }

        @Override
        protected RadioGroup implementNativeView(Context context, @NotNull Event event) {
            RadioGroup radioGroup = new RadioGroup(context);
            radioGroup.setOrientation(isVertical ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);

            int i = -1;
            int selectedOptionId = 0;
            for (OptionElement option : options) {
                i++;

                option.setEnabled(this.isEnabled());

                RadioButton radioButton = (RadioButton) option.build(context, event);
                option.setParent(this);

                if (option.checkColor == null) {
                    if (checkColor != null) CompoundButtonCompat.setButtonTintList(
                            radioButton,
                            ColorStateList.valueOf(checkColor)
                    );
                }

                int finalI = i;
                radioButton.setOnClickListener(v -> {
                    ((RadioButton) v).setChecked(true);
                    if (this.onClickListener != null) this.onClickListener.invoke(this, v);
                    if (this.onOptionSelected != null) this.onOptionSelected.invoke(option, finalI);
                    if (watchOptionSelections) {
                        int id = ((RadioGroup) this.nativeView).getCheckedRadioButtonId();
                        event.onStatusChanged(
                                Options.this,
                                "OptionChanged-" + id
                        );
                    }
                });

                radioGroup.addView(radioButton);

                if (option.isSelected) selectedOptionId = radioButton.getId();
            }

            if (selectedOptionId > 0) {
                radioGroup.clearCheck();
                radioGroup.check(selectedOptionId);
            }

            return radioGroup;
        }

        @Override
        public Pair<Integer, CharSequence> getValue() {
            RadioGroup radioGroup = (RadioGroup) this.nativeView;
            int id = radioGroup.getCheckedRadioButtonId();
            if (id <= 0) return null;

            int i = -1;
            while (i++ < radioGroup.getChildCount()) {
                if (radioGroup.getChildAt(i).getId() == id) {
                    break;
                }
            }

            if (i < 0) return null;

            var r = options.get(i).getValue();
            return new Pair<Integer, CharSequence>(i, r.first);
        }

        @Override
        protected void destroy() {
            super.destroy();
            onOptionSelected = null;
        }
    }

    public static class Image extends BaseViewBuilder<ImageView> {
        private final Integer imageResId;
        private final Uri imageUri;
        private final android.graphics.drawable.Drawable imageDrawable;
        private final String url;
        private ImageView.ScaleType scaleType;
        private Integer tintColor;

        public Image(int imageResId) {
            this.imageResId = imageResId;
            this.imageUri = null;
            this.imageDrawable = null;
            this.url = null;
        }

        public Image(Uri imageUri) {
            this.imageResId = null;
            this.imageUri = imageUri;
            this.imageDrawable = null;
            this.url = null;
        }

        public Image(Drawable imageDrawable) {
            this.imageResId = null;
            this.imageUri = null;
            this.imageDrawable = imageDrawable;
            this.url = null;
        }

        public Image(String url) {
            this.imageResId = null;
            this.imageUri = null;
            this.imageDrawable = null;
            this.url = url;
        }

        public Image setScaleType(ImageView.ScaleType scaleType) {
            this.scaleType = scaleType;
            if (nativeView != null) nativeView.setScaleType(scaleType);
            return this;
        }

        public Image setTintColor(int tintColor) {
            this.tintColor = tintColor;
            if (nativeView != null) nativeView.setImageTintList(
                    ColorStateList.valueOf(tintColor)
            );
            return this;
        }

        @Override
        protected ImageView implementNativeView(Context context, @NotNull Event event) {
            ImageView view = new ImageView(context);

            if (imageUri != null) view.setImageURI(imageUri);

            if (imageDrawable != null) view.setImageDrawable(imageDrawable);

            if (imageResId != null) view.setImageResource(imageResId);

            if (url != null) {
                ImageLoader.instance().load(url, view);
            }

            if (scaleType != null) view.setScaleType(scaleType);

            if (tintColor != null) view.setImageTintList(
                    ColorStateList.valueOf(tintColor)
            );

            return view;
        }

        @Override
        public Drawable getValue() {
            return ((ImageView) this.nativeView).getDrawable();
        }
    }

    public static abstract class MultiChildrenView extends BaseViewBuilder<ViewGroup> {
        private final List<BaseViewBuilder<?>> children;
        private Integer contentGravity;

        protected MultiChildrenView() {
            this(new ArrayList<>());
        }

        protected MultiChildrenView(int contentGravity) {
            this(new ArrayList<>());
            this.contentGravity = contentGravity;
        }

        protected MultiChildrenView(List<BaseViewBuilder<?>> children) {
            this.children = children != null ? children : new ArrayList<>();
        }

        protected MultiChildrenView(int contentGravity, List<BaseViewBuilder<?>> children) {
            this.children = children != null ? children : new ArrayList<>();
            this.contentGravity = contentGravity;
        }

        public MultiChildrenView addChild(BaseViewBuilder<?> child) {
            this.children.add(child);
            return this;
        }

        @Override
        public List<BaseView<?>> getSubViews() {
            return List.copyOf(children);
        }

        public BaseView<?> getSubViewAt(int index) {
            return children.get(index);
        }

        @Override
        protected ViewGroup implementNativeView(Context context, @NotNull Event event) {
            ViewGroup layout = instantiateViewGroup(context);

            for (BaseViewBuilder<?> child : children) {
                if (child == null) continue;

                View build = child.build(context, event);
                child.setParent(this);
                layout.addView(build);
            }

            return layout;
        }

        protected ViewGroup instantiateViewGroup(Context context) {
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(linearOrientation());

            if (contentGravity != null) linearLayout.setGravity(contentGravity);

            return linearLayout;
        }

        protected int linearOrientation() {
            return LinearLayout.HORIZONTAL;
        }

        @Override
        public Object getValue() {
            return this.nativeView;
        }

        @Override
        protected void destroy() {
            super.destroy();
            if (children != null) children.clear();
        }
    }

    public static class Row extends MultiChildrenView {
        public Row() {
        }

        public Row(int contentGravity) {
            super(contentGravity);
        }

        public Row(List<BaseViewBuilder<?>> children) {
            super(children);
        }

        public Row(int contentGravity, List<BaseViewBuilder<?>> children) {
            super(contentGravity, children);
        }

        @Override
        protected int linearOrientation() {
            return LinearLayout.HORIZONTAL;
        }
    }

    public static class Column extends MultiChildrenView {
        public Column() {
        }

        public Column(int contentGravity) {
            super(contentGravity);
        }

        public Column(List<BaseViewBuilder<?>> children) {
            super(children);
        }

        public Column(int contentGravity, List<BaseViewBuilder<?>> children) {
            super(contentGravity, children);
        }

        @Override
        protected int linearOrientation() {
            return LinearLayout.VERTICAL;
        }
    }

    public static class Stack extends MultiChildrenView {
        public Stack() {
        }

        public Stack(int contentGravity) {
            super(contentGravity);
        }

        public Stack(List<BaseViewBuilder<?>> children) {
            super(children);
        }

        public Stack(int contentGravity, List<BaseViewBuilder<?>> children) {
            super(contentGravity, children);
        }

        @Override
        protected ViewGroup instantiateViewGroup(Context context) {
            return new FrameLayout(context);
        }
    }

    public static class Scroll extends BaseViewBuilder<ViewGroup> {
        private final boolean isVertical;
        public final BaseView<?> child;

        public Scroll(boolean isVertical, @NotNull ViewSize size, BaseViewBuilder<?> child) {
            //if (isVertical) assert size.value > 0;
            assert !isVertical || size.value > 0;

            this.isVertical = isVertical;
            this.child = child;

            if (isVertical) setHeight(size);
            else setWidth(size);
        }

        @Override
        public List<BaseView<?>> getSubViews() {
            return List.of(child);
        }

        @Override
        protected ViewGroup implementNativeView(Context context, @NotNull Event event) {
            ViewGroup view;

            if (isVertical) {
                view = new ScrollView(context);
            }
            //
            else {
                view = new HorizontalScrollView(context);
            }

            if (child != null) {
                View build = ((BaseViewBuilder<?>) child).build(context, event);
                child.setParent(this);
                view.addView(build);
            }

            return view;
        }

        @Override
        public Object getValue() {
            return this.nativeView;
        }

        @Override
        protected void destroy() {
            super.destroy();
            if (child != null) child.destroy();
        }
    }

    public static class BasicView extends BaseViewBuilder<View> {
        public BasicView() {
        }

        public BasicView(ViewSize width, ViewSize height) {
            setWidth(width);
            setHeight(height);
        }

        @Override
        protected View implementNativeView(Context context, @NotNull Event event) {
            return new View(context);
        }

        @Override
        public Object getValue() {
            return this.nativeView;
        }
    }

}
