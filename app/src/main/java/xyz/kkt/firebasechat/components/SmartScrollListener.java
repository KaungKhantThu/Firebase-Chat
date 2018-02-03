package xyz.kkt.firebasechat.components;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class SmartScrollListener extends RecyclerView.OnScrollListener {

    public interface OnSmartScrollListener {
        void onListEndReached();
    }

    private int visibleItemCount, pastVisibleItems, totalItemCount, lastCompletelyVisibleItem;
    private boolean isListEndReached = false;
    private boolean reachedOnce = false;
    private int previousDy, currentDy;

    private OnSmartScrollListener mSmartScrollListener;

    public SmartScrollListener(OnSmartScrollListener smartScrollListener) {
        this.mSmartScrollListener = smartScrollListener;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        currentDy = dy;
        if (currentDy > previousDy) {
            //from top to bottom
        } else if (currentDy < previousDy) {
            //from bottom to top
            isListEndReached = false;
        }

        //get currently visible items count


        visibleItemCount = recyclerView.getChildCount();

        totalItemCount = recyclerView.getLayoutManager().getItemCount();

        pastVisibleItems = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

        lastCompletelyVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();

        previousDy = currentDy;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
        super.onScrollStateChanged(recyclerView, scrollState);
        if (scrollState == RecyclerView.SCROLL_STATE_IDLE) {
            if (totalItemCount >= 2) {
                if ((visibleItemCount + pastVisibleItems) >= totalItemCount && !isListEndReached && !reachedOnce) {
                    isListEndReached = true;
                    reachedOnce = true;
                    mSmartScrollListener.onListEndReached();
                } else if ((lastCompletelyVisibleItem == (totalItemCount - 2)) && reachedOnce) {
                    isListEndReached = false;
                    reachedOnce = false;
                }
            } else {
                /**
                 * if total item count == 1
                 */
                if ((visibleItemCount + pastVisibleItems) >= totalItemCount && !isListEndReached && !reachedOnce) {
                    isListEndReached = true;
                    reachedOnce = true;
                    mSmartScrollListener.onListEndReached();
                }
            }
        }
    }
}
