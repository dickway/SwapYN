package com.face.util;

import android.graphics.PointF;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

public class CepatSnapHelper extends SnapHelper {
    private static final float INVALID_DISTANCE = 1f;

    // Orientation helpers are lazily created per LayoutManager.
    @Nullable
    private OrientationHelper mVerticalHelper;
    @Nullable
    private OrientationHelper mHorizontalHelper;

    @Override
    public int[] calculateDistanceToFinalSnap(
            @NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
        int[] out = new int[2];
        if (layoutManager.canScrollHorizontally()) {
            out[0] = distanceToCenter(targetView,
                    getHorizontalHelper(layoutManager));
        } else {
            out[0] = 0;
        }

        if (layoutManager.canScrollVertically()) {
            out[1] = distanceToCenter(targetView,
                    getVerticalHelper(layoutManager));
        } else {
            out[1] = 0;
        }
        return out;
    }


    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager.canScrollVertically()) {
            return findCenterView(layoutManager, getVerticalHelper(layoutManager));
        } else if (layoutManager.canScrollHorizontally()) {
            return findCenterView(layoutManager, getHorizontalHelper(layoutManager));
        }
        return null;
    }

    private int distanceToCenter(@NonNull View targetView, OrientationHelper helper) {
        final int childCenter = helper.getDecoratedStart(targetView)
                + (helper.getDecoratedMeasurement(targetView) / 2);
        final int containerCenter = helper.getStartAfterPadding() + helper.getTotalSpace() / 2;
        return childCenter - containerCenter;
    }

    /**
     * Estimates a position to which SnapHelper will try to scroll to in response to a fling.
     *
     * @param layoutManager The {@link RecyclerView.LayoutManager} associated with the attached
     *                      {@link RecyclerView}.
     * @param helper        The {@link OrientationHelper} that is created from the LayoutManager.
     * @param velocityX     The velocity on the x axis.
     * @param velocityY     The velocity on the y axis.
     * @return The diff between the target scroll position and the current position.
     */
    private int estimateNextPositionDiffForFling(RecyclerView.LayoutManager layoutManager,
                                                 OrientationHelper helper, int velocityX, int velocityY) {
        int[] distances = calculateScrollDistance(velocityX, velocityY);
        float distancePerChild = computeDistancePerChild(layoutManager, helper);
        if (distancePerChild <= 0) {
            return 0;
        }
        int distance =
                Math.abs(distances[0]) > Math.abs(distances[1]) ? distances[0] : distances[1];
        return (int) Math.round(distance / distancePerChild);
    }

    /**
     * Return the child view that is currently closest to the center of this parent.
     *
     * @param layoutManager The {@link RecyclerView.LayoutManager} associated with the attached
     *                      {@link RecyclerView}.
     * @param helper        The relevant {@link OrientationHelper} for the attached {@link RecyclerView}.
     * @return the child view that is currently closest to the center of this parent.
     */
    @Nullable
    private View findCenterView(RecyclerView.LayoutManager layoutManager,
                                OrientationHelper helper) {
        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return null;
        }

        View closestChild = null;
        final int center = helper.getStartAfterPadding() + helper.getTotalSpace() / 2;
        int absClosest = Integer.MAX_VALUE;

        for (int i = 0; i < childCount; i++) {
            final View child = layoutManager.getChildAt(i);
            int childCenter = helper.getDecoratedStart(child)
                    + (helper.getDecoratedMeasurement(child) / 2);
            int absDistance = Math.abs(childCenter - center);

            /** if child center is closer than previous closest, set it as closest  **/
            if (absDistance < absClosest) {
                absClosest = absDistance;
                closestChild = child;
            }
        }
        return closestChild;
    }

    /**
     * Computes an average pixel value to pass a single child.
     * <p>
     * Returns a negative value if it cannot be calculated.
     *
     * @param layoutManager The {@link RecyclerView.LayoutManager} associated with the attached
     *                      {@link RecyclerView}.
     * @param helper        The relevant {@link OrientationHelper} for the attached
     *                      {@link RecyclerView.LayoutManager}.
     * @return A float value that is the average number of pixels needed to scroll by one view in
     * the relevant direction.
     */
    private float computeDistancePerChild(RecyclerView.LayoutManager layoutManager,
                                          OrientationHelper helper) {
        View minPosView = null;
        View maxPosView = null;
        int minPos = Integer.MAX_VALUE;
        int maxPos = Integer.MIN_VALUE;
        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return INVALID_DISTANCE;
        }

        for (int i = 0; i < childCount; i++) {
            View child = layoutManager.getChildAt(i);
            final int pos = layoutManager.getPosition(child);
            if (pos == RecyclerView.NO_POSITION) {
                continue;
            }
            if (pos < minPos) {
                minPos = pos;
                minPosView = child;
            }
            if (pos > maxPos) {
                maxPos = pos;
                maxPosView = child;
            }
        }
        if (minPosView == null || maxPosView == null) {
            return INVALID_DISTANCE;
        }
        int start = Math.min(helper.getDecoratedStart(minPosView),
                helper.getDecoratedStart(maxPosView));
        int end = Math.max(helper.getDecoratedEnd(minPosView),
                helper.getDecoratedEnd(maxPosView));
        int distance = end - start;
        if (distance == 0) {
            return INVALID_DISTANCE;
        }
        return 1f * distance / ((maxPos - minPos) + 1);
    }

    @NonNull
    private OrientationHelper getVerticalHelper(@NonNull RecyclerView.LayoutManager layoutManager) {
        if (mVerticalHelper == null) {
            mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager);
        }
        return mVerticalHelper;
    }

    @NonNull
    private OrientationHelper getHorizontalHelper(
            @NonNull RecyclerView.LayoutManager layoutManager) {
        if (mHorizontalHelper == null) {
            mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        }
        return mHorizontalHelper;
    }


    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX,
                                      int velocityY) {
        //判断layoutManager是否实现了RecyclerView.SmoothScroller.ScrollVectorProvider这个接口
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return RecyclerView.NO_POSITION;
        }

        final int itemCount = layoutManager.getItemCount();
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION;
        }

        //找到snapView
        final View currentView = findSnapView(layoutManager);
        if (currentView == null) {
            return RecyclerView.NO_POSITION;
        }

        final int currentPosition = layoutManager.getPosition(currentView);
        if (currentPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION;
        }

        RecyclerView.SmoothScroller.ScrollVectorProvider vectorProvider =
                (RecyclerView.SmoothScroller.ScrollVectorProvider) layoutManager;
        // 通过ScrollVectorProvider接口中的computeScrollVectorForPosition（）方法
        // 来确定layoutManager的布局方向
        PointF vectorForEnd = vectorProvider.computeScrollVectorForPosition(itemCount - 1);
        if (vectorForEnd == null) {
            return RecyclerView.NO_POSITION;
        }

        int vDeltaJump, hDeltaJump;
        if (layoutManager.canScrollHorizontally()) {
            //layoutManager是横向布局，并且内容超出一屏，canScrollHorizontally()才返回true
            //估算fling结束时相对于当前snapView位置的横向位置偏移量
            hDeltaJump = estimateNextPositionDiffForFling(layoutManager,
                    getHorizontalHelper(layoutManager), velocityX, 0);
            //vectorForEnd.x < 0代表layoutManager是反向布局的，就把偏移量取反
            if (vectorForEnd.x < 0) {
                hDeltaJump = -hDeltaJump;
            }
        } else {
            //不能横向滚动，横向位置偏移量当然就为0
            hDeltaJump = 0;
        }

        //竖向的原理同上
        if (layoutManager.canScrollVertically()) {
            vDeltaJump = estimateNextPositionDiffForFling(layoutManager,
                    getVerticalHelper(layoutManager), 0, velocityY);
            if (vectorForEnd.y < 0) {
                vDeltaJump = -vDeltaJump;
            }
        } else {
            vDeltaJump = 0;
        }

        int deltaJump;
        //计算一屏的item数
//        int deltaThreshold = layoutManager.getWidth() / getHorizontalHelper(layoutManager).getDecoratedMeasurement(currentView);
        int deltaThreshold = 2;
        if (layoutManager.canScrollHorizontally()) {
            deltaJump = layoutManager.canScrollVertically() ? vDeltaJump : hDeltaJump;
            if (deltaJump == 0) {
                return RecyclerView.NO_POSITION;
            }
            //对估算出来的位置偏移量进行阈值判断，最多只能滚动一屏的Item个数
            if (deltaJump > deltaThreshold) {
                deltaJump = deltaThreshold;
            }
            if (deltaJump < -deltaThreshold) {
                deltaJump = -deltaThreshold;
            }
            if (vectorForEnd.x < 0) {
                deltaJump = -hDeltaJump;
            }
        } else {
            deltaJump = 0;
        }

        //当前位置加上偏移位置，就得到fling结束时的位置，这个位置就是targetPosition
        int targetPos = currentPosition + deltaJump;
        if (targetPos < 0) {
            targetPos = 0;
        }
        if (targetPos >= itemCount) {
            targetPos = itemCount - 1;
        }

        return targetPos;
    }


}
