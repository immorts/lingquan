package com.sunofbeaches.taobaounion.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.sunofbeaches.taobaounion.R;
import com.sunofbeaches.taobaounion.base.BaseFragment;
import com.sunofbeaches.taobaounion.model.domain.Categories;
import com.sunofbeaches.taobaounion.presenter.impl.HomePresenterImpl;
import com.sunofbeaches.taobaounion.ui.activity.IMainActivity;
import com.sunofbeaches.taobaounion.ui.activity.MainActivity;
import com.sunofbeaches.taobaounion.ui.activity.ScanQrCodeActivity;
import com.sunofbeaches.taobaounion.ui.adapter.HomePagerAdapter;
import com.sunofbeaches.taobaounion.utils.LogUtils;
import com.sunofbeaches.taobaounion.utils.PresenterManger;
import com.sunofbeaches.taobaounion.view.IHomeCallback;

import butterknife.BindView;


public class HomeFragment extends BaseFragment implements IHomeCallback {

    @BindView(R.id.home_indicator)
    public TabLayout mTablayout;

    private HomePresenterImpl mHomePresenter;

    @BindView(R.id.home_pager)
    public ViewPager homePager;

    @BindView(R.id.home_search_input_box)
    public View mSearchInputBox;

    @BindView(R.id.scan_icon)
    public View scanBtn;

    private HomePagerAdapter mHomePagerAdapter;

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_home;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.d(this,"on create view...");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.d(this,"on destory view");
    }

    @Override
    protected void initView(View rootView) {
        mTablayout.setupWithViewPager(homePager);
        //???viewPager???????????????
        mHomePagerAdapter = new HomePagerAdapter(getChildFragmentManager());
        //??????Adapter
        homePager.setAdapter(mHomePagerAdapter);
    }

    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.base_home_fragment_layout,container,false);
    }

    @Override
    protected void initPresenter() {
        //??????Presenter
        mHomePresenter = (HomePresenterImpl) PresenterManger.getHomeInstance();
        mHomePresenter.registerViewCallback(this);
        mSearchInputBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //?????????????????????
                FragmentActivity activity = getActivity();
                if (activity instanceof IMainActivity) {
                    ((MainActivity) activity).switch2Search();
                }


            }
        });
    }

    @Override
    protected void initListener() {
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //????????? ????????????
                startActivity(new Intent(getContext(), ScanQrCodeActivity.class));
            }
        });
    }

    @Override
    protected void loadData() {
        setUpState(State.LOADING);
        //????????????
        mHomePresenter.getCategories();
    }

    @Override
    public void onCategoriesLoaded(Categories categories) {
        if (categories == null || categories.getData().size() == 0) {
            setUpState(State.EMPTY);
        } else {
            setUpState(State.SUCCESS);
        }
        LogUtils.d(this,"onCategoriesLoaded..");
        //????????????????????????????????????
        if (mHomePagerAdapter!=null) {
    //        homePager.setOffscreenPageLimit(categories.getData().size());
            mHomePagerAdapter.setCategories(categories);
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
    protected void release() {
        //????????????
        if (mHomePresenter != null){
            mHomePresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    protected void onRetryClick() {
        //??????????????????????????????
        //????????????????????????
        if (mHomePresenter != null) {
            mHomePresenter.getCategories();
        }
    }
}