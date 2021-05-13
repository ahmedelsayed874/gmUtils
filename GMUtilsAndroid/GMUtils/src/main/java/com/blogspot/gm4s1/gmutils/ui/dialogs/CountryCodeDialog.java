package com.blogspot.gm4s1.gmutils.ui.dialogs;

import android.content.Context;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;

import com.blogspot.gm4s1.gmutils.CountryPhoneCodes;
import com.blogspot.gm4s1.gmutils.R;
import com.blogspot.gm4s1.gmutils.ui.adapters.BaseRecyclerAdapter;
import com.blogspot.gm4s1.gmutils.listeners.ResultCallback;
import com.blogspot.gm4s1.gmutils.listeners.SearchTextChangeListener;
import com.blogspot.gm4s1.gmutils.ui.utils.ViewSource;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer with (Java/Kotlin, Swift)
 * Have experience with:
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

    private Listener mListener;

    public final CountryPhoneCodes countryPhoneCodes;

    private CountryCodeDialog(Context context, boolean hideDialCode, Listener listener) {
        super(context);
        mListener = listener;

        View view = getView();
        countryPhoneCodes = CountryPhoneCodes.getInstance();

        RecyclerView recyclerCountryCodes = view.findViewById(R.id.recycler_country_code);
        recyclerCountryCodes.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        CountryCodesAdapter adapter = new CountryCodesAdapter(recyclerCountryCodes, hideDialCode);
        adapter.add(countryPhoneCodes.sortByName().getCountryCodes(), true);
        adapter.setOnItemClickListener((adapter1, item, position) -> {
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

    @NonNull
    @Override
    protected View createView(LayoutInflater layoutInflater) {
        return layoutInflater.inflate(R.layout.dialog_country_code, null);
    }

    @Override
    protected void onDestroy() {
        mListener = null;
    }

    //----------------------------------------------------------------------------------------------

    public void setListBackground(int color) {
        RecyclerView recyclerCountryCodes = getView().findViewById(R.id.recycler_country_code);
        recyclerCountryCodes.setBackgroundColor(color);
    }

    public void setListBackgroundRes(@DrawableRes int resid) {
        RecyclerView recyclerCountryCodes = getView().findViewById(R.id.recycler_country_code);
        recyclerCountryCodes.setBackgroundResource(resid);
    }

    //----------------------------------------------------------------------------------------------

    public static class CountryCodesAdapter extends BaseRecyclerAdapter<CountryPhoneCodes.CountryCode> {
        private final boolean mHideDialCode;

        public CountryCodesAdapter(@NonNull RecyclerView recyclerView, boolean hideDialCode) {
            super(recyclerView, new ArrayList<CountryPhoneCodes.CountryCode>());
            mHideDialCode = hideDialCode;
        }

        @NonNull
        @Override
        protected ViewSource getViewSource(int viewType, @NonNull LayoutInflater inflater, ViewGroup container) {
            return new ViewSource.LayoutResource(R.layout.adapter_country_codes);
        }

        @Override
        protected ViewHolder getViewHolder(ViewBinding viewBinding, int viewType) {
            return new CViewHolder(viewBinding);
        }

        @Override
        protected void onDispose() {
        }

        private class CViewHolder extends BaseRecyclerAdapter<CountryPhoneCodes.CountryCode>.ViewHolder {
            TextView txtCode, txtName;

            public CViewHolder(ViewBinding viewBinding) {
                super(viewBinding);

                txtCode = viewBinding.getRoot().findViewById(R.id.text_code);
                txtName = viewBinding.getRoot().findViewById(R.id.text_name);
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
            }

            @Override
            protected void dispose() {
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
