package gmutils.ui.dialogs;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import gmutils.Animations;
import gmutils.KeypadOp;
import gmutils.R;
import gmutils.listeners.SearchTextChangeListener;
import gmutils.ui.adapters.BaseListAdapter;


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
public class ListDialog<T> extends BaseDialog {

    private TextView tvTitle;
    private final TextView tvHint;
    private final EditText etSearchToken;
    private ListView lvList;
    private TextView tvNoResult;
    private TextView tvAddValue;

    private List<T> mList;
    private BaseListAdapter<T> mCustomAdapter;
    private Listener<T> mListener;
    private Listener2 mListener2;
    private SearchDelegate<T> mSearchDelegate;

    @NonNull
    @Override
    protected View createView(LayoutInflater layoutInflater) {
        return layoutInflater.inflate(R.layout.dialog_list_gm4s, null);
    }

    public ListDialog(Context context, Listener<T> listener) {
        super(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setBackground(context.getColor(R.color.gmDialogBackground));
        } else {
            setBackground(context.getResources().getColor(R.color.gmDialogBackground));
        }

        View view = getView();
        tvTitle = view.findViewById(R.id.text_title);
        tvHint = view.findViewById(R.id.text_hint);
        etSearchToken = view.findViewById(R.id.text_search_token);
        View ivClose = view.findViewById(R.id.image_close);
        final EditText etSearchToken = view.findViewById(R.id.text_search_token);
        lvList = view.findViewById(R.id.list);
        tvNoResult = view.findViewById(R.id.text_no_result);
        tvAddValue = view.findViewById(R.id.text_add_value);

        mListener = listener;

        ivClose.setOnClickListener(v -> {
            Animations.getInstance().scaleDown(v);
            dismiss();
        });

        etSearchToken.addTextChangedListener(getTextChangeListener());

        tvAddValue.setOnClickListener(v -> {
            if (mListener2 != null) {
                mListener2.onNewValueInserted(etSearchToken.getText().toString());
            }

            dismiss();
        });
    }

    private SearchTextChangeListener getTextChangeListener() {
        return SearchTextChangeListener.create(500, text -> {
            text = text.toLowerCase();

            final List<T> newList = new ArrayList<>();
            for (T o : ListDialog.this.mList) {
                if (o != null) {
                    if (mSearchDelegate == null) {
                        if (o.toString().toLowerCase().contains(text)) {
                            newList.add(o);
                        }
                    } else {
                        if (mSearchDelegate.onSearchingTextChanged(text, o)) {
                            newList.add(o);
                        }
                    }
                }
            }

            if (newList.size() > 0) {
                setListAdapter(newList, true);

            } else {
                lvList.setVisibility(View.GONE);
                tvNoResult.setVisibility(View.VISIBLE);

                if (mListener2 != null) {
                    tvAddValue.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private AdapterView.OnItemClickListener getOnListItemClickListener() {
        return (adapterView, view, i, l) -> {
            if (mListener != null)
                mListener.onItemSelected((T) adapterView.getItemAtPosition(i), i);
            dismiss();
        };
    }


    private void setListAdapter(@NonNull List<T> list, boolean isNewList) {
        if (mCustomAdapter == null) {

            ArrayAdapter<T> adapter = new ArrayAdapter<T>(
                    lvList.getContext(),
                    R.layout.list_dialog_adapter_simple_text,
                    list
            );
            lvList.setAdapter(adapter);
            lvList.setOnItemClickListener(getOnListItemClickListener());

        } else {
            if (isNewList) {
                mCustomAdapter.replaceList(list);
            }

            lvList.setAdapter(mCustomAdapter);
            mCustomAdapter.setOnItemClickListener((adapter, item, position) -> {
                if (mListener != null)
                    mListener.onItemSelected(item, position);
                dismiss();
            });
        }

        lvList.setVisibility(View.VISIBLE);
        tvNoResult.setVisibility(View.GONE);
        tvAddValue.setVisibility(View.GONE);
    }


    public ListDialog<T> setTitle(@StringRes int title) {
        tvTitle.setText(title);
        return this;
    }

    public ListDialog<T> setTitle(CharSequence title) {
        tvTitle.setText(title);
        return this;
    }

    @Override
    public ListDialog<T> setTitleColorRes(int resid) {
        tvTitle.setTextColor(ContextCompat.getColor(tvTitle.getContext(), resid));
        return this;
    }

    public ListDialog<T> setHint(@StringRes int hint) {
        tvHint.setText(hint);
        tvHint.setVisibility(View.VISIBLE);
        return this;
    }

    public ListDialog<T> setHint(CharSequence hint) {
        tvHint.setText(hint);
        tvHint.setVisibility(View.VISIBLE);
        return this;
    }

    @Override
    public BaseDialog setTextColorRes(int resid) {
        throw new IllegalStateException();
    }

    public ListDialog<T> setAddButtonText(CharSequence text) {
        tvAddValue.setText(text);
        return this;
    }

    public ListDialog<T> setAddButtonTextColor(int resid) {
        tvAddValue.setTextColor(ContextCompat.getColor(tvAddValue.getContext(), resid));
        return this;
    }

    public ListDialog<T> hideSearchBox() {
        etSearchToken.setVisibility(View.GONE);
        return this;
    }

    public ListDialog<T> setList(List<T> list) {
        mList = list;
        return this;
    }

    public ListDialog<T> setAdapter(BaseListAdapter<T> adapterListener) {
        this.mCustomAdapter = adapterListener;
        if (adapterListener != null) setList(adapterListener.getList());
        else setList(null);
        return this;
    }

    public ListDialog<T> setOnNewValueInsertedListener(Listener2<T> listener2) {
        mListener2 = listener2;
        return this;
    }

    public ListDialog<T> setSearchDelegate(SearchDelegate<T> searchDelegate) {
        this.mSearchDelegate = searchDelegate;
        return this;
    }

    public ListDialog<T> show() {
        setListAdapter(mList, false);
        super.show();
        return this;
    }

    @Override
    public void dismiss() {
        KeypadOp.hide(etSearchToken);
        super.dismiss();
    }

    //----------------------------------------------------------------------------------------------

    @Override
    protected BaseDialog reinitialize(Context context) {
        ListDialog dialog = new ListDialog(context, this.mListener);
        dialog.setTitle(this.tvTitle.getText());
        dialog.setList(this.mList);
        dialog.setAdapter(this.mCustomAdapter);
        dialog.mListener = this.mListener;
        dialog.setOnNewValueInsertedListener(this.mListener2);
        return dialog;
    }


    //----------------------------------------------------------------------------------------------

    public TextView getAddValueButton() {
        return tvAddValue;
    }


    //----------------------------------------------------------------------------------------------

    @Override
    protected void onDestroy() {
        KeypadOp.hide(getView().findFocus());

        tvTitle = null;
        lvList = null;
        tvNoResult = null;
        tvAddValue = null;

        mList = null;
        mCustomAdapter = null;
        mListener = null;
        mListener2 = null;
    }

    //----------------------------------------------------------------------------------------------

    public interface Listener<T> {
        void onItemSelected(T item, int position);
    }

    public interface Listener2<T> {
        void onNewValueInserted(String text);
    }

    public interface SearchDelegate<T> {
        boolean onSearchingTextChanged(String text, T item);
    }

}
