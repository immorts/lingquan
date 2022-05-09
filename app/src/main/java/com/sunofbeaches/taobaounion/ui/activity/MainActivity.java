package com.sunofbeaches.taobaounion.ui.activity;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sunofbeaches.taobaounion.R;
import com.sunofbeaches.taobaounion.base.BaseActivity;
import com.sunofbeaches.taobaounion.base.BaseFragment;
import com.sunofbeaches.taobaounion.ui.fragment.HomeFragment;
import com.sunofbeaches.taobaounion.ui.fragment.OnSellFragment;
import com.sunofbeaches.taobaounion.ui.fragment.SearchFragment;
import com.sunofbeaches.taobaounion.ui.fragment.SelectedFragment;
import com.sunofbeaches.taobaounion.utils.LogUtils;

import butterknife.BindView;
import butterknife.Unbinder;


public class MainActivity extends BaseActivity implements IMainActivity{

    private static final String TAG = "MainActivity";

    private HomeFragment mHomeFragment;
    private OnSellFragment mRedPacketFragment;
    private SelectedFragment mSelectedFragment;
    private SearchFragment mSearchFragment;

    private FragmentManager mFm;

    @BindView(R.id.main_navigation_bar)
    BottomNavigationView mNavigationView;
    private Unbinder mBind;

    @Override
    protected void initPresenter() {

    }

    @Override
    protected void initEvent() {
        initListener();
    }

    @Override
    protected void initView() {
        initFragment();
    }


    private void initFragment() {
        mHomeFragment = new HomeFragment();
        mRedPacketFragment = new OnSellFragment();
        mSelectedFragment = new SelectedFragment();
        mSearchFragment = new SearchFragment();

        mFm = getSupportFragmentManager();
        switchFragment(mHomeFragment);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    private void initListener() {
        mNavigationView.setOnNavigationItemSelectedListener(item -> {
            LogUtils.d(MainActivity.class, "title -->" + item.getTitle() + "id -->" + item.getItemId());
            if(item.getItemId() == R.id.home){
                LogUtils.d(MainActivity.class, "onNavigationItemSelected: 切换到首页");
                switchFragment(mHomeFragment);
            } else if(item.getItemId() == R.id.selected){
                LogUtils.d(MainActivity.class, "onNavigationItemSelected: 切换到精选");
                switchFragment(mSelectedFragment);
            } else if(item.getItemId() == R.id.red_packet){
                LogUtils.d(MainActivity.class, "onNavigationItemSelected: 切换到特惠");
                switchFragment(mRedPacketFragment);
            } else if(item.getItemId() == R.id.search){
                LogUtils.d(MainActivity.class, "onNavigationItemSelected: 切换到搜索");
                switchFragment(mSearchFragment);
            }
            //TODO
            return true;
        });

    }

    /**
     * 上一次显示的fragment
     */
    private BaseFragment lastOneFragment = null;

    private void switchFragment(BaseFragment targetFragment){
        //如果上一个fragment和当前要切换的fragment是同一个，那么不需要切换
        if (lastOneFragment == targetFragment) {
            return;
        }
        //修改成add和hide的方式来控制fragment的切换
        FragmentTransaction fragmentTransaction = mFm.beginTransaction();
        if (!targetFragment.isAdded()) {
            fragmentTransaction.add(R.id.main_page_container,targetFragment);
        } else {
            fragmentTransaction.show(targetFragment);
        }
        if (lastOneFragment != null) {
            fragmentTransaction.hide(lastOneFragment);
        }
        lastOneFragment = targetFragment;
        //fragmentTransaction.replace(R.id.main_page_container,targetFragment);
        fragmentTransaction.commit();
    }

    /**
     * 跳转到搜索界面
     */
    @Override
    public void switch2Search() {
        //switchFragment(mSearchFragment);
        //切换导航栏的选中项
        //TODO:
        mNavigationView.setSelectedItemId(R.id.search);
    }
}