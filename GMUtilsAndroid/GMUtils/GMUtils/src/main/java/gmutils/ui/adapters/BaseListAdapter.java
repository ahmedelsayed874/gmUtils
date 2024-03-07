package gmutils.ui.adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import gmutils.listeners.SimpleWindowAttachListener;
import gmutils.ui.utils.DumbViewBinding;
import gmutils.utils.UIUtils;


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

public abstract class BaseListAdapter<T> extends BaseAdapter {
    private List<T> mList;
    private OnItemClickListener<T> mOnClickListener;
    private OnItemLongClickListener<T> mOnLongClickListener;

    public BaseListAdapter(List<T> mList) {
        this.mList = mList;
    }

    public void setOnItemClickListener(OnItemClickListener<T> onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<T> onLongClickListener) {
        this.mOnLongClickListener = onLongClickListener;
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

    public boolean removeAt(int position, boolean refresh) {
        int s0 = this.mList.size();
        this.mList.remove(position);
        int s = this.mList.size();
        if ((s0 > s) && refresh) notifyDataSetChanged();
        return s0 > s;
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

    //----------------------------------------------------------------------------------------------

    @NotNull
    protected abstract ViewHolder<T> getViewHolder(@NotNull LayoutInflater inflater, ViewGroup container);

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (parent != null && parent.getTag() == null) {
            parent.setTag(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                setupDetachListener(parent);
            }
        }

        ViewHolder<T> holder = null;

        if (view == null) {
            holder = getViewHolder(LayoutInflater.from(parent.getContext()), parent);
            holder.mOnClickListener = mOnClickListener /*== null ?
                    null :
                    (adapter, item, position1) -> mOnClickListener.onClick(
                            BaseListAdapter.this,
                            item,
                            position1
                    )*/;
            holder.mOnLongClickListener = mOnLongClickListener /*== null ?
                    null :
                    (adapter, item, position1) -> mOnLongClickListener.onLongClick(
                            BaseListAdapter.this,
                            item,
                            position1
                    )*/;

            view = holder.itemView;
            view.setTag(holder);

        } else {
            holder = (ViewHolder<T>) view.getTag();
        }

        holder.setValues(mList.get(position), position);

        return view;
    }

    //----------------------------------------------------------------------------------------------

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void setupDetachListener(View view) {
        view.getViewTreeObserver().addOnWindowAttachListener(new SimpleWindowAttachListener() {
            @Override
            public void onWindowAttached() {
            }

            @Override
            public void onWindowDetached() {
                dispose();
            }
        });
    }

    public void dispose() {
        onDispose();

        mList = null;

        mOnClickListener = null;
        mOnLongClickListener = null;
    }

    protected abstract void onDispose();

    //----------------------------------------------------------------------------------------------

    public abstract static class ViewHolder<T> implements View.OnClickListener, View.OnLongClickListener {
        private View itemView;
        private ViewBinding viewBinding;
        private int itemPosition;
        private T item;
        private OnItemClickListener<T> mOnClickListener;
        private OnItemLongClickListener<T> mOnLongClickListener;

        public ViewHolder(@LayoutRes int resId, @NotNull LayoutInflater inflater, ViewGroup container) {
            this(inflater.inflate(resId, container, false));
        }

        public ViewHolder(View view) {
            this.itemView = view;
            if (view != null) {
                view.setOnClickListener(this);
                view.setOnLongClickListener(this);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                UIUtils.createInstance().setViewDetachedObserver(view, new Runnable() {
                    @Override
                    public void run() {
                        ViewHolder.this.dispose();
                    }
                });
            }
        }

        public ViewHolder(ViewBinding viewBinding) {
            this(viewBinding.getRoot());
            this.viewBinding = viewBinding;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                UIUtils.createInstance().setViewDetachedObserver(viewBinding.getRoot(), new Runnable() {
                    @Override
                    public void run() {
                        if (ViewHolder.this.viewBinding instanceof DumbViewBinding) {
                            ((DumbViewBinding) ViewHolder.this.viewBinding).dispose();
                        }

                        ViewHolder.this.viewBinding = null;
                    }
                });
            }
        }

        public ViewBinding getViewBinding() {
            return viewBinding;
        }

        public <V extends View> V findViewById(@IdRes int resId) {
            return itemView.findViewById(resId);
        }


        private void setValues(T item, int position) {
            this.itemPosition = position;
            this.item = item;
            setValues(item);
        }

        public abstract void setValues(T item);


        public int getItemPosition() {
            return itemPosition;
        }

        protected T getItem() {
            return item;
        }

        private void dispose() {
            this.onDispose();
            if (this.itemView != null) {
                this.itemView.setOnClickListener(null);
                this.itemView.setOnLongClickListener(null);
            }
            this.itemView = null;
            this.viewBinding = null;
            this.item = null;
            this.mOnClickListener = null;
            this.mOnLongClickListener = null;
        }

        protected abstract void onDispose();

        @Override
        public void onClick(View v) {
            if (mOnClickListener != null)
                mOnClickListener.onClick(null, item, itemPosition);
        }

        @Override
        public boolean onLongClick(View v) {
            if (mOnLongClickListener != null)
                mOnLongClickListener.onLongClick(null, item, itemPosition);
            return mOnLongClickListener != null;
        }
    }

    public interface OnItemClickListener<T> {
        void onClick(ViewHolder<T> viewHolder, T item, int position);
    }

    public interface OnItemLongClickListener<T> {
        void onLongClick(ViewHolder<T> viewHolder, T item, int position);
    }
}
