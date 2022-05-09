package com.lcodecore.tkrefreshlayout.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;


public class TbNestedScrollView extends NestedScrollView {
    private static final String TAG = "TbNestedScrollView";
    private int mHeaderHegiht = 529;
    private int originScroll;
    private RecyclerView mRecycleView;

    public TbNestedScrollView(@NonNull Context context) {
        super(context);
    }

    public TbNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TbNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setHeaderHegiht(int headerHeight){
        this.mHeaderHegiht = headerHeight;
    }


    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        Log.d(TAG, "dy -- >" + dy);
        if(target instanceof RecyclerView){
            this.mRecycleView = (RecyclerView) target;
        }
        if(originScroll < mHeaderHegiht){
            scrollBy(dx,dy);
            consumed[0] = dx;
            consumed[1] = dy;
        }
        super.onNestedPreScroll(target, dx, dy, consumed, type);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        this.originScroll = t;
        Log.d(TAG, "vertical -- > " + t);
        super.onScrollChanged(l, t, oldl, oldt);
    }

    /**
     * 判断子类是否已经滑动到了底部
     * @return
     */
    public boolean isInBottom() {
        if (mRecycleView != null) {
            boolean isBottom = !mRecycleView.canScrollVertically(1);
            //Log.d(TAG, "isBottom -- >" + isBottom);
            return isBottom;
        }
        return false;
    }
}
