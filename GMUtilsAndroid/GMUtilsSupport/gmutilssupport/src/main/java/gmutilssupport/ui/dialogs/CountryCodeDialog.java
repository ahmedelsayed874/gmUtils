package gmutilssupport.ui.dialogs;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import gmutils.geography.CountryPhoneCodes;
import gmutils.listeners.ResultCallback;
import gmutils.listeners.SearchTextChangeListener;
import gmutilsSupport.R;
import gmutilssupport.ui.adapters.BaseRecyclerAdapter;

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

    private boolean hideDialCode;
    private Listener mListener;

    public final CountryPhoneCodes countryPhoneCodes;

    private CountryCodeDialog(Context context, boolean hideDialCode, Listener listener) {
        super(context);
        this.hideDialCode = hideDialCode;
        mListener = listener;

        View view = getView();
        countryPhoneCodes = CountryPhoneCodes.getInstance();

        RecyclerView recyclerCountryCodes = view.findViewById(gmutils.R.id.recycler_country_code);
        recyclerCountryCodes.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        CountryCodesAdapter adapter = new CountryCodesAdapter(recyclerCountryCodes, hideDialCode);
        adapter.add(countryPhoneCodes.sortByName().getCountryCodes(), true);
        adapter.setOnItemClickListener((adapter1, itemView, item, position) -> {
            if (mListener != null) mListener.onCountryPhoneCodeSelected(item);
            dismiss();
        });

        EditText txtSearch = view.findViewById(gmutils.R.id.text_search);
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
        return layoutInflater.inflate(gmutils.R.layout.dialog_country_code, null);
    }

    @Override
    protected void onDestroy() {
        mListener = null;
    }

    //----------------------------------------------------------------------------------------------

    public void setListBackground(int color) {
        RecyclerView recyclerCountryCodes = getView().findViewById(gmutils.R.id.recycler_country_code);
        recyclerCountryCodes.setBackgroundColor(color);
    }

    public void setListBackgroundRes(@DrawableRes int resid) {
        RecyclerView recyclerCountryCodes = getView().findViewById(gmutils.R.id.recycler_country_code);
        recyclerCountryCodes.setBackgroundResource(resid);
    }

    //----------------------------------------------------------------------------------------------

    @Override
    protected BaseDialog reinitialize(Context context) {
        CountryCodeDialog dialog = new CountryCodeDialog(context, this.hideDialCode, this.mListener);
        RecyclerView recyclerCountryCodes = getView().findViewById(gmutils.R.id.recycler_country_code);
        if (recyclerCountryCodes != null) {
            RecyclerView recyclerCountryCodes2 = dialog.getView().findViewById(gmutils.R.id.recycler_country_code);
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

        @NotNull
        @Override
        protected ViewHolder getViewHolder(int viewType, @NotNull LayoutInflater inflater, ViewGroup container) {
            return new CViewHolder(gmutils.R.layout.adapter_country_codes, inflater, container);
        }

        @Override
        protected void onDispose() {
        }

        private class CViewHolder extends BaseRecyclerAdapter<CountryPhoneCodes.CountryCode>.ViewHolder {
            TextView txtCode, txtName;

            public CViewHolder(int resId, @NotNull LayoutInflater inflater, ViewGroup container) {
                super(resId, inflater, container);

                txtCode = findViewById(gmutils.R.id.text_code);
                txtName = findViewById(gmutils.R.id.text_name);
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
