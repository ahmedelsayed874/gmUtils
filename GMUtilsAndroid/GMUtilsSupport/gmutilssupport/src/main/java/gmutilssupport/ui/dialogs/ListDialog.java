package gmutilssupport.ui.dialogs;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gmutils.Animations;
import gmutils.KeypadOp;
import gmutils.listeners.SearchTextChangeListener;
import gmutilsSupport.R;


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
public class ListDialog extends BaseDialog {

    private TextView tvTitle;
    private TextView tvHint;
    private ListView lvList;
    private TextView tvNoResult;
    private TextView tvAddValue;

    private List mList;
    private CustomListAdapter mCustomAdapter;
    private Listener mListener;
    private Listener2 mListener2;
    private SearchDelegate mSearchDelegate;

    @NonNull
    @Override
    protected View createView(LayoutInflater layoutInflater) {
        return layoutInflater.inflate(R.layout.dialog_list_gm4s, null);
    }

    public ListDialog(Context context, Listener listener) {
        super(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setBackground(context.getColor(R.color.gmDialogBackground));
        } else {
            setBackground(context.getResources().getColor(R.color.gmDialogBackground));
        }

        View view = getView();
        tvTitle = view.findViewById(R.id.text_title);
        tvHint = view.findViewById(R.id.text_hint);
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
                mListener2.onNewValueInserted(ListDialog.this, etSearchToken.getText().toString());
            }

            dismiss();
        });
    }

    private SearchTextChangeListener getTextChangeListener() {
        return SearchTextChangeListener.create(500, text -> {
            text = text.toLowerCase();

            final List newList = new ArrayList();
            for (Object o : ListDialog.this.mList) {
                if (o != null) {
                    if (mSearchDelegate == null) {
                        if (o.toString().toLowerCase().contains(text)) {
                            newList.add(o);
                        }
                    } else {
                        if (mSearchDelegate.onSearchingTextChanged(ListDialog.this, text, o)) {
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
                mListener.onItemSelected(ListDialog.this, adapterView.getItemAtPosition(i));
            dismiss();
        };
    }


    private void setListAdapter(@NonNull List list, boolean isNewList) {
        if (mCustomAdapter == null) {
            ArrayAdapter adapter = new ArrayAdapter<String>(
                    lvList.getContext(),
                    android.R.layout.simple_list_item_1,
                    list);
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


    public ListDialog setTitle(@StringRes int title) {
        tvTitle.setText(title);
        return this;
    }

    public ListDialog setTitle(CharSequence title) {
        tvTitle.setText(title);
        return this;
    }

    public ListDialog setHint(@StringRes int hint) {
        tvHint.setText(hint);
        tvHint.setVisibility(View.VISIBLE);
        return this;
    }

    public ListDialog setHint(CharSequence hint) {
        tvHint.setText(hint);
        tvHint.setVisibility(View.VISIBLE);
        return this;
    }

    public ListDialog setList(List<?> list) {
        mList = list;
        return this;
    }

    public ListDialog setAdapter(CustomListAdapter adapterListener) {
        this.mCustomAdapter = adapterListener;
        if (adapterListener != null) setList(adapterListener.getList());
        else setList(null);
        return this;
    }

    public ListDialog setListener(Listener listener) {
        this.mListener = listener;
        return this;
    }

    public ListDialog setOnNewValueInsertedListener(Listener2 listener2) {
        mListener2 = listener2;
        return this;
    }

    public ListDialog setSearchDelegate(SearchDelegate searchDelegate) {
        this.mSearchDelegate = searchDelegate;
        return this;
    }

    public ListDialog show() {
        setListAdapter(mList, false);
        super.show();
        return this;
    }

    //----------------------------------------------------------------------------------------------

    @Override
    protected BaseDialog reinitialize(Context context) {
        ListDialog dialog = new ListDialog(context, this.mListener);
        dialog.setTitle(this.tvTitle.getText());
        dialog.setList(this.mList);
        dialog.setAdapter(this.mCustomAdapter);
        dialog.setListener(this.mListener);
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

    public interface Listener {
        void onItemSelected(ListDialog dialog, Object item);
    }

    public interface Listener2 {
        void onNewValueInserted(ListDialog dialog, String text);
    }

    public interface SearchDelegate {
        boolean onSearchingTextChanged(ListDialog dialog, String text, Object item);
    }

    public interface CustomListAdapter {
        List getList();

        BaseAdapter createAdapter();

        void updateAdapter(BaseAdapter adapter, List newList);
    }
}
