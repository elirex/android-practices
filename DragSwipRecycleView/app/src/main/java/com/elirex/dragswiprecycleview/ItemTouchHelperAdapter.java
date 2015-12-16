package com.elirex.dragswiprecycleview;

/**
 * @author Sheng-Yuan Wang (2015/12/16).
 */
public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);

}
