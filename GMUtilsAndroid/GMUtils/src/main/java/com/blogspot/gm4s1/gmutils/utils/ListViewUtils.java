package com.blogspot.gm4s1.gmutils.utils;

import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

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
public class ListViewUtils {

    public static ListViewUtils createInstance() {
        return new ListViewUtils();
    }

    //----------------------------------------------------------------------------------------------

    public int computeTotalHeight(ListView listView) {

        ListAdapter mAdapter = listView.getAdapter();

        int totalHeight = 0;

        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);

            mView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            totalHeight += mView.getMeasuredHeight();

            Log.w("HEIGHT" + i, String.valueOf(totalHeight));
        }

        /*ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight +
                (listView.getDividerHeight() * (mAdapter.getCount() - 1)) +
                listView.getPaddingBottom() +
                listView.getPaddingTop();

        listView.setLayoutParams(params);
        listView.requestLayout();*/

        return totalHeight +
                (listView.getDividerHeight() * (mAdapter.getCount() - 1)) +
                listView.getPaddingBottom() +
                listView.getPaddingTop();
    }
}
