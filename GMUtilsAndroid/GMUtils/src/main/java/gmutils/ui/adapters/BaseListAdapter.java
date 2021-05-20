package gmutils.ui.adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import gmutils.ui.utils.DumbViewBinding;
import gmutils.ui.utils.ViewSource;
import gmutils.utils.UIUtils;

import org.jetbrains.annotations.NotNull;

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

    //----------------------------------------------------------------------------------------------

    @NotNull
    protected abstract ViewSource getViewSource(@NotNull LayoutInflater inflater, ViewGroup container);

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder<T> holder = null;

        if (view == null) {
            ViewSource viewSource = getViewSource(LayoutInflater.from(parent.getContext()), parent);
            assert viewSource != null;

            if (viewSource instanceof ViewSource.LayoutResource) {
                int resId = ((ViewSource.LayoutResource) viewSource).getResourceId();
                view = LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
                holder = getViewHolder(new DumbViewBinding(view));

            } else if (viewSource instanceof ViewSource.ViewBinding) {
                ViewBinding viewBinding = ((ViewSource.ViewBinding) viewSource).getViewBinding();
                view = viewBinding.getRoot();
                holder = getViewHolder(viewBinding);

            } else if (viewSource instanceof ViewSource.View) {
                view = ((ViewSource.View) viewSource).getView();
                holder = getViewHolder(new DumbViewBinding(view));
            }

                assert view != null;
            view.setTag(holder);

        } else {
            holder = (ViewHolder<T>) view.getTag();
        }

        holder.setValues(mList.get(position), position);

        return view;
    }

    protected abstract ViewHolder<T> getViewHolder(ViewBinding viewBinding);

    public abstract static class ViewHolder<T> {
        private ViewBinding viewBinding;
        private int itemPosition;
        private T item;

        public ViewHolder(ViewBinding viewBinding) {
            this.viewBinding = viewBinding;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                UIUtils.createInstance().setViewDetachedObserver(viewBinding.getRoot(), new Runnable() {
                    @Override
                    public void run() {
                        if (ViewHolder.this.viewBinding instanceof DumbViewBinding) {
                            ((DumbViewBinding) ViewHolder.this.viewBinding).dispose();
                        }
                        ViewHolder.this.viewBinding = null;
                        ViewHolder.this.item = null;
                        ViewHolder.this.onDispose();
                    }
                });
            }
        }

        public ViewBinding getViewBinding() {
            return viewBinding;
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
