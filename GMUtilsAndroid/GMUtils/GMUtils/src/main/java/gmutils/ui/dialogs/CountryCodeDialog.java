package gmutils.ui.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import gmutils.geography.CountryPhoneCodes;
import gmutils.R;
import gmutils.listeners.ResultCallback;
import gmutils.listeners.SearchTextChangeListener;
import gmutils.ui.adapters.BaseRecyclerAdapter;
import gmutils.ui.adapters.BaseRecyclerAdapterViewHolder;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer (Java/Kotlin, Swift) also Flutter (Dart)
 * Have precedent experience with:
 *      - (C/C++, C#) languages
 *      - .NET environment
 *      - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public class CountryCodeDialog extends BaseDialog {

    public static CountryCodeDialog show(Context context, boolean hideDialCode, Listener listener) {
        CountryCodeDialog dialog = new CountryCodeDialog(context, hideDialCode, listener);
        dialog.show();
        return dialog;
    }

    //----------------------------------------------------------------------------------------------

    private final boolean hideDialCode;
    private Listener mListener;

    public final CountryPhoneCodes countryPhoneCodes;
    private final CountryCodesAdapter adapter;

    private CountryCodeDialog(Context context, boolean hideDialCode, Listener listener) {
        super(context);
        this.hideDialCode = hideDialCode;
        mListener = listener;

        View view = getView();
        countryPhoneCodes = CountryPhoneCodes.getInstance();

        RecyclerView recyclerCountryCodes = view.findViewById(R.id.recycler_country_code);
        recyclerCountryCodes.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        adapter = new CountryCodesAdapter(recyclerCountryCodes, hideDialCode);
        adapter.add(countryPhoneCodes.sortByName().getCountryCodes(), true);
        adapter.setOnItemClickListener((itemView, item, position) -> {
            if (mListener != null) mListener.onCountryPhoneCodeSelected(item);
            dismiss();
        });

        EditText txtSearch = view.findViewById(R.id.text_search);
        ResultCallback<ArrayList<CountryPhoneCodes.CountryCode>> callback = result -> {
            adapter.clear(false);
            adapter.add(result, true);
        };
        txtSearch.addTextChangedListener(SearchTextChangeListener.create(500, text -> {
            countryPhoneCodes.searchAsync(text, CountryPhoneCodes.TargetFields.all, callback);
        }));
    }

    @NotNull
    @Override
    protected View createView(LayoutInflater layoutInflater) {
        return layoutInflater.inflate(R.layout.dialog_country_code, null);
    }

    @Override
    protected void onDestroy() {
        mListener = null;
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public CountryCodeDialog setTitleColorRes(int resid) {
        return this;
    }

    @Override
    public CountryCodeDialog setTextColorRes(int resid) {
        adapter.setTextColor(resid);
        adapter.notifyDataSetChanged();
        return this;
    }

    public CountryCodeDialog setListBackground(int color) {
        RecyclerView recyclerCountryCodes = getView().findViewById(R.id.recycler_country_code);
        recyclerCountryCodes.setBackgroundColor(color);
        return this;
    }

    public CountryCodeDialog setListBackgroundRes(@DrawableRes int resid) {
        RecyclerView recyclerCountryCodes = getView().findViewById(R.id.recycler_country_code);
        recyclerCountryCodes.setBackgroundResource(resid);
        return this;
    }

    //----------------------------------------------------------------------------------------------

    @Override
    protected BaseDialog reinitialize(Context context) {
        CountryCodeDialog dialog = new CountryCodeDialog(context, this.hideDialCode, this.mListener);
        RecyclerView recyclerCountryCodes = getView().findViewById(R.id.recycler_country_code);
        if (recyclerCountryCodes != null) {
            RecyclerView recyclerCountryCodes2 = dialog.getView().findViewById(R.id.recycler_country_code);
            recyclerCountryCodes2.setBackground(recyclerCountryCodes.getBackground());
        }
        return dialog;
    }

    //----------------------------------------------------------------------------------------------

    public static class CountryCodesAdapter extends BaseRecyclerAdapter<CountryPhoneCodes.CountryCode> {
        private final boolean mHideDialCode;

        public CountryCodesAdapter(@NotNull RecyclerView recyclerView, boolean hideDialCode) {
            super(recyclerView, new ArrayList<CountryPhoneCodes.CountryCode>());
            mHideDialCode = hideDialCode;
        }

        @NonNull
        @Override
        protected BaseRecyclerAdapterViewHolder<CountryPhoneCodes.CountryCode> getViewHolder(int viewType, @NonNull LayoutInflater inflater, ViewGroup container) {
            return new CViewHolder(R.layout.adapter_country_codes, inflater, container);
        }

        @Override
        protected void onDispose() {
        }

        private int customTextColor = 0;
        public void setTextColor(int resid) {
            customTextColor = resid;
        }

        private class CViewHolder extends BaseRecyclerAdapterViewHolder<CountryPhoneCodes.CountryCode> {
            TextView txtCode, txtName;

            public CViewHolder(int resId, @NotNull LayoutInflater inflater, ViewGroup container) {
                super(resId, inflater, container);

                txtCode = findViewById(R.id.text_code);
                txtName = findViewById(R.id.text_name);
            }

            @Override
            protected void setValues(CountryPhoneCodes.CountryCode object) {
                if (mHideDialCode) {
                    txtCode.setVisibility(View.GONE);

                } else {
                    txtCode.setVisibility(View.VISIBLE);
                }

                txtCode.setText("+" + object.getDialCode());
                txtName.setText(object.getName());

                if (customTextColor != 0) {
                    txtCode.setTextColor(ContextCompat.getColor(txtCode.getContext(), customTextColor));
                    txtName.setTextColor(ContextCompat.getColor(txtCode.getContext(), customTextColor));
                }
            }

            @Override
            protected void onDispose() {
                txtCode = null;
                txtName = null;
            }
        }

    }

    //----------------------------------------------------------------------------------------------

    public interface Listener {
        void onCountryPhoneCodeSelected(CountryPhoneCodes.CountryCode countryCode);
    }
}
