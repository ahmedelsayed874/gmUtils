package gmutils.ui.dialogs;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.util.ArrayList;
import java.util.List;

import gmutils.Animations;
import gmutils.KeypadOp;
import gmutils.R;
import gmutils.listeners.SearchTextChangeListener;


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
public class ListDialogV2<T> extends BaseDialog {

    private TextView tvTitle;
    private TextView tvHint;
    private EditText etSearchToken;
    private ListView lvList;
    private TextView tvNoResult;
    private TextView tvAddValue;

    private List<T> mList;
    private CustomListAdapter<T> mCustomAdapter;
    private Listener<T> mListener;
    private Listener2 mListener2;
    private SearchDelegate<T> mSearchDelegate;

    @NonNull
    @Override
    protected View createView(LayoutInflater layoutInflater) {
        return layoutInflater.inflate(R.layout.dialog_list_gm4s, null);
    }

    public ListDialogV2(Context context, Listener<T> listener) {
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
        lvList.setOnItemClickListener(getOnListItemClickListener());

        tvAddValue.setOnClickListener(v -> {
            if (mListener2 != null) {
                mListener2.onNewValueInserted(ListDialogV2.this, etSearchToken.getText().toString());
            }

            dismiss();
        });
    }

    private SearchTextChangeListener getTextChangeListener() {
        return SearchTextChangeListener.create(500, text -> {
            text = text.toLowerCase();

            final List<T> newList = new ArrayList<>();
            for (T o : ListDialogV2.this.mList) {
                if (o != null) {
                    if (mSearchDelegate == null) {
                        if (o.toString().toLowerCase().contains(text)) {
                            newList.add(o);
                        }
                    } else {
                        if (mSearchDelegate.onSearchingTextChanged(ListDialogV2.this, text, o)) {
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
                mListener.onItemSelected(ListDialogV2.this, (T) adapterView.getItemAtPosition(i));
            dismiss();
        };
    }


    private void setListAdapter(@NonNull List<T> list, boolean isNewList) {
        if (mCustomAdapter == null) {
            ArrayAdapter<T> adapter = new ArrayAdapter<T>(
                    lvList.getContext(),
                    android.R.layout.simple_list_item_1,
                    list
            );
            lvList.setAdapter(adapter);

        } else {
            if (!isNewList) {
                lvList.setAdapter(mCustomAdapter.createAdapter());
            } else {
                mCustomAdapter.updateAdapter((BaseAdapter) lvList.getAdapter(), list);
            }
        }

        lvList.setVisibility(View.VISIBLE);
        tvNoResult.setVisibility(View.GONE);
        tvAddValue.setVisibility(View.GONE);
    }


    public ListDialogV2<T> setTitle(@StringRes int title) {
        tvTitle.setText(title);
        return this;
    }

    public ListDialogV2<T> setTitle(CharSequence title) {
        tvTitle.setText(title);
        return this;
    }

    public ListDialogV2<T> setHint(@StringRes int hint) {
        tvHint.setText(hint);
        tvHint.setVisibility(View.VISIBLE);
        return this;
    }

    public ListDialogV2<T> setHint(CharSequence hint) {
        tvHint.setText(hint);
        tvHint.setVisibility(View.VISIBLE);
        return this;
    }

    public ListDialogV2<T> hideSearchBox() {
        etSearchToken.setVisibility(View.GONE);
        return this;
    }
    public ListDialogV2<T> setList(List<T> list) {
        mList = list;
        return this;
    }

    public ListDialogV2<T> setAdapter(CustomListAdapter<T> adapterListener) {
        this.mCustomAdapter = adapterListener;
        if (adapterListener != null) setList(adapterListener.getList());
        else setList(null);
        return this;
    }

    public ListDialogV2<T> setOnNewValueInsertedListener(Listener2<T> listener2) {
        mListener2 = listener2;
        return this;
    }

    public ListDialogV2<T> setSearchDelegate(SearchDelegate<T> searchDelegate) {
        this.mSearchDelegate = searchDelegate;
        return this;
    }

    public ListDialogV2<T> show() {
        setListAdapter(mList, false);
        super.show();
        return this;
    }

    //----------------------------------------------------------------------------------------------

    @Override
    protected BaseDialog reinitialize(Context context) {
        ListDialogV2 dialog = new ListDialogV2(context, this.mListener);
        dialog.setTitle(this.tvTitle.getText());
        dialog.setList(this.mList);
        dialog.setAdapter(this.mCustomAdapter);
        dialog.mListener = this.mListener;
        dialog.setOnNewValueInsertedListener(this.mListener2);
        return dialog;
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
        void onItemSelected(ListDialogV2<T> dialog, T item);
    }

    public interface Listener2<T> {
        void onNewValueInserted(ListDialogV2<T> dialog, String text);
    }

    public interface SearchDelegate<T> {
        boolean onSearchingTextChanged(ListDialogV2<T> dialog, String text, T item);
    }

    public interface CustomListAdapter<T> {
        List<T> getList();

        BaseAdapter createAdapter();

        void updateAdapter(BaseAdapter adapter, List<T> newList);
    }
}
