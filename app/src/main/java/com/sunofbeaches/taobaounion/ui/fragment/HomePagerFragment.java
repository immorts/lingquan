package com.sunofbeaches.taobaounion.ui.fragment;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.views.TbNestedScrollView;
import com.sunofbeaches.taobaounion.R;
import com.sunofbeaches.taobaounion.base.BaseFragment;
import com.sunofbeaches.taobaounion.model.domain.Categories;
import com.sunofbeaches.taobaounion.model.domain.HomePagerContent;
import com.sunofbeaches.taobaounion.model.domain.IBaseInfo;
import com.sunofbeaches.taobaounion.presenter.ICategpryPagerPresenter;
import com.sunofbeaches.taobaounion.ui.adapter.LinearItemContentAdapter;
import com.sunofbeaches.taobaounion.ui.adapter.LooperPagerAdapter;
import com.sunofbeaches.taobaounion.ui.custom.AutoLoopViewPager;
import com.sunofbeaches.taobaounion.utils.Constants;
import com.sunofbeaches.taobaounion.utils.LogUtils;
import com.sunofbeaches.taobaounion.utils.PresenterManger;
import com.sunofbeaches.taobaounion.utils.SizeUtils;
import com.sunofbeaches.taobaounion.utils.TicketUtils;
import com.sunofbeaches.taobaounion.utils.ToastUtils;
import com.sunofbeaches.taobaounion.view.ICategoryPagerCallback;

import java.util.List;

import butterknife.BindView;


public class HomePagerFragment extends BaseFragment implements ICategoryPagerCallback, LinearItemContentAdapter.OnListItemClickListener, LooperPagerAdapter.OnLooperPageItemClickListener {

    private ICategpryPagerPresenter mCategoryPagePresenter;
    private int mMaterialId;
    private LinearItemContentAdapter mContentAdapter;
    private LooperPagerAdapter mLooperPagerAdapter;

    public static HomePagerFragment newInstance(Categories.DataBean category){
        HomePagerFragment homePagerFragment = new HomePagerFragment();
        //
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_HOME_PAGER_TITLE,category.getTitle());
        bundle.putInt(Constants.KEY_HOME_PAGER_MATERIAL_ID,category.getId());
        homePagerFragment.setArguments(bundle);
        return homePagerFragment;
    }

    @BindView(R.id.home_pager_content_list)
    public RecyclerView mContentList;

    @BindView(R.id.looper_pager)
    public AutoLoopViewPager looperPager;

    @BindView(R.id.home_pager_title)
    public TextView currentCategoryTitleTV;

    @BindView(R.id.looper_point_container)
    public LinearLayout looperPointContainer;

    @BindView(R.id.home_pager_refresh)
    public TwinklingRefreshLayout twinklingRefreshLayout;

    @BindView(R.id.home_pager_parent)
    public LinearLayout homePagerParent;

    @BindView(R.id.home_pager_nested_scroller)
    public TbNestedScrollView homePagerNestedView;

    @BindView(R.id.home_pager_header_container)
    public LinearLayout homeHeaderContainer;


    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_home_pager;
    }

    @Override
    public void onResume() {
        super.onResume();
        //??????????????????????????????
        looperPager.startLoop();
    }

    @Override
    public void onPause() {
        super.onPause();
        //???????????????????????????
        looperPager.stopLoop();
    }

    @Override
    protected void initView(View rootView) {
        //?????????????????????
        mContentList.setLayoutManager(new LinearLayoutManager(getContext()));
        mContentList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = SizeUtils.dip2px(getContext(),1.5f);
                outRect.bottom = SizeUtils.dip2px(getContext(),1.5f);
            }
        });
        //???????????????
        mContentAdapter = new LinearItemContentAdapter();
        //???????????????
        mContentList.setAdapter(mContentAdapter);
        setUpState(State.SUCCESS);
        mLooperPagerAdapter = new LooperPagerAdapter();
        //????????????????????????
        looperPager.setAdapter(mLooperPagerAdapter);
        //??????Refresh????????????
        twinklingRefreshLayout.setEnableRefresh(false);
        twinklingRefreshLayout.setEnableLoadmore(true);
    }

    @Override
    protected void initListener() {
        mContentAdapter.setOnListItemClickListener(this);
        mLooperPagerAdapter.setOnLooperPageItemClickListener(this);

        homePagerParent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (homeHeaderContainer == null) {
                    return;
                }
                int headerHeight = homeHeaderContainer.getMeasuredHeight();
//                LogUtils.d(HomePagerFragment.this,"headerHeight -- >" + headerHeight);
                homePagerNestedView.setHeaderHegiht(headerHeight);
                int measuredHeight = homePagerParent.getMeasuredHeight();
               //LogUtils.d(HomePagerFragment.this,"measuredHeight --> " + measuredHeight);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mContentList.getLayoutParams();
                layoutParams.height = measuredHeight;
                mContentList.setLayoutParams(layoutParams);
                if (measuredHeight != 0) {
                    homePagerParent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        currentCategoryTitleTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int measuredHeight = mContentList.getMeasuredHeight();
                int measuredWidth = mContentList.getMeasuredWidth();
//                LogUtils.d(HomePagerFragment.this,"measuerHeight --> "+ measuredHeight);

            }
        });
        looperPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int targetPosition;
                if (mLooperPagerAdapter.getDataSize() != 0) {
                    targetPosition = position % mLooperPagerAdapter.getDataSize();
                } else {
                    targetPosition = position;
                }
                //???????????????
                updateLooperIndicator(targetPosition);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        twinklingRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                LogUtils.d(HomePagerFragment.this,"Load More......");
                //?????????????????????
                if (mCategoryPagePresenter != null) {
                    mCategoryPagePresenter.loaderMore(mMaterialId);
                }
            }
        });
    }

    /**
     * ???????????????
     * @param targetPosition
     */
    private void updateLooperIndicator(int targetPosition) {
        if (mLooperPagerAdapter.getDataSize() != 0) {
            targetPosition = targetPosition % mLooperPagerAdapter.getDataSize();
        }
        for (int i = 0; i < looperPointContainer.getChildCount(); i++) {
            View point = looperPointContainer.getChildAt(i);
            if (i == targetPosition) {
                point.setBackgroundResource(R.drawable.shape_indicator_point_selected);
            } else {
                point.setBackgroundResource(R.drawable.shape_indicator_point_normal);
            }
        }
    }

    @Override
    protected void initPresenter() {
        mCategoryPagePresenter = PresenterManger.getCategoryInstance();
        mCategoryPagePresenter.registerViewCallback(this);
    }

    @Override
    protected void loadData() {
        Bundle arguments = getArguments();
        String title = arguments.getString(Constants.KEY_HOME_PAGER_TITLE);
        mMaterialId = arguments.getInt(Constants.KEY_HOME_PAGER_MATERIAL_ID);
        //????????????
        LogUtils.d(this,"title -- >" + title);

        LogUtils.d(this,"materialId -- >" + mMaterialId);
        if (mCategoryPagePresenter != null) {
            mCategoryPagePresenter.getContentByCategoryId(mMaterialId);
        }
        if (currentCategoryTitleTV != null) {
            currentCategoryTitleTV.setText(title);
        }

    }

    @Override
    public void onContentLoaded(List<HomePagerContent.DataBean> contents) {
        //????????????????????????
        mContentAdapter.setData(contents);
        setUpState(State.SUCCESS);


    }

    @Override
    public int getCategoryId() {
        return mMaterialId;
    }

    @Override
    public void onError() {
        //????????????
        setUpState(State.ERROR);
    }

    @Override
    public void onLoading() {
        setUpState(State.LOADING);
    }


    @Override
    public void onEmpty() {
        setUpState(State.EMPTY);

    }

    @Override
    public void onLoaderMoreError() {
        ToastUtils.showToast("??????????????????????????????");
        if (twinklingRefreshLayout != null) {
            twinklingRefreshLayout.finishLoadmore();
        }
    }

    @Override
    public void onLoaderMoreEmpty() {
        ToastUtils.showToast("??????????????????");
        if (twinklingRefreshLayout != null) {
            twinklingRefreshLayout.finishLoadmore();
        }
    }

    @Override
    public void onLoaderMoreLoaded(List<HomePagerContent.DataBean> contents) {
        //??????????????????
        mContentAdapter.addData(contents);
        if (twinklingRefreshLayout != null) {
            twinklingRefreshLayout.finishLoadmore();
        }
        ToastUtils.showToast("?????????" + contents.size() + "?????????");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onLooperListLoaded(List<HomePagerContent.DataBean> contents) {
        LogUtils.d(this,"current thread ==== > + on +  " + Thread.currentThread().getName());
        mLooperPagerAdapter.setData(contents);
        //?????????????????????size????????????0????????????????????????????????????
        int dx = (Integer.MAX_VALUE / 2) % contents.size();
        int targetCenterPosition = Integer.MAX_VALUE / 2 - dx;

        LogUtils.d(this,"targetCenterPosition -- >" + targetCenterPosition);
        //??????????????????
        //TODO:????????????
        //looperPager.setCurrentItem(targetCenterPosition);
//        if (looperPager.getCurrentItem() != 0) {
//
//        }

        LogUtils.d(this,"url -- >" + contents.get(0).getPict_url());
        looperPointContainer.removeAllViews();

        //?????????
        for (int i = 0; i < contents.size(); i++) {
            View point = new View(getContext());
            int size = SizeUtils.dip2px(getContext(), 8);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size,size);
            layoutParams.leftMargin = SizeUtils.dip2px(getContext(),5);
            layoutParams.rightMargin = SizeUtils.dip2px(getContext(),5);
            point.setLayoutParams(layoutParams);
            if(i == 0){
                point.setBackgroundResource(R.drawable.shape_indicator_point_selected);
            } else {
                point.setBackgroundResource(R.drawable.shape_indicator_point_normal);
            }
            looperPointContainer.addView(point);
        }

    }

    @Override
    protected void release() {
        if (mCategoryPagePresenter != null) {
            mCategoryPagePresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onItemClick(IBaseInfo item) {
        //????????????????????????
        LogUtils.d(this,"list item click -- > " + item.getTitle());
        handleItemClick(item);
    }

    @Override
    public void OnLooperItemClick(IBaseInfo item) {
        //???????????????????????????
        LogUtils.d(this,"looper item click -- > " + item.getTitle());
        handleItemClick(item);
    }

    private void handleItemClick(IBaseInfo item) {
        TicketUtils.toTicketPage(getContext(),item);
    }
}