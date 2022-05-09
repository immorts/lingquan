package com.sunofbeaches.taobaounion.ui.fragment;

import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.sunofbeaches.taobaounion.R;
import com.sunofbeaches.taobaounion.base.BaseFragment;
import com.sunofbeaches.taobaounion.model.domain.IBaseInfo;
import com.sunofbeaches.taobaounion.model.domain.OnSellContent;
import com.sunofbeaches.taobaounion.presenter.IOnSellPagePresenter;
import com.sunofbeaches.taobaounion.ui.adapter.OnSellPageAdapter;
import com.sunofbeaches.taobaounion.utils.PresenterManger;
import com.sunofbeaches.taobaounion.utils.SizeUtils;
import com.sunofbeaches.taobaounion.utils.TicketUtils;
import com.sunofbeaches.taobaounion.utils.ToastUtils;
import com.sunofbeaches.taobaounion.view.IOnSellPageCallback;

import butterknife.BindView;


public class OnSellFragment extends BaseFragment implements IOnSellPageCallback, OnSellPageAdapter.OnSellPageItemClickListener {

    private IOnSellPagePresenter mOnSellPagePresenter;

    @BindView(R.id.on_sell_content_list)
    public RecyclerView mContentRv;

    @BindView(R.id.on_sell_refresh_layout)
    public TwinklingRefreshLayout mTwinklingRefreshLayout;

    @BindView(R.id.fragment_bar_title_tv)
    public TextView barTitleTv;


    private OnSellPageAdapter mOnSellPageAdapter;
    private GridLayoutManager mGridLayoutManager;

    public static final int DEFAULT_SPAN = 2;

    @Override
    protected void initPresenter() {
        super.initPresenter();
        mOnSellPagePresenter = PresenterManger.getOnSellInstance();
        mOnSellPagePresenter.registerViewCallback(this);
        mOnSellPagePresenter.getOnSellContent();
    }

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_on_sell;
    }

    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_with_bar_layout,container,false);
    }

    protected void initView(View rootView) {
        barTitleTv.setText("特惠宝贝");
        mOnSellPageAdapter = new OnSellPageAdapter();
        //设置布局管理器
        mGridLayoutManager = new GridLayoutManager(getContext(),DEFAULT_SPAN);
        mContentRv.setLayoutManager(mGridLayoutManager);
        mContentRv.setAdapter(mOnSellPageAdapter);
        mContentRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = SizeUtils.dip2px(getContext(),2.5f);
                outRect.bottom = SizeUtils.dip2px(getContext(),2.5f);
                outRect.left = SizeUtils.dip2px(getContext(),2.5f);
                outRect.right = SizeUtils.dip2px(getContext(),2.5f);
            }
        });

        mTwinklingRefreshLayout.setEnableLoadmore(true);
        mTwinklingRefreshLayout.setEnableRefresh(false);
        mTwinklingRefreshLayout.setEnableOverScroll(true);



    }

    @Override
    protected void initListener() {
        super.initListener();
        mTwinklingRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                if (mOnSellPagePresenter != null) {
                    mOnSellPagePresenter.loaderMore();
                }
            }
        });
        mOnSellPageAdapter.setOnSellPageItemClickListener(this);
    }

    @Override
    protected void release() {
        super.release();
        if (mOnSellPagePresenter != null) {
            mOnSellPagePresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onError() {
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
    public void onContentLoadedSuccess(OnSellContent result) {
        //数据回来
        setUpState(State.SUCCESS);
        //TODO：更新UI
        mOnSellPageAdapter.setData(result);

    }


    @Override
    public void onMoreLoaded(OnSellContent moreResult) {
        mTwinklingRefreshLayout.finishLoadmore();
        int size = moreResult.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data().size();
        ToastUtils.showToast("加载了" + size + "个宝贝");
        //添加内容到适配器里
        mOnSellPageAdapter.onMoreLoaded(moreResult);
    }

    @Override
    public void onMoreLoadedError() {
        mTwinklingRefreshLayout.finishLoadmore();
        ToastUtils.showToast("网络异常，请稍后重试。");
    }

    @Override
    public void onMoreLoadedEmpty() {
        mTwinklingRefreshLayout.finishLoadmore();
        ToastUtils.showToast("没有更多内容。。。");
    }

    @Override
    public void onSellItemClick(IBaseInfo data) {
        TicketUtils.toTicketPage(getContext(),data);
//        //特惠列表内容被点击
//        //处理数据
//        //拿到ticketPresenter去加载数据
//        String title = data.getTitle();
//        String url = data.getCoupon_click_url();
//        if (TextUtils.isEmpty(url)) {
//            url = data.getClick_url();
//        }
//        String cover = data.getPict_url();
//        ITicketPresenter ticketPresenter = PresenterManger.getTicketInstance();
//        ticketPresenter.getTicket(title,url,cover);
//        startActivity(new Intent(getContext(), TicketActivity.class));
    }
}