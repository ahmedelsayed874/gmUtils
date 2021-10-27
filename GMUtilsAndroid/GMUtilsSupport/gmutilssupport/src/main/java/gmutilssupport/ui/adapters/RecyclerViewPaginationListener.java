package gmutilssupport.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class RecyclerViewPaginationListener extends RecyclerView.OnScrollListener {

    private Integer lY;

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (!isLoadingMoreEnabled()) return;

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int totalItemCount = layoutManager.getItemCount();
        int visibleItemCount = layoutManager.getChildCount();
        int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        int supposedLastPosition = firstVisibleItemPosition + visibleItemCount;

        if (firstVisibleItemPosition == 0 && supposedLastPosition < totalItemCount) {
            if (lY != null && lY > dy) {
                loadMore(false);
            }
        } else if (firstVisibleItemPosition > 0 && supposedLastPosition >= totalItemCount) {
            if (lY != null && dy > lY) {
                loadMore(true);
            }
        }

        lY = dy;
    }

    protected abstract boolean isLoadingMoreEnabled();

    protected abstract void loadMore(boolean toBottom);

}
