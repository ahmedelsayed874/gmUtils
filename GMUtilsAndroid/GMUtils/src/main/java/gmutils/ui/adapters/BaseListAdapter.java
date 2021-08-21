package gmutils.ui.adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.viewbinding.ViewBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import gmutils.ui.utils.DumbViewBinding;
import gmutils.utils.UIUtils;


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
    private ItemTouchListener<T> mItemTouchListener;

    public BaseListAdapter(List<T> mList) {
        this.mList = mList;
    }


    public void setItemTouchListener(ItemTouchListener<T> itemTouchListener) {
        this.mItemTouchListener = itemTouchListener;
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

    //----------------------------------------------------------------------------------------------

    @NotNull
    protected abstract ViewHolder<T> getViewHolder(@NotNull LayoutInflater inflater, ViewGroup container);

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder<T> holder = null;

        if (view == null) {
            holder = getViewHolder(LayoutInflater.from(parent.getContext()), parent);
            holder.listener = new ViewHolder.Listener<T>() {
                @Override
                public ItemTouchListener<T> getItemTouchListener() {
                    return BaseListAdapter.this.mItemTouchListener;
                }
            };

            view = holder.itemView;
            view.setTag(holder);

        } else {
            holder = (ViewHolder<T>) view.getTag();
        }

        holder.setValues(mList.get(position), position);

        return view;
    }

    //----------------------------------------------------------------------------------------------

    public abstract static class ViewHolder<T> implements View.OnTouchListener {
        private View itemView;
        private ViewBinding viewBinding;
        private int itemPosition;
        private T item;
        private Listener<T> listener;


        public ViewHolder(@LayoutRes int resId, @NotNull LayoutInflater inflater, ViewGroup container) {
            this(inflater.inflate(resId, container, false));
        }

        public ViewHolder(View view) {
            this.itemView = view;

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
            this.itemView = null;
            this.viewBinding = null;
            this.item = null;
            this.listener = null;
            this.onDispose();
        }

        protected abstract void onDispose();

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (this.listener != null)
                if (this.listener.getItemTouchListener() != null) {
                    this.listener.getItemTouchListener().onTouch(item, event);
                    return true;
                }
            return false;
        }

        private interface Listener<T> {
            ItemTouchListener<T> getItemTouchListener();
        }
    }

    //----------------------------------------------------------------------------------------------

    public interface ItemTouchListener<T> {
        void onTouch(T item, MotionEvent event);
    }


}
