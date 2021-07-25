package com.blogspot.gm4s.gmutileexample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import java.util.Arrays;
import java.util.List;

import gmutils.ui.adapters.BaseRecyclerAdapter;

public class RecyclerViewAdapterExample extends BaseRecyclerAdapter<String> {

    public RecyclerViewAdapterExample(RecyclerView recyclerView) {
        super(recyclerView);
    }

    /*//optional
    public RecyclerViewAdapterExample(RecyclerView recyclerView, List<String> list) {
        super(recyclerView, list);
    }*/

    @Override
    protected void onDispose() {
        //destroy any reference here
    }

    @NonNull
    @Override
    protected ViewHolder getViewHolder(int viewType, @NonNull LayoutInflater inflater, ViewGroup container) {
        return new VH(android.R.layout.simple_list_item_1, inflater, container);

        /*TextView tv = new TextView(container.getContext());
        container.addView(tv);
        return new VH(tv);*/

        //return new VH(new AdapterLayoutBinding()); //for view binding
    }

    class VH extends BaseRecyclerAdapter<String>.ViewHolder {

        TextView tv;

        public VH(int resId, @NonNull LayoutInflater inflater, ViewGroup container) {
            super(resId, inflater, container);
        }

        /*//optional
        public VH(View view) {
            super(view);
        }

        //optional
        public VH(ViewBinding viewBinding) {
            super(viewBinding);
        }*/

        private void init() {
            tv = findViewById(android.R.id.text1);
        }

        @Override
        protected void setValues(String item) {
            tv.setText(item);
        }

        @Override
        protected void dispose() {
            tv = null;
        }
    }
}


class HowToUseRecyclerViewAdapterExample {

    void onCreate(Context context) {
        RecyclerView rv = new RecyclerView(context);

        RecyclerViewAdapterExample adapter = new RecyclerViewAdapterExample(rv);
        //that's enough to use
        //if you need more

        adapter.setOnItemClickListener(new BaseRecyclerAdapter.ClickListener<String>() {
            @Override
            public void onItemClicked(BaseRecyclerAdapter<String> adapter, String item, int position) {
                System.out.println(position + ": " +item);
            }
        });

        adapter.add("Item1", true);
        adapter.add(Arrays.asList("Item2", "Item3", "Item4"), true);


    }
}