package com.sunofbeaches.taobaounion.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sunofbeaches.taobaounion.R;
import com.sunofbeaches.taobaounion.utils.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TextFlowLayout extends ViewGroup {


    public static final int DEFAULT_SPACE = 10;
    private float mItemHorizontalSpace = DEFAULT_SPACE;
    private float mItemVerticalSpace = DEFAULT_SPACE;

    private List<String> mTextList = new ArrayList<>();
    private int mSelfWidth;
    private int mItemHeight;
    private OnFlowTextItemClickListener mItemClickListener = null;

    public int getContentSize(){
        return mTextList.size();
    }

    public TextFlowLayout(Context context) {
        super(context,null,0);
    }

    public TextFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs,0);
    }

    public TextFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //去拿到相关属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FlowTextStyle);
        mItemHorizontalSpace = ta.getDimension(R.styleable.FlowTextStyle_horizontalSpace, DEFAULT_SPACE);
        mItemVerticalSpace = ta.getDimension(R.styleable.FlowTextStyle_verticalSpace, DEFAULT_SPACE);
        ta.recycle();


    }

    public float getmItemHorizontalSpace() {
        return mItemHorizontalSpace;
    }

    public void setmItemHorizontalSpace(float mItemHorizontalSpace) {
        this.mItemHorizontalSpace = mItemHorizontalSpace;
    }

    public float getmItemVerticalSpace() {
        return mItemVerticalSpace;
    }

    public void setmItemVerticalSpace(float mItemVerticalSpace) {
        this.mItemVerticalSpace = mItemVerticalSpace;
    }

    public void setTextList(List<String> textList){
        removeAllViews();
        this.mTextList.clear();
        this.mTextList.addAll(textList);
        Collections.reverse(mTextList);

        for(String text : mTextList){
            //添加子View
            //LayoutInflater.from(getContext()).inflate(R.layout.flow_text_view,this,true);
            //等价于
            TextView item = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.flow_text_view, this, false);
            item.setText(text);
            item.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onFlowItemClick(text);
                    }
                }
            });
            addView(item);
        }

    }


    //这个是描述所有的行
    private List<List<View>> lines = new ArrayList<>();
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(getChildCount() == 0){
            return;
        }
        //这个是描述单行
        List<View> line = null;
        lines.clear();
        mSelfWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        //测量
        //测量孩子
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View itemView = getChildAt(i);
            if (itemView.getVisibility() != VISIBLE) {
                //不需要进行测量
                continue;
            }
            //测量前
            //LogUtils.d(this,"before height -- >" + itemView.getMeasuredHeight());
            measureChild(itemView,widthMeasureSpec,heightMeasureSpec);
            //测量后
            //LogUtils.d(this,"before width -- >" + itemView.getMeasuredWidth());
            if (line == null) {
                //说明当前行为空，可以添加进来
                line = createNewLine(itemView);
            } else {
                //判断是否可以继续添加
                if (canBeAdd(itemView,line)) {
                    //可以添加进去
                    line.add(itemView);
                } else {
                    //新创建一行
                    line = createNewLine(itemView);
                }
            }
        }
        mItemHeight = getChildAt(0).getMeasuredHeight();
        int selfHeight = (int) (lines.size() * mItemHeight + mItemVerticalSpace*(lines.size()+1) + 0.5f);
        //测量自己
        setMeasuredDimension(mSelfWidth,selfHeight);
    }

    private List<View> createNewLine(View itemView){
        List<View> line = new ArrayList<>();
        line.add(itemView);
        lines.add(line);
        return line;
    }

    /**
     * 判断当前行是否可以再添加新数据
     * @param itemView
     * @param line
     */
    private boolean canBeAdd(View itemView, List<View> line) {
        //所有已经添加的子view宽度相加+(line.size()+1)*mItemHorizontalSpace + itemView.getMeasureWidth()
        //条件：如果小于或等于当前控件的宽度，则可以添加，否则不能添加
        int totalWidth = itemView.getMeasuredWidth();
        for (View view : line) {
            //叠加所有已经添加控件的宽度
            totalWidth += view.getMeasuredWidth();
        }
        totalWidth += mItemHorizontalSpace * (line.size() + 1);
        LogUtils.d(this,"totalWidth -- >" + totalWidth);
        LogUtils.d(this,"mSelfWidth -- >" + mSelfWidth);
        //如果小于或等于当前控件的宽度，则可以添加，否则不能 添加
        if (totalWidth <= mSelfWidth) {
            return true;
        } else {
            return false;
        }
        
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int topOffSet = (int) mItemHorizontalSpace;
        for (List<View> views : lines) {
            //views是每一行
            int leftOffSet = (int) mItemHorizontalSpace;
            for (View view : views) {
                //每一行里的每一个item
                view.layout(leftOffSet,
                        topOffSet,
                        leftOffSet + view.getMeasuredWidth(),
                        topOffSet + view.getMeasuredHeight());
                //
                leftOffSet += view.getMeasuredWidth() + mItemHorizontalSpace;
            }
            topOffSet += mItemHeight + mItemHorizontalSpace;
        }
    }

    public void setOnFlowTextItemClickListener(OnFlowTextItemClickListener listener){
        this.mItemClickListener = listener;
    }


    public interface OnFlowTextItemClickListener{
        void onFlowItemClick(String text);
    }
}
