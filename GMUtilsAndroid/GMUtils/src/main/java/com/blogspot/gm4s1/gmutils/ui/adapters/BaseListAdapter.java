package com.blogspot.gm4s1.gmutils.ui.adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.blogspot.gm4s1.gmutils.utils.UIUtils;

import java.util.List;


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

public abstract class BaseListAdapter<T> extends BaseAdapter {
    private List<T> mList;

    public BaseListAdapter(List<T> mList) {
        this.mList = mList;
    }


    public List<T> getList() {
        return mList;
    }

    public void add(T item, boolean refresh) {
        add(item, false, refresh);
    }

    public void add(T newItem, boolean addOnTop, boolean refresh) {
        if (addOnTop) this.mList.add(0, newItem);
        else this.mList.add(newItem);

        if (refresh) notifyDataSetChanged();
    }

    public void add(List<T> newItems, boolean refresh) {
        add(newItems, false, refresh);
    }

    public void add(List<T> newItems, boolean addOnTop, boolean refresh) {
        if (addOnTop) this.mList.addAll(0, newItems);
        else this.mList.addAll(newItems);
        if (refresh) notifyDataSetChanged();
    }

    public boolean remove(T newItem, boolean refresh) {
        boolean removed = this.mList.remove(newItem);
        if (removed && refresh) notifyDataSetChanged();
        return removed;
    }

    public boolean remove(List<T> newItems, boolean refresh) {
        boolean removed = this.mList.removeAll(newItems);
        if (removed && refresh) notifyDataSetChanged();
        return removed;
    }

    public void clear(boolean refresh) {
        this.mList.clear();
        if (refresh) notifyDataSetChanged();
    }

    public void replaceList(List<T> newList) {
        this.mList = newList;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder<T> holder;

        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(getLayoutResId(), parent, false);
            holder = getViewHolder(view);
            view.setTag(holder);

        } else {
            holder = (ViewHolder<T>) view.getTag();
        }

        holder.setValues(mList.get(position), position);

        return view;
    }

    protected abstract int getLayoutResId();

    protected abstract ViewHolder<T> getViewHolder(View view);

    public abstract static class ViewHolder<T> {
        private int itemPosition;
        private T item;

        public ViewHolder(View view) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                UIUtils.createInstance().setViewDetachedObserver(view, new Runnable() {
                    @Override
                    public void run() {
                        item = null;
                        onDispose();
                    }
                });
            }
        }

        private void setValues(T item, int position) {
            this.itemPosition = position;
            this.item = item;
            setValues(item);
         }

        public abstract void setValues(T item);

        public int getItemPosition() { return itemPosition; }

        protected T getItem() {
            return item;
        }

        protected abstract void onDispose();
    }
}
