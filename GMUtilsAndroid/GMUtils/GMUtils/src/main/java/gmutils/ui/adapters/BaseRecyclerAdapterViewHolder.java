package gmutils.ui.adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gmutils.listeners.SimpleWindowAttachListener;
import gmutils.ui.utils.DumbViewBinding;


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
public abstract class BaseRecyclerAdapterViewHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    private ViewBinding viewBinding;
    private T item;
    private int itemPosition;
    BaseRecyclerAdapter.ClickListener<T> mClickListener;
    BaseRecyclerAdapter.LongClickListener<T> mLongClickListener;

    public BaseRecyclerAdapterViewHolder(@LayoutRes int resId, @NotNull LayoutInflater inflater, ViewGroup container) {
        this(inflater.inflate(resId, container, false));
    }

    public BaseRecyclerAdapterViewHolder(View view) {
        super(view);

        view.setOnClickListener(this);
        view.setOnLongClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            view.getViewTreeObserver().addOnWindowAttachListener(
                    new SimpleWindowAttachListener() {
                        @Override
                        public void onWindowAttached() {
                        }

                        @Override
                        public void onWindowDetached() {
                            dispose();
                        }
                    }
            );
        }
    }

    public BaseRecyclerAdapterViewHolder(ViewBinding viewBinding) {
        this(viewBinding.getRoot());
        this.viewBinding = viewBinding;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            SimpleWindowAttachListener listener = new SimpleWindowAttachListener() {
                @Override
                public void onWindowAttached() {
                }

                @Override
                public void onWindowDetached() {
                    try {
                        if (BaseRecyclerAdapterViewHolder.this.viewBinding instanceof DumbViewBinding) {
                            ((DumbViewBinding) BaseRecyclerAdapterViewHolder.this.viewBinding).dispose();
                        }
                    } catch (Exception ignored) {
                    }

                    try {
                        BaseRecyclerAdapterViewHolder.this.viewBinding = null;
                    } catch (Exception ignored) {
                    }

                    dispose();
                }
            };

            try {
                viewBinding.getRoot().getViewTreeObserver().addOnWindowAttachListener(listener);
            } catch (Exception ignored) {
            }
        }
    }

    @Nullable
    public ViewBinding getViewBinding() {
        return viewBinding;
    }

    public <V extends View> V findViewById(@IdRes int resId) {
        return itemView.findViewById(resId);
    }

    void setValuesAndPosition(T item, int position) {
        this.item = item;
        this.itemPosition = position;
        setValues(item);
    }

    protected abstract void setValues(T item);

    public T getItem() {
        return item;
    }

    public int getItemPosition() {
        return itemPosition;
    }

    @Override
    public void onClick(View v) {
        if (mClickListener != null)
            mClickListener.onItemClicked(BaseRecyclerAdapterViewHolder.this, getItem(), itemPosition);
    }

    @Override
    public boolean onLongClick(View v) {
        if (mLongClickListener != null)
            return mLongClickListener.onItemLongClicked(BaseRecyclerAdapterViewHolder.this, getItem(), itemPosition);
        else
            return false;
    }

    private void dispose() {
        onDispose();
        mClickListener = null;
        mLongClickListener = null;
        viewBinding = null;
        item = null;
    }

    protected abstract void onDispose();
}
