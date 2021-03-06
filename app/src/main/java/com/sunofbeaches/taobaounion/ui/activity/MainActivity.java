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
                LogUtils.d(MainActivity.class, "onNavigationItemSelected: ???????????????");
                switchFragment(mHomeFragment);
            } else if(item.getItemId() == R.id.selected){
                LogUtils.d(MainActivity.class, "onNavigationItemSelected: ???????????????");
                switchFragment(mSelectedFragment);
            } else if(item.getItemId() == R.id.red_packet){
                LogUtils.d(MainActivity.class, "onNavigationItemSelected: ???????????????");
                switchFragment(mRedPacketFragment);
            } else if(item.getItemId() == R.id.search){
                LogUtils.d(MainActivity.class, "onNavigationItemSelected: ???????????????");
                switchFragment(mSearchFragment);
            }
            //TODO
            return true;
        });

    }

    /**
     * ??????????????????fragment
     */
    private BaseFragment lastOneFragment = null;

    private void switchFragment(BaseFragment targetFragment){
        //???????????????fragment?????????????????????fragment????????????????????????????????????
        if (lastOneFragment == targetFragment) {
            return;
        }
        //?????????add???hide??????????????????fragment?????????
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
     * ?????????????????????
     */
    @Override
    public void switch2Search() {
        //switchFragment(mSearchFragment);
        //???????????????????????????
        //TODO:
        mNavigationView.setSelectedItemId(R.id.search);
    }
}